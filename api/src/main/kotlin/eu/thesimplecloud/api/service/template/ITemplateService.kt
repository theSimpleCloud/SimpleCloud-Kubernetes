package eu.thesimplecloud.api.service.template

import eu.thesimplecloud.api.service.IService
import eu.thesimplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 02.04.2021
 * Time: 12:31
 * @author Frederick Baier
 */
interface ITemplateService : IService {

    fun findByName(name: String): CompletableFuture<ITemplate>

}