package app.simplecloud.simplecloud.plugin.proxy

/**
 * Date: 24.01.22
 * Time: 19:40
 * @author Frederick Baier
 *
 */
/*
class ProxyIgniteUpdateListener @Inject constructor(
    private val proxyServerRegistry: ProxyServerRegistry,
    private val processFactory: CloudProcessFactory,
) : AbstractCacheEntryListener<String, CloudProcessConfiguration>(false, false) {

    override fun onCreated(events: MutableIterable<CacheEntryEvent<out String, out CloudProcessConfiguration>>) {
        events.forEach { handleCreateEvent(it) }
    }

    private fun handleCreateEvent(event: CacheEntryEvent<out String, out CloudProcessConfiguration>) {
        val cloudProcess = this.processFactory.create(event.value)
        this.proxyServerRegistry.registerProcess(cloudProcess)
    }

    override fun onRemoved(events: MutableIterable<CacheEntryEvent<out String, out CloudProcessConfiguration>>) {
        events.forEach { handleRemoveEvent(it) }
    }

    private fun handleRemoveEvent(event: CacheEntryEvent<out String, out CloudProcessConfiguration>) {
        this.proxyServerRegistry.unregisterServer(event.value.getProcessName())
    }

}

 */