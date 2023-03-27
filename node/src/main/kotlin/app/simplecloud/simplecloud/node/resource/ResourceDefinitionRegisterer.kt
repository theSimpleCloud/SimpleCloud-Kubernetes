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

package app.simplecloud.simplecloud.node.resource

import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.CloudStateService
import app.simplecloud.simplecloud.api.service.StaticProcessTemplateService
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePodService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import app.simplecloud.simplecloud.module.api.impl.ftp.start.FtpServerStarter
import app.simplecloud.simplecloud.module.api.impl.ftp.stop.FtpServerStopper
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinitionService
import app.simplecloud.simplecloud.node.connect.DistributedRepositories
import app.simplecloud.simplecloud.node.process.ProcessShutdownHandler
import app.simplecloud.simplecloud.node.process.ProcessStarter
import app.simplecloud.simplecloud.node.resource.error.V1Beta1ErrorPrePostProcessor
import app.simplecloud.simplecloud.node.resource.error.V1Beta1ErrorResourceSpec
import app.simplecloud.simplecloud.node.resource.ftp.V1Beta1FtpPrePostProcessor
import app.simplecloud.simplecloud.node.resource.ftp.V1Beta1FtpSpec
import app.simplecloud.simplecloud.node.resource.group.*
import app.simplecloud.simplecloud.node.resource.onlinestrategy.V1Beta1OnlineCountStrategyPrePostProcessor
import app.simplecloud.simplecloud.node.resource.onlinestrategy.V1Beta1ProcessOnlineCountStrategySpec
import app.simplecloud.simplecloud.node.resource.permissiongroup.V1Beta1PermissionGroupPrePostProcessor
import app.simplecloud.simplecloud.node.resource.permissiongroup.V1Beta1PermissionGroupSpec
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerPrePostProcessor
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerSpec
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerStatus
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerStatusGeneration
import app.simplecloud.simplecloud.node.resource.process.V1Beta1CloudProcessPrePostProcessor
import app.simplecloud.simplecloud.node.resource.process.V1Beta1CloudProcessSpec
import app.simplecloud.simplecloud.node.resource.process.V1Beta1CloudProcessStatus
import app.simplecloud.simplecloud.node.resource.process.V1Beta1CloudProcessStatusGeneration
import app.simplecloud.simplecloud.node.resource.process.execute.V1Beta1CloudProcessExecuteBody
import app.simplecloud.simplecloud.node.resource.process.execute.V1Beta1CloudProcessExecuteHandler
import app.simplecloud.simplecloud.node.resource.staticserver.*

/**
 * Date: 07.03.23
 * Time: 12:53
 * @author Frederick Baier
 *
 */
class ResourceDefinitionRegisterer(
    private val resourceDefinitionService: ResourceDefinitionService,
    private val cloudStateService: CloudStateService,
    private val groupService: CloudProcessGroupService,
    private val staticService: StaticProcessTemplateService,
    private val processService: CloudProcessService,
    private val ftpService: InternalFtpServerService,
    private val processStarterFactory: ProcessStarter.Factory,
    private val processShutdownHandlerFactory: ProcessShutdownHandler.Factory,
    private val ftpServerStarter: FtpServerStarter,
    private val ftpServerStopper: FtpServerStopper,
    private val podService: KubePodService,
    private val volumeClaimService: KubeVolumeClaimService,
    private val distributedRepositories: DistributedRepositories,
) {

    fun registerDefinitions() {
        registerLobbyGroupDefinition()
        registerProxyGroupDefinition()
        registerServerGroupDefinition()

        registerStaticLobbyDefinition()
        registerStaticProxyDefinition()
        registerStaticServerDefinition()

        registerPermissionGroupDefinition()
        registerOnlineCountStrategyDefinition()

        registerCloudPlayerDefinition()
        registerProcessDefinition()
        registerErrorDefinition()
        registerFtpServerDefinition()
    }

    private fun registerFtpServerDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("FtpServer")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1FtpSpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1FtpPrePostProcessor(
                this.cloudStateService,
                this.ftpService,
                this.volumeClaimService,
                this.ftpServerStarter,
                this.ftpServerStopper
            )
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerErrorDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("Error")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1ErrorResourceSpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1ErrorPrePostProcessor(
                distributedRepositories.errorRepository
            )
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerProcessDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("CloudProcess")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1CloudProcessSpec::class.java)
        v1VersionBuilder.setStatusSchemaClass(V1Beta1CloudProcessStatus::class.java)
        v1VersionBuilder.setStatusGenerationFunction(V1Beta1CloudProcessStatusGeneration(this.distributedRepositories.cloudProcessRepository))
        v1VersionBuilder.setPreProcessor(
            V1Beta1CloudProcessPrePostProcessor(
                this.groupService,
                this.staticService,
                this.processService,
                this.processStarterFactory,
                this.processShutdownHandlerFactory
            )
        )

        v1VersionBuilder.setActions(
            v1VersionBuilder.newActionsBuilder()
                .setCreateActionName("start")
                .setDeleteActionName("stop")
                .disableUpdate()
                .registerCustomAction(
                    "execute",
                    V1Beta1CloudProcessExecuteBody::class.java,
                    V1Beta1CloudProcessExecuteHandler(this.processService, this.podService)
                ).build()
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerCloudPlayerDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("CloudPlayer")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1CloudPlayerSpec::class.java)
        v1VersionBuilder.setStatusSchemaClass(V1Beta1CloudPlayerStatus::class.java)
        v1VersionBuilder.setStatusGenerationFunction(V1Beta1CloudPlayerStatusGeneration(this.distributedRepositories.cloudPlayerRepository))
        v1VersionBuilder.setPreProcessor(
            V1Beta1CloudPlayerPrePostProcessor(this.distributedRepositories.cloudPlayerRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerOnlineCountStrategyDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("ProcessOnlineCountStrategy")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1ProcessOnlineCountStrategySpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1OnlineCountStrategyPrePostProcessor(this.distributedRepositories.distributedOnlineCountStrategyRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerPermissionGroupDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("PermissionGroup")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1PermissionGroupSpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1PermissionGroupPrePostProcessor(this.distributedRepositories.permissionGroupRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerStaticLobbyDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("StaticLobby")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1StaticLobbySpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1StaticLobbyPrePostProcessor(this.distributedRepositories.staticProcessTemplateRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerStaticProxyDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("StaticProxy")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1StaticProxySpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1StaticProxyPrePostProcessor(this.distributedRepositories.staticProcessTemplateRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerStaticServerDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("StaticServer")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1StaticServerSpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1StaticServerPrePostProcessor(this.distributedRepositories.staticProcessTemplateRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerLobbyGroupDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("LobbyGroup")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1LobbyGroupSpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1LobbyGroupPrePostProcessor(this.distributedRepositories.cloudProcessGroupRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerProxyGroupDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("ProxyGroup")

        val v1ProxyVersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1ProxyVersionBuilder.setName("v1beta1")
        v1ProxyVersionBuilder.setSpecSchemaClass(V1Beta1ProxyGroupSpec::class.java)
        v1ProxyVersionBuilder.setPreProcessor(
            V1Beta1ProxyGroupPrePostProcessor(this.distributedRepositories.cloudProcessGroupRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1ProxyVersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerServerGroupDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("ServerGroup")

        val v1ServerVersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1ServerVersionBuilder.setName("v1beta1")
        v1ServerVersionBuilder.setSpecSchemaClass(V1Beta1ServerGroupSpec::class.java)
        v1ServerVersionBuilder.setPreProcessor(
            V1Beta1ServerGroupPrePostProcessor(this.distributedRepositories.cloudProcessGroupRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1ServerVersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

}