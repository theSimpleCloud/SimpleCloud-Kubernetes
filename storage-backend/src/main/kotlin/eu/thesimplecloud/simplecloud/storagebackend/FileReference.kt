/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.storagebackend

import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 05.06.2021
 * Time: 11:31
 * @author Frederick Baier
 */
class FileReference(
    directoryPath: String,
    val name: String,
    val isDirectory: Boolean,
    val lastModified: Long,
    /**
     * The file size in bytes
     */
    val size: Long
) {
    val directoryPath = directoryPath.replace("\\", "/")

    val path: String

    init {
        if (isDirectory) {
            this.path = if (this.directoryPath.isBlank()) name else "${this.directoryPath}/$name"
        } else {
            this.path = if (this.directoryPath.isBlank()) name else "${this.directoryPath}/$name"
        }
    }

    companion object {
        fun fromRealFile(file: File): FileReference {
            return FileReference(file.parentFile?.path ?: "", file.name, file.isDirectory, file.lastModified(), file.length())
        }

        private fun calculateRelativePath(file: File, directoryForRelativePath: File): String {
            return directoryForRelativePath.toPath().relativize(file.toPath()).toString()
        }

    }

    fun isNewerThan(other: FileReference): Boolean {
        return this.lastModified > other.lastModified
    }

    fun toLocalFile(): File {
        return File(this.path)
    }

}