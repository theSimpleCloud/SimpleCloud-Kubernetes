/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.node.update

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.internal.service.InternalCloudStateService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.utils.CloudState
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import app.simplecloud.simplecloud.module.api.error.configuration.ErrorCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.module.api.service.ErrorService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

/**
 * Date: 27.12.22
 * Time: 19:09
 * @author Frederick Baier
 *
 */
class NodeUpdater(
    private val moduleLinksToInstall: List<String>,
    private val baseImageName: String,
    private val remoteBuildkitAddr: String,
    private val destImageTag: String,
    private val stateService: InternalCloudStateService,
    private val ftpServerService: InternalFtpServerService,
    private val processService: CloudProcessService,
    private val errorService: ErrorService,
    private val kubeAPI: KubeAPI,
) {

    private val WORK_DIR = "/home/work/"
    private val MODULES_DIR_IN_CONTAINER = "/node/modules/"

    private val podService = this.kubeAPI.getPodService()
    private val deploymentService = this.kubeAPI.getDeploymentService()

    fun canPerformUpdate(): Boolean {
        return this.stateService.getCloudState().get() != CloudState.DISABLED
    }

    @Synchronized
    fun executeUpdate() {
        logger.info("Updating Cloud...")
        NodeDisabler(
            this.stateService,
            this.ftpServerService,
            this.processService
        ).disableNodes()
        executeUpdate0()
    }

    private fun executeUpdate0() {
        deleteOldUpdaterPod()
        val rebuildFuture = rebuildNode()
        rebuildFuture.thenAccept { handeRebuildCompleted(it) }
    }

    private fun deleteOldUpdaterPod() {
        try {
            val pod = this.podService.getPod("updater")
            pod.delete()
        } catch (e: NoSuchElementException) {
            //ignore because pod doesn't exist
        }
    }

    private fun handeRebuildCompleted(buildResult: BuildResult) {
        when (buildResult) {
            is BuildSuccess -> handleBuildSuccess()
            is BuildFailure -> handleBuildFailure(buildResult)
        }
    }

    private fun handleBuildFailure(buildResult: BuildFailure) {
        logger.error("Rebuild failed")
        this.errorService.createCreateRequest(
            ErrorCreateConfiguration(
                -1,
                "Rebuild failed: Update pod failed",
                buildResult.logString,
                "Cloud",
                emptyMap()
            )
        ).submit()
    }

    private fun handleBuildSuccess() {
        updateSelfDeployment()
    }

    private fun updateSelfDeployment() {
        try {
            updateSelfDeployment0()
        } catch (e: Exception) {
            handleSelfDeploymentUpdateError(e)
        }
    }

    private fun handleSelfDeploymentUpdateError(e: Exception) {
        logger.error("Error updating deployment:", e)
        val stacktrace = ExceptionUtils.getStackTrace(e)
        this.errorService.createCreateRequest(
            ErrorCreateConfiguration(
                -1,
                "Rebuild failed: Failed to update deployment",
                stacktrace,
                "Cloud",
                emptyMap()
            )
        ).submit()
    }

    private fun updateSelfDeployment0() {
        logger.info("Rebuild complete...")
        val selfDeployment = this.deploymentService.getDeployment("simplecloud")
        selfDeployment.editImage(this.destImageTag)
    }

    private fun rebuildNode(): CompletableFuture<BuildResult> {
        val pod = startUpdatePod()
        return CloudCompletableFuture.supplyAsync {
            while (pod.isActive()) {
                Thread.sleep(500)
            }
            val isFailed = pod.isFailed()
            val logString = pod.getLogs()
            return@supplyAsync if (isFailed) BuildFailure(logString) else BuildSuccess()
        }

    }

    private fun startUpdatePod(): KubePod {
        val podSpec = createPodSpec()
        return this.podService.createPod("updater", podSpec)
    }

    private fun createPodSpec(): PodSpec {
        val command = createCommand()
        return PodSpec()
            .withImage("moby/buildkit:v0.9.0")
            .withRestartPolicy("Never")
            .withMaxMemory(256)
            .withCommand(
                PodSpec.KubeCommand(listOf("/bin/sh", "-c"), listOf(command))
            )
    }

    private fun createCommand(): String {
        return listOf("mkdir $WORK_DIR")
            .union(createModuleDownloadCommands())
            .union(
                listOf(
                    createDockerfileCommand(),
                    createDebugCommand(),
                    createBuildCommand()
                )
            )
            .toList().joinToString(" && ")
    }

    private fun createDebugCommand(): String {
        return "ls $WORK_DIR && cat ${WORK_DIR + "Dockerfile"}"
    }

    private fun createBuildCommand(): String {
        return "buildctl --addr tcp://${remoteBuildkitAddr}:1234 build --frontend=dockerfile.v0 --local context=${WORK_DIR} --local dockerfile=${WORK_DIR} --output type=image,name=${destImageTag},push=true,registry.insecure=true"
    }

    private fun createModuleDownloadCommands(): List<String> {
        return this.moduleLinksToInstall.map { crateDownloadCommand(it) }
    }

    private fun crateDownloadCommand(link: String): String {
        val jarName = getJarNameFromLink(link)
        return "wget $link -O ${WORK_DIR + jarName}"
    }

    private fun createDockerfileCommand(): String {
        val lines = createDockerfileLines()
        return "echo \"${lines.joinToString("\n")}\" > ${WORK_DIR + "Dockerfile"}"
    }

    private fun createDockerfileLines(): Collection<String> {
        val moduleCopyCommands = creatModuleDockerfileCommands()
        return listOf(
            "FROM $baseImageName",
            "WORKDIR /node/",
            "EXPOSE 8008"
        ).union(moduleCopyCommands)
            .union(listOf("CMD [ \\\"java\\\", \\\"-jar\\\", \\\"/node.jar\\\" ]")) // escape \ and escape "
    }

    private fun creatModuleDockerfileCommands(): List<String> {
        return this.moduleLinksToInstall.map { createModuleDockerfileCommand(it) }
    }

    private fun createModuleDockerfileCommand(link: String): String {
        val jarName = getJarNameFromLink(link)
        return "COPY $jarName ${MODULES_DIR_IN_CONTAINER + jarName}"
    }

    private fun getJarNameFromLink(link: String): String {
        return link.split("/").last()
    }

    sealed interface BuildResult

    class BuildSuccess : BuildResult
    class BuildFailure(val logString: String) : BuildResult

    companion object {
        private val logger =
            LogManager.getLogger(NodeUpdater::class.java)
    }

}