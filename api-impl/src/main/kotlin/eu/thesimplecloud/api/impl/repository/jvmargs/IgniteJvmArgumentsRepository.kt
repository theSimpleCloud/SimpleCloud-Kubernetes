package eu.thesimplecloud.api.impl.repository.jvmargs

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.repository.jvmargs.IJvmArgumentsRepository
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 21:21
 * @author Frederick Baier
 */
@Singleton
class IgniteJvmArgumentsRepository @Inject constructor(
    private val ignite: Ignite
) : AbstractIgniteRepository<IJVMArguments>(), IJvmArgumentsRepository {

    override fun getCache(): IgniteCache<String, IJVMArguments> {
        return ignite.getOrCreateCache("cloud-jvm-args")
    }
}