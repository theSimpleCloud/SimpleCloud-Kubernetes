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

package app.simplecloud.simplecloud.api.impl.service

import app.simplecloud.simplecloud.api.impl.node.NodeImpl
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteNodeRepository
import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.service.NodeService
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 11.06.2021
 * Time: 19:32
 * @author Frederick Baier
 */
open class DefaultNodeService(
    private val igniteRepository: IgniteNodeRepository
) : NodeService {

    override fun findAll(): CompletableFuture<List<Node>> {
        val completableFuture = this.igniteRepository.findAll()
        return completableFuture.thenApply { list -> list.map { NodeImpl(it) } }
    }

    override fun findByUniqueId(uniqueId: UUID): CompletableFuture<Node> {
        val completableFuture = this.igniteRepository.find(uniqueId)
        return completableFuture.thenApply { NodeImpl(it) }
    }

    override fun findFirst(): CompletableFuture<Node> {
        val future = this.igniteRepository.findFirst()
        return future.thenApply { NodeImpl(it) }
    }

}