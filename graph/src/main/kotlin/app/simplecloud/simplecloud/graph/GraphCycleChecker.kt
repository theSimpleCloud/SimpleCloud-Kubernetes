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

import app.simplecloud.simplecloud.graph.path.GraphNodePathFinder

/**
 * Created by IntelliJ IDEA.
 * Date: 30.04.2021
 * Time: 10:32
 * @author Frederick Baier
 */
class GraphCycleChecker<T>(
    private val graph: Graph<T>,
    private val valueToAdd: T,
    private val valueSuccessors: Collection<T>,
) {

    private val nodeFromValueToAdd = graph.getNodeByValue(valueToAdd)

    fun checkForCycle() {
        //if the [valueToAdd] is not registered than no other node can have it as a successor,
        // because every successor is a node too
        checkForSelfCycle()
        if (!isNodeRegistered(this.valueToAdd)) return
        checkCycle()
    }

    private fun isNodeRegistered(valueToAdd: T): Boolean {
        return this.graph.getNodeByValue(valueToAdd) != null
    }

    private fun checkCycle() {
        val alreadyRegisteredSuccessors = valueSuccessors.mapNotNull { graph.getNodeByValue(it) }

        for (successor in alreadyRegisteredSuccessors) {
            val path = GraphNodePathFinder(this.graph, successor, nodeFromValueToAdd!!).findPath()
            if (!path.isEmpty()) {
                throw IllegalArgumentException("Cycle detected in Node '${valueToAdd}' (path: '${path.map { it.nodeValue }}')")
            }
        }

    }

    private fun checkForSelfCycle() {
        if (valueSuccessors.contains(valueToAdd))
            throw IllegalArgumentException("Cycle detected: '${valueToAdd.toString()}' has itself as successor")
    }

}