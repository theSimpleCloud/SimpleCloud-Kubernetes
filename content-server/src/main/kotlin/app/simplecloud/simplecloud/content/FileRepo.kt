/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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