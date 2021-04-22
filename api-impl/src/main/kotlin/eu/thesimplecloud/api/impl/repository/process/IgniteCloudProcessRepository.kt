package eu.thesimplecloud.api.impl.repository.process

import eu.thesimplecloud.api.impl.ignite.IgniteSupplier
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.repository.process.ICloudProcessRepository
import org.apache.ignite.IgniteCache

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 19:07
 * @author Frederick Baier
 */
class IgniteCloudProcessRepository : AbstractIgniteRepository<ICloudProcess>(), ICloudProcessRepository {

    override fun getCache(): IgniteCache<String, ICloudProcess> {
        return IgniteSupplier.ignite.getOrCreateCache("cloud-process-groups")
    }


}