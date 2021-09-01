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

import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.node.util.Downloader
import eu.thesimplecloud.simplecloud.task.Task
import org.tinylog.Logger
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 09/08/2021
 * Time: 22:45
 * @author Frederick Baier
 */
class DockerInstallTask : Task<Unit>() {

    override fun getName(): String {
        return "docker_install"
    }

    override fun run(): CompletableFuture<Unit> {
        Logger.warn("The cloud is about to INSTALL DOCKER", "")
        Logger.warn("If you want to abort press Ctrl+C. Waiting 20 seconds.", "")
        Thread.sleep(20_000)
        Downloader.userAgentDownload("https://get.docker.com/", File("install-docker.sh"))
        executeAndWatchLog("chmod 777 install-docker.sh")
        executeAndWatchLog("./install-docker.sh")
        return unitFuture()
    }

    private fun executeAndWatchLog(command: String) {
        val process = Runtime.getRuntime().exec(command)
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        while (process.isAlive) {
            val log = bufferedReader.readLine()
            Logger.info("[Docker] {}", log)
        }
    }

}