package app.simplecloud.simplecloud.content

import io.ktor.http.content.*
import java.io.File

class FileRepo {

    private val dir = File("files/")

    init {
        dir.mkdirs()
        println(dir.listFiles().map { it.name })
    }

    fun saveFile(filePath: String, parts: List<PartData.FileItem>) {
        parts.forEach { part ->
            val file = File(this.dir, filePath)
            file.parentFile.mkdirs()
            part.streamProvider().use { input ->
                file.outputStream().buffered().use { output ->
                    input.copyTo(output)
                }
            }
            part.dispose()
        }
    }

    fun getFile(name: String): File {
        return File(dir, name)
    }

}