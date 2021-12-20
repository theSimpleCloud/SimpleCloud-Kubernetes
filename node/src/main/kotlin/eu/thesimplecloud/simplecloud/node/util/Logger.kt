package eu.thesimplecloud.simplecloud.node.util

import org.tinylog.Logger

object Logger {

    fun info(message: String) {
        Logger.info(message as Any)
    }

    fun error(throwable: Throwable) {
        Logger.error(throwable)
    }

    fun warn(message: String) {
        Logger.warn(message as Any)
    }

}