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

package eu.thesimplecloud.simplecloud.node.task

import com.ea.async.Async.await
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Injector
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.node.util.SelfNodeGetter
import eu.thesimplecloud.simplecloud.api.impl.util.SimpleCloudFileContent
import eu.thesimplecloud.simplecloud.api.impl.util.ClusterKey
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.task.Task
import java.io.File
import java.util.concurrent.CompletableFuture

class SimpleCloudFileCreationTask(
    tmpDir: File,
    private val process: ICloudProcess,
    private val injector: Injector
) : Task<Unit>() {

    private val simpleCloudFile = File(tmpDir, "SIMPLE-CLOUD.json")
    private val objectMapper = ObjectMapper()

    override fun getName(): String {
        return "create_simplecloud_file"
    }

    override fun run(): CompletableFuture<Unit> {
        val selfNode = await(getSelfNode())
        val simpleCloudFileContent = SimpleCloudFileContent(
            selfNode.getAddress(),
            this.process.getName(),
            this.process.getAddress(),
            getClusterKey()
        )
        this.objectMapper.writeValue(this.simpleCloudFile, simpleCloudFileContent)
        return unitFuture()
    }

    private fun getSelfNode(): CompletableFuture<INode> {
        val selfNodeGetter = this.injector.getInstance(SelfNodeGetter::class.java)
        return selfNodeGetter.getSelfNode()
    }

    private fun getClusterKey(): ClusterKey {
        return this.injector.getInstance(ClusterKey::class.java)
    }

}