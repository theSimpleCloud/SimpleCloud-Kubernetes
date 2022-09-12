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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 29.04.2021
 * Time: 14:36
 * @author Frederick Baier
 */
class GraphTest {

    private lateinit var graph: Graph<String>

    @BeforeEach
    fun setUp() {
        graph = Graph<String>(false)
    }

    @Test
    fun afterNodeSet_NodesAreRegistered() {
        graph.setNodeWithSuccessors("Test", listOf("Test1", "Test2"))
        Assertions.assertTrue(graph.getNodeValues().contains("Test"))
        Assertions.assertTrue(graph.getNodeValues().contains("Test1"))
        Assertions.assertTrue(graph.getNodeValues().contains("Test2"))
    }

    @Test
    fun afterNodeSet_NodesAreRegistered2() {
        graph.setNodeWithSuccessors("Test", listOf("TestN", "Test2"))
        Assertions.assertTrue(graph.getNodeValues().contains("Test"))
        Assertions.assertTrue(graph.getNodeValues().contains("TestN"))
        Assertions.assertTrue(graph.getNodeValues().contains("Test2"))
    }

    @Test
    fun afterOneNodeAdd_EntrypointExists() {
        graph.setNodeWithSuccessors("Test", listOf("TestN", "Test2"))
        val entryPointValues = graph.getEntryPointValues()
        Assertions.assertTrue(entryPointValues.contains("Test2"))
        Assertions.assertTrue(entryPointValues.contains("TestN"))
        Assertions.assertEquals(2, entryPointValues.size)
    }

    @Test
    fun afterTwoIndependentNodeAdd_TwoEntrypointExists() {
        graph.setNodeWithSuccessors("Test", listOf("TestN", "Test2"))
        graph.setNodeWithSuccessors("TestC", listOf("TestNC", "Test2C"))
        Assertions.assertEquals(4, graph.getEntryPointValues().size)
    }

    @Test
    fun afterTwoNodeAddWithSecondLinkedToFist_OneEntrypointExists() {
        graph.setNodeWithSuccessors("Test", listOf("Test2"))
        graph.setNodeWithSuccessors("Test2", emptyList())
        Assertions.assertEquals(1, graph.getEntryPointValues().size)
    }

    @Test
    fun entryPointTest() {
        graph.setNodeWithSuccessors("Test", listOf("Test2"))
        graph.setNodeWithSuccessors("Test2", listOf("Test3"))
        graph.setNodeWithSuccessors("Test3", emptyList())
        graph.setNodeWithSuccessors("Test4", emptyList())
        graph.setNodeWithSuccessors("Test5", emptyList())
        Assertions.assertEquals(3, graph.getEntryPointValues().size)
    }

    @Test
    fun selfRecursionTest() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            graph.setNodeWithSuccessors("Test", listOf("Test"))
        }
    }

    @Test
    fun recursionTest() {
        graph.setNodeWithSuccessors("Test", listOf("Test2"))
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            graph.setNodeWithSuccessors("Test2", listOf("Test"))
        }
    }

    @Test
    fun recursionTest2() {
        graph.setNodeWithSuccessors("Test", listOf("Test2"))
        graph.setNodeWithSuccessors("Test2", listOf("Test3"))
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            graph.setNodeWithSuccessors("Test3", listOf("Test"))
        }
    }

    @Test
    fun pathTest1() {
        graph.setNodeWithSuccessors("Test", listOf("Test2"))
        graph.setNodeWithSuccessors("Test2", listOf("Test3"))
        graph.setNodeWithSuccessors("Test", listOf("Test7"))
        graph.setNodeWithSuccessors("Test", listOf("Test8"))
        graph.setNodeWithSuccessors("Test", listOf("Test9"))
        val testNode = graph.getNodeByValue("Test")!!
        val test3Node = graph.getNodeByValue("Test3")!!
        val path = GraphNodePathFinder<String>(graph, testNode, test3Node).findPath().map { it.nodeValue }
        Assertions.assertEquals("Test", path[0])
        Assertions.assertEquals("Test2", path[1])
    }

    @Test
    fun pathTest2() {
        graph.setNodeWithSuccessors("Test", listOf("Test2"))
        graph.setNodeWithSuccessors("Test2", listOf("Test22", "Test3", "Test66"))
        graph.setNodeWithSuccessors("Test3", listOf("Test4"))
        graph.setNodeWithSuccessors("Test4", listOf("Test5"))
        graph.setNodeWithSuccessors("Test5", listOf("Test6"))
        graph.setNodeWithSuccessors("Test6", listOf("Test7"))
        val startNode = graph.getNodeByValue("Test")!!
        val searchNode = graph.getNodeByValue("Test5")!!
        val path = GraphNodePathFinder<String>(graph, startNode, searchNode).findPath().map { it.nodeValue }
        println(path)
        Assertions.assertEquals("Test", path[0])
        Assertions.assertEquals("Test2", path[1])
        Assertions.assertEquals("Test3", path[2])
        Assertions.assertEquals("Test4", path[3])
    }

}