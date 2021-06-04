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

package eu.thesimplecloud.simplecloud.container.local

import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 21:39
 * @author Frederick Baier
 */
class LocalContainerExecutor(
    private val startCommand: String,
    private val stopCommand: String,
    private val image: LocalImage,
    private val workingDir: File
) {

    @Volatile
    private var process: Process? = null

    @Volatile
    private var localContainerLogsReader: LocalContainerLogsReader? = null

    @Synchronized
    fun startContainer() {
        check(!isContainerRunning()) { "Cannot start a running container" }

        startContainer0()
    }

    private fun startContainer0() {
        prepareProcess().thenAccept {
            startProcess()
            initLogsReader()
        }
    }

    private fun initLogsReader() {
        this.localContainerLogsReader = LocalContainerLogsReader(this.process!!)
    }

    private fun startProcess() {
        val processBuilder = ProcessBuilder()
            .command(*startCommand.split(" ").toTypedArray())
            .directory(this.workingDir)
        this.process = processBuilder.start()
    }

    private fun prepareProcess(): CompletableFuture<Void> {
        return image.build()
            .thenAccept { image.copyBuildImageTo(this.workingDir) }
    }

    fun shutdownContainer(): CompletableFuture<Void> {
        executeCommand(this.stopCommand)
        return this.terminationFuture()
    }

    fun terminationFuture(): CompletableFuture<Void> {
        if (!this.isContainerRunning()) return CompletableFuture.completedFuture(null)
        return this.process!!.onExit().thenAccept { }
    }

    fun isContainerRunning(): Boolean {
        return this.process?.isAlive == true
    }

    fun executeCommand(command: String) {
        check(isContainerRunning()) { "Process must be alive to execute a command" }

        val command = command + "\n"
        val process = this.process
        if (process != null && process.outputStream != null) {
            process.outputStream?.write(command.toByteArray())
            process.outputStream?.flush()
        }
    }

    fun forceShutdown() {
        this.process?.destroyForcibly()
    }

    fun getLogs(): List<String> {
        return this.localContainerLogsReader?.getLogs() ?: emptyList()
    }

}