package eu.thesimplecloud.api.impl.repository.process

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.api.impl.ignite.predicate.NetworkComponentCompareUUIDPredicate
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.repository.process.ICloudProcessRepository
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 19:07
 * @author Frederick Baier
 */
@Singleton
class IgniteCloudProcessRepository @Inject constructor(
    private val ignite: Ignite
) : AbstractIgniteRepository<ICloudProcess>(), ICloudProcessRepository {

    override fun getCache(): IgniteCache<String, ICloudProcess> {
        return ignite.getOrCreateCache("cloud-process-groups")
    }

    override fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<ICloudProcess> {
        return executeQueryAndFindFirst(NetworkComponentCompareUUIDPredicate<ICloudProcess>(uniqueId))
    }


}