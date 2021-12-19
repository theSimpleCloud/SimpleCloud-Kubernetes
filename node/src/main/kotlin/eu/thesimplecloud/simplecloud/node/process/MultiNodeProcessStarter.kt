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

package eu.thesimplecloud.simplecloud.node.process

import com.google.inject.Injector
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import eu.thesimplecloud.simplecloud.api.node.Node
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import eu.thesimplecloud.simplecloud.api.service.NodeService
import eu.thesimplecloud.simplecloud.node.task.NodeToStartProcessSelectionTask
import java.util.concurrent.CompletableFuture

class MultiNodeProcessStarter(
    private val nodeService: NodeService,
    private val configuration: ProcessStartConfiguration,
    private val injector: Injector
) {

    private val messageChannelManager = this.injector.getInstance(MessageChannelManager::class.java)

    fun startProcess(): CompletableFuture<CloudProcess> {
        val node = selectNodeForProcess()
        return startProcessOnNode(node)
    }

    private fun selectNodeForProcess(): Node {
        return NodeToStartProcessSelectionTask(
            this.configuration.maxMemory,
            this.nodeService
        ).run().join()
    }

    private fun startProcessOnNode(node: Node): CompletableFuture<CloudProcess> {
        val messageChannel =
            this.messageChannelManager.getMessageChannelByName<ProcessStartConfiguration, CloudProcess>("start_process")!!
        return messageChannel.createMessageRequest(this.configuration, node).submit()
    }

}