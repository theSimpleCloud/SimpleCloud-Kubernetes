package eu.thesimplecloud.api.impl.repository.node

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.api.impl.ignite.predicate.NetworkComponentCompareUUIDPredicate
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.repository.node.INodeRepository
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 21:21
 * @author Frederick Baier
 */
@Singleton
class IgniteNodeRepository @Inject constructor(
    private val ignite: Ignite
) : AbstractIgniteRepository<INode>(), INodeRepository {

    override fun getCache(): IgniteCache<String, INode> {
        return ignite.getOrCreateCache("cloud-nodes")
    }

    override fun findNodeByUniqueId(uniqueId: UUID): CompletableFuture<INode> {
        return executeQueryAndFindFirst(NetworkComponentCompareUUIDPredicate<INode>(uniqueId))
    }
}