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

/**
 * Created by IntelliJ IDEA.
 * Date: 30.04.2021
 * Time: 10:39
 * @author Frederick Baier
 */
class GraphNodeFromNodeFinder<T>(
    private val nodeToSearch: Node<T>,
    private val fromNode: Node<T>,
) {

    private val searchedNodes = ArrayList<Node<T>>()

    fun findNode(): Node<T>? {
        return searchInNode(this.fromNode)
    }

    private fun searchInNode(node: Node<T>): Node<T>? {
        if (wasNodeAlreadySearched(node)) return null
        this.searchedNodes.add(node)
        return searchInNode0(node)
    }

    private fun searchInNode0(node: Node<T>): Node<T>? {
        if (node == nodeToSearch) return node
        return searchInSuccessors(node)
    }

    private fun searchInSuccessors(node: Node<T>): Node<T>? {
        val successors = node.getSuccessors()
        for (successor in successors) {
            val resultNode = searchInNode(successor) ?: continue
            return resultNode
        }
        return null
    }

    private fun wasNodeAlreadySearched(node: Node<T>): Boolean {
        return this.searchedNodes.contains(node)
    }

}