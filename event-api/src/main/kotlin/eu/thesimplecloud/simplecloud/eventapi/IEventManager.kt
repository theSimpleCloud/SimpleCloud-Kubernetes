package eu.thesimplecloud.simplecloud.eventapi


interface IEventManager {

    /**
     * Registers a listener
     */
    fun registerListener(eventRegisterer: IEventRegisterer, listener: IListener)

    /**
     * Registers an event
     */
    fun registerEvent(
        eventRegisterer: IEventRegisterer,
        eventClass: Class<out IEvent>,
        listener: IListener,
        eventExecutor: IEventExecutor
    )

    /**
     * Unregisters the all [IEventExecutor]s associated with the specified [listener]
     */
    fun unregisterListener(listener: IListener)

    /**
     * Calls the specified [event] so it will be handed to the executors registered
     */
    fun call(event: IEvent)

    /**
     * Unregisters all [IListener]s associated with the specified [IEventRegisterer]
     */
    fun unregisterAllListenersByRegisterer(registerer: IEventRegisterer)

    /**
     * Unregisters all listeners
     */
    fun unregisterAll()

}
