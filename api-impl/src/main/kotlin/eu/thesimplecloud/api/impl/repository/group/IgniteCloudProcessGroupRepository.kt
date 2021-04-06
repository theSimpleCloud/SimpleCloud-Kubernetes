package eu.thesimplecloud.api.impl.repository.group

import eu.thesimplecloud.api.impl.ignite.IgniteSupplier
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.repository.group.ICloudProcessGroupRepository
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 13:42
 * @author Frederick Baier
 */
class IgniteCloudProcessGroupRepository : ICloudProcessGroupRepository {

    private val cache = IgniteSupplier.ignite.getOrCreateCache<String, ICloudProcessGroup>(IGNITE_GROUP_CACHE_NAME)

    override fun findAll(): CompletableFuture<List<ICloudProcessGroup>> {
        return CompletableFuture.supplyAsync { this.cache.toList().map { it.value } }
    }

    override fun find(identifier: String): CompletableFuture<ICloudProcessGroup?> {
        return CompletableFuture.supplyAsync { this.cache.get(identifier) }
    }

    override fun save(value: ICloudProcessGroup) {
        this.cache.putAsync(value.getIdentifier(), value)
    }

    companion object {
        const val IGNITE_GROUP_CACHE_NAME = "cloud-process-groups"
    }

}