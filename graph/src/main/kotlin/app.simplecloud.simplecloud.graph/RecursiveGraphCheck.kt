package app.simplecloud.simplecloud.graph

import app.simplecloud.simplecloud.graph.path.GraphNodePathFinder

/**
 * Created by IntelliJ IDEA.
 * Date: 30.04.2021
 * Time: 10:32
 * @author Frederick Baier
 */
class RecursiveGraphCheck<T>(
    private val graph: Graph<T>,
    private val valueToAdd: T,
    private val valueSuccessors: List<T>,
) {

    private val nodeFromValueToAdd = graph.getNodeByValue(valueToAdd)

    fun checkForRecursion() {
        //if the [valueToAdd] is not registered than no other node can have it as a successor,
        // because every successor is a node too
        checkForSelfRecursion()
        if (!isNodeRegistered(this.valueToAdd)) return
        checkRecursion()
    }

    private fun isNodeRegistered(valueToAdd: T): Boolean {
        return this.graph.getNodeByValue(valueToAdd) != null
    }

    private fun checkRecursion() {
        val alreadyRegisteredSuccessors = valueSuccessors.mapNotNull { graph.getNodeByValue(it) }

        for (successor in alreadyRegisteredSuccessors) {
            val path = GraphNodePathFinder(this.graph, successor, nodeFromValueToAdd!!).findPath()
            if (!path.isEmpty()) {
                throw IllegalArgumentException("Recursion detected in Node '${valueToAdd}' (path: '${path.map { it.nodeValue }}')")
            }
        }

    }

    private fun checkForSelfRecursion() {
        if (valueSuccessors.contains(valueToAdd))
            throw IllegalArgumentException("Recursion detected: '${valueToAdd.toString()}' has itself as successor")
    }

}