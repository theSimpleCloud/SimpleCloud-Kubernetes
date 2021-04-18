package eu.thesimplecloud.simplecloud.container.local

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 08.04.2021
 * Time: 09:48
 * @author Frederick Baier
 */
class LocalContainerLogsReader(
    process: Process
) {

    private val logsReader: BufferedReader = BufferedReader(InputStreamReader(process.inputStream))

    private val readLogs = CopyOnWriteArrayList<String>()

    fun getLogs(): List<String> {
        updateLogs()
        return this.readLogs
    }

    private fun updateLogs() {
        while (this.logsReader.ready()) {
            addLogLine(this.logsReader.readLine())
        }
    }

    private fun addLogLine(line: String) {
        if (this.readLogs.size > 200) {
            this.readLogs.removeAt(0)
        }
        this.readLogs.add(line)
    }

}