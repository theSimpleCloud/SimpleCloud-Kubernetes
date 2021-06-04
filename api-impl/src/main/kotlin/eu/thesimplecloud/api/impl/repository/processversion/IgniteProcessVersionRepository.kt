package eu.thesimplecloud.api.impl.repository.processversion

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.repository.processversion.IProcessVersionRepository
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 19:07
 * @author Frederick Baier
 */
@Singleton
class IgniteProcessVersionRepository @Inject constructor(
    private val ignite: Ignite
) : AbstractIgniteRepository<IProcessVersion>(), IProcessVersionRepository {

    override fun getCache(): IgniteCache<String, IProcessVersion> {
        return ignite.getOrCreateCache("cloud-process-versions")
    }


}