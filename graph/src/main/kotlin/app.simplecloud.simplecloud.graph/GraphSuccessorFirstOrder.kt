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