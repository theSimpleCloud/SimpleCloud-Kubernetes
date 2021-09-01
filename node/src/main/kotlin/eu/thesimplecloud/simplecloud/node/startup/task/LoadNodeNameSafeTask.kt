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

package eu.thesimplecloud.simplecloud.node.startup.task

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.node.startup.NodeStartupSetupHandler
import eu.thesimplecloud.simplecloud.node.startup.setup.task.NodeNameSetupTask
import eu.thesimplecloud.simplecloud.task.Task
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

class LoadNodeNameSafeTask(
    private val nodeSetupHandler: NodeStartupSetupHandler,
    private val useRandomNodeName: Boolean
) : Task<String>() {

    override fun getName(): String {
        return "load_node_name_safe"
    }

    override fun run(): CompletableFuture<String> {
        if (!NODE_NAME_FILE.exists()) {
            await(determineNodeNameAndSafeToFile())
        }
        val nodeName = NODE_NAME_FILE.readLines().first()
        return completedFuture(nodeName)
    }

    private fun determineNodeNameAndSafeToFile(): CompletableFuture<Unit> {
        val nodeName = await(determineNodNameToUse())
        saveNodeNameToFile(nodeName)
        return unitFuture()
    }

    private fun determineNodNameToUse(): CompletableFuture<String> {
        if (this.useRandomNodeName) {
            return completedFuture(generateRandomNodeName())
        }
        return executeSetup()
    }

    private fun executeSetup(): CompletableFuture<String> {
        return this.nodeSetupHandler.executeSetupTask(this.taskSubmitter) {
            NodeNameSetupTask(it)
        }
    }

    private fun generateRandomNodeName(): String {
        return "Node-${UUID.randomUUID()}"
    }

    private fun saveNodeNameToFile(nodeName: String) {
        NODE_NAME_FILE.writeText(nodeName)
    }

    companion object {
        val NODE_NAME_FILE = File("node_name.txt")
    }

}