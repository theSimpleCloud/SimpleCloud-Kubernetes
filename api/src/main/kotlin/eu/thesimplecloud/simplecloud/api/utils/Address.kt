package eu.thesimplecloud.simplecloud.api.utils

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 12:57
 * @author Frederick Baier
 */
class Address(
    val host: String,
    val port: Int
) {

    fun asIpString(): String {
        return "${host}:${port}"
    }

    companion object {
        fun fromIpString(string: String): Address {
            val array = string.split(":")
            return Address(array[0], array[1].toInt())
        }
    }

}