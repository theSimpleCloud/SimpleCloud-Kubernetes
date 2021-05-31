package eu.thesimplecloud.api.impl.repository.group

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.repository.group.ICloudProcessGroupRepository
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 13:42
 * @author Frederick Baier
 */
@Singleton
class IgniteCloudProcessGroupRepository @Inject constructor(
    private val ignite: Ignite
) : AbstractIgniteRepository<ICloudProcessGroup>(), ICloudProcessGroupRepository {

    override fun getCache(): IgniteCache<String, ICloudProcessGroup> {
        return ignite.getOrCreateCache("cloud-process-groups")
    }

}