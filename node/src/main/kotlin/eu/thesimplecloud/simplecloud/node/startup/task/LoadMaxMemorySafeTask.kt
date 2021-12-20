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
import com.fasterxml.jackson.databind.ObjectMapper
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.node.startup.NodeStartupSetupHandler
import eu.thesimplecloud.simplecloud.node.startup.setup.task.MaxMemorySetupTask
import eu.thesimplecloud.simplecloud.node.util.Logger
import eu.thesimplecloud.simplecloud.restserver.setup.body.NodeMaxMemoryResponseBody
import java.io.File
import java.util.concurrent.CompletableFuture

class LoadMaxMemorySafeTask(
    private val nodeSetupHandler: NodeStartupSetupHandler,
    private val maxMemoryArgument: Int?
) {

    private val objectMapper = ObjectMapper()


    fun run(): CompletableFuture<Int> {
        Logger.info("Loading Max Memory")
        if (this.maxMemoryArgument != null)
            return completedFuture(maxMemoryArgument)
        return loadMaxMemory()
    }

    private fun loadMaxMemory(): CompletableFuture<Int> {
        if (MAX_MEMORY_FILE.exists()) {
            return loadMaxMemoryFromFile()
        }
        return executeSetupAndSafeMaxMemoryToFile()
    }

    private fun executeSetupAndSafeMaxMemoryToFile(): CompletableFuture<Int> {
        val maxMemory = executeSetupUntilValid()
        saveMaxMemoryToFile(maxMemory)
        return completedFuture(maxMemory)
    }

    private fun executeSetupUntilValid(): Int {
        val maxMemory = await(executeSetup()).maxMemory
        if (maxMemory < 2048)
            return executeSetupUntilValid()
        return maxMemory
    }

    private fun saveMaxMemoryToFile(int: Int) {
        this.objectMapper.writeValue(MAX_MEMORY_FILE, int)
    }

    private fun executeSetup(): CompletableFuture<NodeMaxMemoryResponseBody> {
        return this.nodeSetupHandler.executeSetupTask() {
            MaxMemorySetupTask(it).run()
        }
    }

    private fun loadMaxMemoryFromFile(): CompletableFuture<Int> {
        return completedFuture(
            this.objectMapper.readValue(MAX_MEMORY_FILE, Int::class.java)
        )
    }

    companion object {
        val MAX_MEMORY_FILE = File("maxMemory.txt")
    }

}