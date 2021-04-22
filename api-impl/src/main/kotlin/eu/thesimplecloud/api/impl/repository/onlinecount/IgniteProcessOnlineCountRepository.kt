package eu.thesimplecloud.api.impl.repository.onlinecount

import eu.thesimplecloud.api.impl.ignite.IgniteSupplier
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.repository.onlinecount.IProcessOnlineCountRepository
import org.apache.ignite.IgniteCache

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 21:41
 * @author Frederick Baier
 */
class IgniteProcessOnlineCountRepository : AbstractIgniteRepository<IProcessesOnlineCountConfiguration>(), IProcessOnlineCountRepository {

    override fun getCache(): IgniteCache<String, IProcessesOnlineCountConfiguration> {
        return IgniteSupplier.ignite.getOrCreateCache("cloud-process-online-count-configurations")
    }
}