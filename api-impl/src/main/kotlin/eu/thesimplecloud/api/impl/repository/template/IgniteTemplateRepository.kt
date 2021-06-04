package eu.thesimplecloud.api.impl.repository.template

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.repository.template.ITemplateRepository
import eu.thesimplecloud.api.template.ITemplate
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 19:07
 * @author Frederick Baier
 */
@Singleton
class IgniteTemplateRepository @Inject constructor(
    private val ignite: Ignite
) : AbstractIgniteRepository<ITemplate>(), ITemplateRepository {

    override fun getCache(): IgniteCache<String, ITemplate> {
        return ignite.getOrCreateCache("cloud-templates")
    }


}