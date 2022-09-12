package app.simplecloud.simplecloud.graph

import java.util.concurrent.ConcurrentHashMap

class Graph<T>(
    private val allowRecursion: Boolean,
) {

    private val allNodes = ConcurrentHashMap.newKeySet<Node<T>>()

    fun setNodeWithSuccessors(nodeValue: T, successors: List<T>) {
        if (!allowRecursion)
            RecursiveGraphCheck(this, nodeValue, successors).checkForRecursion()

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
