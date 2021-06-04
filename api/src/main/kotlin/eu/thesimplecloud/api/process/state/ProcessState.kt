package eu.thesimplecloud.api.process.state

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

    /**
     * The process is currently starting
     */
    STARTING,

    /**
     * The process is online and joinable for players
     */
    VISIBLE,

    /**
     * The process is online but ingame
     */
    INVISIBLE,

    /**
     * The process is closed
     */
    CLOSED;



}