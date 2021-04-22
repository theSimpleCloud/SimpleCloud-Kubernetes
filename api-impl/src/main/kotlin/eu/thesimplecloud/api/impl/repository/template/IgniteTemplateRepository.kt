package eu.thesimplecloud.api.impl.repository.template

import eu.thesimplecloud.api.impl.ignite.IgniteSupplier
import eu.thesimplecloud.api.impl.repository.AbstractIgniteRepository
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.repository.process.ICloudProcessRepository
import eu.thesimplecloud.api.repository.processversion.IProcessVersionRepository
import eu.thesimplecloud.api.repository.template.ITemplateRepository
import eu.thesimplecloud.api.template.ITemplate
import org.apache.ignite.IgniteCache

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 19:07
 * @author Frederick Baier
 */
class IgniteTemplateRepository : AbstractIgniteRepository<ITemplate>(), ITemplateRepository {

    override fun getCache(): IgniteCache<String, ITemplate> {
        return IgniteSupplier.ignite.getOrCreateCache("cloud-templates")
    }


}