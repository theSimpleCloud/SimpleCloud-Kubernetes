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

package eu.thesimplecloud.simplecloud.node.startup

import com.google.inject.Injector
import com.google.inject.Key
import dev.morphia.Datastore
import eu.thesimplecloud.simplecloud.api.utils.Address
import eu.thesimplecloud.simplecloud.container.ContainerSpec
import eu.thesimplecloud.simplecloud.container.IContainer
import eu.thesimplecloud.simplecloud.container.IImage
import eu.thesimplecloud.simplecloud.container.ImageBuildInstructions
import eu.thesimplecloud.simplecloud.node.annotation.NodeBindAddress
import eu.thesimplecloud.simplecloud.node.annotation.NodeMaxMemory
import eu.thesimplecloud.simplecloud.node.annotation.NodeName
import eu.thesimplecloud.simplecloud.node.connect.NodeClusterConnectTask
import eu.thesimplecloud.simplecloud.node.startup.task.NodeStartupTask
import eu.thesimplecloud.simplecloud.storagebackend.IStorageBackend
import eu.thesimplecloud.simplecloud.task.TaskExecutorService
import eu.thesimplecloud.simplecloud.task.TaskExecutorServiceImpl
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture


/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 10:23
 * @author Frederick Baier
 */
class NodeStartup(
    private val startArguments: NodeStartArgumentParserMain
) {

    private val taskExecutorService: TaskExecutorService = TaskExecutorServiceImpl("DEFAULT")
    private val taskSubmitter = taskExecutorService.createSubmitter("STARTUP")

    fun start() {
        this.taskSubmitter.submit(NodeStartupTask(this.startArguments)).thenAccept {
            executeClusterConnect(it)
        }
    }

    private fun executeClusterConnect(injector: Injector) {
        try {
            executeClusterConnect0(injector)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun executeClusterConnect0(injector: Injector) {
        val task = NodeClusterConnectTask(
            injector,
            injector.getInstance(Datastore::class.java),
            injector.getInstance(Key.get(String::class.java, NodeName::class.java)),
            injector.getInstance(Key.get(Address::class.java, NodeBindAddress::class.java)),
            injector.getInstance(Key.get(Int::class.java, NodeMaxMemory::class.java)),
        )
        this.taskSubmitter.submit(task)
    }

    fun executeImageBuild(injector: Injector) {
        println("after inject")
        val imageFactory = injector.getInstance(IImage.Factory::class.java)
        val containerFactory = injector.getInstance(IContainer.Factory::class.java)
        val buildDir = File("./buildDirTemp")
        FileUtils.copyDirectory(File("/mnt/ssd/1.16-Server"), buildDir)

        val image = imageFactory.create("test",
            buildDir,
            ImageBuildInstructions()
                .from("adoptopenjdk:16.0.1_9-jdk-hotspot")
                .copy(".", "/home/myserver/")
                .expose(25565)
                .workdir("/home/myserver/")
                .cmd("java", "-jar", "/home/myserver/Paper-1.16.5.jar")
        )
        println("image created")
        val container = containerFactory.create(
            "mc-server",
            image,
            ContainerSpec()
                .withPortBinding(25565, 25565)
                .withMaxMemory(1500)
        )
        println("container created")

        container.start()
        println("container start")
    }

}