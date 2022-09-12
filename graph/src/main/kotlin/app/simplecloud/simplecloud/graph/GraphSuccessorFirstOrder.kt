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
 * Date: 29.04.2021
 * Time: 19:19
 * @author Frederick Baier
 *
 * Orders all values of a graph in a way that successors will come first
 *
 */
class GraphSuccessorFirstOrder<T>(
    private val graph: Graph<T>,
) {

    fun order(): Collection<Node<T>> {
        val entryPointNodes = graph.getEntryPointNodes()
        val otherNodes = entryPointNodes.map { getAllPredecessorNodes(it) }.flatten()
        return entryPointNodes.union(otherNodes)
    }

    private fun getAllPredecessorNodes(node: Node<T>): Collection<Node<T>> {
        val predecessors = graph.getNodesPredecessors(node)
        val subPredecessors = predecessors.map { getAllPredecessorNodes(it) }.flatten()
        return predecessors.union(subPredecessors)
    }

}