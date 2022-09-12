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