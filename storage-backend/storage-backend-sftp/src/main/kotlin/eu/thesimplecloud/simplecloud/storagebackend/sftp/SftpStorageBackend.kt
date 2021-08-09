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

package eu.thesimplecloud.simplecloud.storagebackend.sftp

import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.storagebackend.IStorageBackend
import eu.thesimplecloud.simplecloud.storagebackend.FileReference
import eu.thesimplecloud.simplecloud.storagebackend.sftp.config.SftpLoginConfiguration
import eu.thesimplecloud.simplecloud.storagebackend.sftp.util.SplitStringBuilder
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 05.06.2021
 * Time: 11:33
 * @author Frederick Baier
 */
@Singleton
class SftpStorageBackend(
    private val loginConfig: SftpLoginConfiguration
) : IStorageBackend {

    private val ftpOperation = SftpOperations(loginConfig)

    override fun deleteDirectory(remotePath: String) {
        val filesInDir = this.ftpOperation.listFiles(remotePath)
        if (filesInDir.isEmpty()) return
        filesInDir.forEach { deleteFileOrDir(it) }
        this.ftpOperation.deleteDirectory(remotePath)
    }

    override fun deleteFile(remotePath: String) {
        this.ftpOperation.deleteFile(remotePath)
    }

    override fun downloadFile(remotePath: String, fileToSaveTo: File) {
        this.ftpOperation.downloadFile(remotePath, fileToSaveTo)
    }

    override fun uploadFile(remotePath: String, fileToUpload: File) {
        createDirectoryFromFilePath(remotePath)
        this.ftpOperation.uploadFile(remotePath, fileToUpload)
    }

    override fun listFiles(remoteDir: String): List<FileReference> {
        return ftpOperation.listFiles(remoteDir)
    }

    private fun deleteFileOrDir(remoteFile: FileReference) {
        if (remoteFile.isDirectory) {
            deleteDirectory(remoteFile)
        } else {
            deleteFile(remoteFile)
        }
    }

    private fun createDirectoryFromFilePath(path: String) {
        val pathToLastDir = path.split("/").dropLast(1).joinToString("/")
        createDirectory(pathToLastDir)
    }

    private fun createDirectory(path: String) {
        val directoryPathChain = SplitStringBuilder.buildPossibleStringsSplitBy(path, "/")
        directoryPathChain.forEach {
            createDirectoryIfItIsValid(it)
        }
    }

    private fun createDirectoryIfItIsValid(path: String) {
        if (path.isBlank() || path == "/") return
        this.ftpOperation.makeDirectory(path)
    }


}