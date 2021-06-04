package eu.thesimplecloud.api.service.node

import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.service.IService
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 03.04.2021
 * Time: 17:55
 * @author Frederick Baier
 */
interface INodeService : IService {

    fun findNodeByName(name: String): CompletableFuture<INode>

    fun findNodesByName(vararg names: String): CompletableFuture<List<INode>>

}