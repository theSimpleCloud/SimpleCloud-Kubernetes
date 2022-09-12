package app.simplecloud.simplecloud.graph

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 28.04.2021
 * Time: 19:32
 * @author Frederick Baier
 */
class NodeTest {

    private lateinit var node: Node<String>

    @BeforeEach
    internal fun setUp() {
        this.node = Node<String>("test")
    }

    @Test
    fun newNode_HasNoSuccessors() {
        Assertions.assertTrue(node.getSuccessors().isEmpty())
    }

    @Test
    fun afterNodeSuccessorAddSuccessorsAreNotEmpty() {
        val node2 = Node("test2")
        node.addSuccessor(node2)
        Assertions.assertFalse(node.getSuccessors().isEmpty())
    }
}