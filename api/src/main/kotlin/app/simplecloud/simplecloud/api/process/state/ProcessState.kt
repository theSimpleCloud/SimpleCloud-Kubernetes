package app.simplecloud.simplecloud.api.process.state

/**
 * Created by IntelliJ IDEA.
 * Date: 17.03.2021
 * Time: 21:12
 * @author Frederick Baier
 *
 * Represents the state of a process
 *
 */
enum class ProcessState {

    /**
     * The process was registered and is ready to be started
     */
    PREPARED,

    STARTING,

    ONLINE,

    CLOSED;

}