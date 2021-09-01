package eu.thesimplecloud.simplecloud.api.utils

import dev.morphia.annotations.Id

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

    constructor() : this("127.0.0.1", -1)

    fun asIpString(): String {
        return "${host}:${port}"
    }

    companion object {
        fun fromIpString(string: String): Address {
            val array = string.split(":")
            if (array.size != 2) {
                throw IllegalArgumentException(
                    "Wrong Address format. Expected format: 'host:port' (e.g. 55.55.55.55:1630) but was $string"
                )
            }
            return Address(array[0], array[1].toInt())
        }
    }

}