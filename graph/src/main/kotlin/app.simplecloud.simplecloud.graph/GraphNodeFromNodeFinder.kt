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