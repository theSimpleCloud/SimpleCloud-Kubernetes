package eu.thesimplecloud.simplecloud.api.internal.configutation

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 13:47
 * @author Frederick Baier
 */
data class ProcessStartConfiguration(
    val groupName: String,
    val processNumber: Int,
    val imageName: String,
    val maxMemory: Int,
    val maxPlayers: Int
) {

    fun isProcessNumberSet(): Boolean {
        return this.processNumber != -1
    }

}