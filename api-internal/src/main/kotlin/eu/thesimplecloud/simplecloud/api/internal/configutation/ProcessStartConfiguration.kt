package eu.thesimplecloud.simplecloud.api.internal.configutation

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 13:47
 * @author Frederick Baier
 */
class ProcessStartConfiguration(
    val groupName: String,
    val processNumber: Int,
    val templateName: String,
    val maxPlayers: Int,
    val maxMemory: Int,
    val jvmArgumentsName: String?
)