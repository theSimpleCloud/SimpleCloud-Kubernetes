package app.simplecloud.simplecloud.plugin.proxy

import com.google.inject.AbstractModule

/**
 * Date: 14.01.22
 * Time: 14:33
 * @author Frederick Baier
 *
 */
class ProxyBinderModule(
    private val subModule: AbstractModule? = null
) : AbstractModule() {

    override fun configure() {
        bind(ProxyController::class.java).to(ProxyControllerImpl::class.java)

        if (subModule != null)
            install(subModule)
    }

}