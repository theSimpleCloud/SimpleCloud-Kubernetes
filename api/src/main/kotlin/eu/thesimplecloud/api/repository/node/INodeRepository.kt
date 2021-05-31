package eu.thesimplecloud.api.repository.node

import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.repository.IRepository
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 03.04.2021
 * Time: 17:54
 * @author Frederick Baier
 */
interface INodeRepository : IRepository<String, INode> {

    /**
     * Returns the node found by the specified [uniqueId]
     */
    fun findNodeByUniqueId(uniqueId: UUID): CompletableFuture<INode>

}