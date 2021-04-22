package eu.thesimplecloud.api.impl.repository.group

import eu.thesimplecloud.api.impl.ignite.IgniteSupplier
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.repository.group.ICloudProcessGroupRepository
import eu.thesimplecloud.api.repository.process.ICloudProcessRepository
import org.apache.ignite.IgniteCache
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 13:42
 * @author Frederick Baier
 */
class IgniteCloudProcessGroupRepository : AbstractIgniteRepository<ICloudProcessGroup>(), ICloudProcessGroupRepository {

    override fun getCache(): IgniteCache<String, ICloudProcessGroup> {
        return IgniteSupplier.ignite.getOrCreateCache("cloud-process-groups")
    }

}