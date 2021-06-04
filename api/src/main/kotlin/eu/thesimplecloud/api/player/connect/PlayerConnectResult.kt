package eu.thesimplecloud.api.player.connect

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 20:12
 * @author Frederick Baier
 */
enum class PlayerConnectResult {

    /**
     * The player was successfully connected to the server
     */
    SUCCESS,

    /**
     * The player is already connected to the server
     */
    ALREADY_CONNECTED,

    /**
     * The request failed for any reason
     */
    FAILED

}