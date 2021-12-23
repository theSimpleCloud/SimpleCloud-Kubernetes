package app.simplecloud.simplecloud.prepare

import java.io.File
import java.lang.instrument.Instrumentation


class AgentMain {

    companion object {

        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation?) {
            val targetFile = File("plugins/SimpleCloud-Plugin.jar")
            Downloader.userAgentDownload("http://content:8008/content/SimpleCloud-Plugin.jar", targetFile)
            println("Downloaded plugin file to ${targetFile.absolutePath}")
        }
    }


}