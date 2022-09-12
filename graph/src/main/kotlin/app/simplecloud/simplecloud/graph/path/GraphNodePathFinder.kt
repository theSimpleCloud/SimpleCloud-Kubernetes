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

package app.simplecloud.simplecloud.graph.path

import app.simplecloud.simplecloud.graph.Graph
import app.simplecloud.simplecloud.graph.Node
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 30.04.2021
 * Time: 11:23
 * @author Frederick Baier
 */
class GraphNodePathFinder<T>(
    private val graph: Graph<T>,
    private val startNode: Node<T>,
    private val nodeToSearch: Node<T>,
) {

    private val stack = Stack<Node<T>>()
    private var wasNodeFound = false

    fun findPath(): Collection<Node<T>> {
        if (!isNodeRegistered(nodeToSearch.nodeValue)) return emptyList()
        if (!isNodeRegistered(startNode.nodeValue)) return emptyList()
        checkNodeAndSuccessorsForNodeToSearch(this.startNode)
        return this.stack
    }

    private fun checkNodeAndSuccessorsForNodeToSearch(node: Node<T>) {
        stack.push(node)
        checkIfNodeIsNodeToSearch(node)
        if (!wasNodeFound)
            searchInSuccessorsFromNode(node)
    }

    private fun checkIfNodeIsNodeToSearch(node: Node<T>) {
        if (node == this.nodeToSearch) {
            wasNodeFound = true
        }
    }

    private fun searchInSuccessorsFromNode(node: Node<T>) {
        val successorsIterator = node.getSuccessors().iterator()
        while (successorsIterator.hasNext() && !wasNodeFound) {
            checkNodeAndSuccessorsForNodeToSearch(successorsIterator.next())
        }
        removeItemFromStackIfTheNodeWasNotFound()
    }

    private fun removeItemFromStackIfTheNodeWasNotFound() {
        if (!wasNodeFound) {
            stack.pop()
        }
    }

    private fun isNodeRegistered(valueToAdd: T): Boolean {
        return this.graph.getNodeByValue(valueToAdd) != null
    }

}