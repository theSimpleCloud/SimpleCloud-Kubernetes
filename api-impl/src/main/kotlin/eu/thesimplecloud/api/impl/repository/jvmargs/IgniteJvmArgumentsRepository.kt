package eu.thesimplecloud.api.impl.repository.jvmargs

import eu.thesimplecloud.api.impl.ignite.IgniteSupplier
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.repository.jvmargs.IJvmArgumentsRepository
import eu.thesimplecloud.api.repository.node.INodeRepository
import org.apache.ignite.IgniteCache

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 21:21
 * @author Frederick Baier
 */
class IgniteJvmArgumentsRepository : AbstractIgniteRepository<IJVMArguments>(), IJvmArgumentsRepository {

    override fun getCache(): IgniteCache<String, IJVMArguments> {
        return IgniteSupplier.ignite.getOrCreateCache("cloud-jvm-args")
    }
}