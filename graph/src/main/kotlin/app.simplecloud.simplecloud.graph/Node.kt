package app.simplecloud.simplecloud.graph

import java.util.concurrent.ConcurrentHashMap

class Node<T>(
    val nodeValue: T,
    private val successors: MutableSet<Node<T>> = ConcurrentHashMap.newKeySet(),
) {


    fun getSuccessors(): Set<Node<T>> {
        return this.successors
    }

    fun addSuccessor(node: Node<T>) {
        this.successors.add(node)
    }

    fun addSuccessors(nodes: Collection<Node<T>>) {
        this.successors.addAll(nodes)
    }

}
