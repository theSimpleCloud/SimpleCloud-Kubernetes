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

package app.simplecloud.simplecloud.module.load.util

import java.io.*
import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.jar.Manifest


/**
 * Date: 01.09.22
 * Time: 09:54
 * @author Frederick Baier
 *
 */
class JarCreator(
    private val targetFile: File,
) {


    @Volatile
    private var stream: JarOutputStream? = null

    fun createJar() {
        if (!targetFile.exists()) {
            targetFile.parentFile.mkdirs()
            targetFile.createNewFile()
        }
        this.stream = openJar()
    }

    private fun createManifest(): Manifest {
        val manifest = Manifest()
        manifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
        return manifest
    }

    @Throws(IOException::class)
    private fun openJar(): JarOutputStream {
        return JarOutputStream(FileOutputStream(this.targetFile), createManifest())
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun addFile(rootPath: String, source: String) {
        var remaining = ""
        remaining = if (rootPath.endsWith(File.separator)) {
            source.substring(rootPath.length)
        } else {
            source.substring(rootPath.length + 1)
        }
        val name = remaining.replace("\\", "/")
        val entry = JarEntry(name)
        entry.time = File(source).lastModified()
        stream!!.putNextEntry(entry)
        val `in` = BufferedInputStream(FileInputStream(source))
        val buffer = ByteArray(1024)
        while (true) {
            val count = `in`.read(buffer)
            if (count == -1) {
                break
            }
            stream!!.write(buffer, 0, count)
        }
        stream!!.closeEntry()
        `in`.close()
    }

    fun close() {
        this.stream!!.close()
    }

}