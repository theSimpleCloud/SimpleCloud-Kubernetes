/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.node.startup.task.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.*
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.mongodb.MongoClientURI
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.voidFuture
import eu.thesimplecloud.simplecloud.restserver.setup.body.MongoSetupResponseBody
import eu.thesimplecloud.simplecloud.task.Task
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture

/**
 * Creates the mongodb container and returns its connection string
 */
class MongoDockerContainerCreateTask(
    private val responseBody: MongoSetupResponseBody
) : Task<String>() {

    override fun getName(): String {
        return "create_mongo_container"
    }

    private val dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build()
    private val dockerClient = DockerClientBuilder.getInstance(dockerConfig).build()

    override fun run(): CompletableFuture<String> {
        val networkId = getNetworkIdOrCreate(dockerClient)
        val ownContainerId = getOwnContainerId()

        joinContainerInNetwork(ownContainerId, networkId)
        pullImage("docker.io/bitnami/mongodb", "latest")

        val mongoClientURI = createMongoClientURI()
        val mongoContainerId = createMongoContainer(mongoClientURI)
        joinContainerInNetwork(mongoContainerId, networkId)
        this.dockerClient.startContainerCmd(mongoContainerId).exec()
        return completedFuture(
            "mongodb://${mongoClientURI.username}:${String(mongoClientURI.password!!)}@" +
                    "${MONGODB_CONTAINER_NAME}:27017/${mongoClientURI.database}"
        )
    }

    private fun createMongoContainer(mongoClientURI: MongoClientURI): String {
        val createContainerCmd = dockerClient.createContainerCmd("bitnami/mongodb:latest")
            .withName(MONGODB_CONTAINER_NAME)
            .withHostName(MONGODB_CONTAINER_NAME)
            .withHostConfig(
                HostConfig.newHostConfig()
                    .withPortBindings(PortBinding.parse("27017:27017"))
            )
            .withVolumes(Volume("/bitnami/mongodb"))
            .withEnv(
                "MONGODB_USERNAME=${mongoClientURI.username}",
                "MONGODB_PASSWORD=${String(mongoClientURI.password!!)}",
                "MONGODB_DATABASE=${mongoClientURI.database}"
            )

        return createContainerCmd.exec().id
    }

    private fun createMongoClientURI(): MongoClientURI {
        return MongoClientURI(this.responseBody.connectionString)
    }

    private fun getNetworkIdOrCreate(dockerClient: DockerClient): String {
        val network = getNetwork()
        if (network != null) return network.id

        return dockerClient.createNetworkCmd()
            .withName("simplecloud_network")
            .exec().id
    }

    private fun getNetwork(): Network? {
        return this.dockerClient.listNetworksCmd().exec().firstOrNull { it.name == "simplecloud_network" }
    }

    private fun getOwnContainerId(): String {
        val process = Runtime.getRuntime().exec("cat /proc/self/cgroup")
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        val lines = bufferedReader.readLines()
        return getContainerIdFromLine(lines[2])
    }

    private fun getContainerIdFromLine(line: String): String {
        val length = line.length
        return line.substring(length - 64)
    }


    private fun joinContainerInNetwork(containerId: String, networkId: String) {
        this.dockerClient.connectToNetworkCmd()
            .withContainerId(containerId)
            .withNetworkId(networkId)
            .exec()
    }

    private fun pullImage(repository: String, tag: String) {
        val pullImageCmd = dockerClient.pullImageCmd(repository)
            .withTag(tag)
        pullImageCmd.start().awaitCompletion()
    }

    companion object {
        private val MONGODB_CONTAINER_NAME = "mongodb_simplecloud"
    }

}
