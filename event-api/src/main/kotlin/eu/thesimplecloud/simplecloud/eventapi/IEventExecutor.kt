package eu.thesimplecloud.simplecloud.eventapi

interface IEventExecutor {

    /**
     * Executes the specified event.
     */
    fun execute(event: IEvent)

}
