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

package app.simplecloud.simplecloud.graph

import java.util.concurrent.ConcurrentHashMap

class Graph<T>(
    private val allowCycles: Boolean,
) {

    private val allNodes = ConcurrentHashMap.newKeySet<Node<T>>()

    fun setNodeWithSuccessors(nodeValue: T, successors: Collection<T>) {
        if (!allowCycles)
            GraphCycleChecker(this, nodeValue, successors).checkForCycle()

        val successorNodes = successors.map { getOrCreateNodeByValue(it) }
        val node = getOrCreateNodeByValue(nodeValue)
        node.addSuccessors(successorNodes)
    }

    internal fun getNodeByValue(nodeValue: T): Node<T>? {
        return this.allNodes.firstOrNull { it.nodeValue == nodeValue }
    }

    private fun getOrCreateNodeByValue(nodeValue: T): Node<T> {
        val nodeByValue = getNodeByValue(nodeValue)
        if (nodeByValue != null) {
            return nodeByValue
        }
        return createNode(nodeValue)
    }

    private fun createNode(nodeValue: T): Node<T> {
        val node = Node(nodeValue)
        this.allNodes.add(node)
        return node
    }

    internal fun getNodesPredecessors(node: Node<T>): List<Node<T>> {
        return this.allNodes.filter { it.getSuccessors().contains(node) }
    }

    internal fun getNodes(): Set<Node<T>> {
        return allNodes
    }

    fun getNodeValues(): Collection<T> {
        return allNodes.map { it.nodeValue }
    }

    internal fun getEntryPointNodes(): List<Node<T>> {
        return allNodes.filter { it.getSuccessors().isEmpty() }
    }

    fun getEntryPointValues(): List<T> {
        return getEntryPointNodes().map { it.nodeValue }
    }

    /**
     * Returns all values ordered in a way that every successor of a node will be listed before it
     */
    fun getAllOrderedBySuccessorFirst(): List<T> {
        return GraphSuccessorFirstOrder(this).order().map { it.nodeValue }
    }

}
