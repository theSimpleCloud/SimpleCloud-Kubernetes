package eu.thesimplecloud.api.impl.repository.node

import eu.thesimplecloud.api.impl.ignite.IgniteSupplier
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.repository.node.INodeRepository
import org.apache.ignite.IgniteCache

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 21:21
 * @author Frederick Baier
 */
class IgniteNodeRepository : AbstractIgniteRepository<INode>(), INodeRepository {

    override fun getCache(): IgniteCache<String, INode> {
        return IgniteSupplier.ignite.getOrCreateCache("cloud-nodes")
    }
}