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

import eu.thesimplecloud.simplecloud.storagebackend.sync.ClientToServerSynchronizer
import eu.thesimplecloud.simplecloud.storagebackend.sync.ServerToClientSynchronizer
import eu.thesimplecloud.simplecloud.storagebackend.sync.SynchronizationDirection
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 05.06.2021
 * Time: 11:25
 * @author Frederick Baier
 *
 */
interface IStorageBackend {

    fun deleteDirectory(remoteFile: FileReference) {
        deleteDirectory(remoteFile.path)
    }

    fun deleteDirectory(remotePath: String)

    /**
     * Deletes the specified file
     */
    fun deleteFile(remoteFile: FileReference) {
        deleteFile(remoteFile.path)
    }

    /**
     * Deletes the specified file
     */
    fun deleteFile(remotePath: String)

    /**
     * Downloads the specified file
     */
    fun downloadFile(remoteFile: FileReference, fileToSaveTo: File) {
        downloadFile(remoteFile.path, fileToSaveTo)
    }

    /**
     * Downloads the specified file
     */
    fun downloadFile(remotePath: String, fileToSaveTo: File)

    /**
     * Uploads the specified file to the [remotePath]
     */
    fun uploadFile(remotePath: String, fileToUpload: File)

    /**
     * Lists all files in the specified [remoteDir]
     */
    fun listFiles(remoteDir: FileReference): List<FileReference> {
        return listFiles(remoteDir.path)
    }

    /**
     * Lists all files in the specified [remoteDir]
     */
    fun listFiles(remoteDir: String): List<FileReference>

    /**
     * Downloads the specified [remoteDir] into the [localDir]
     */
    fun downloadDir(remoteDir: FileReference, localDir: File) {
        require(remoteDir.isDirectory)
        downloadDir(remoteDir.path, localDir)
    }

    /**
     * Downloads the specified [remoteDir] into the [localDir]
     */
    fun downloadDir(remoteDir: String, localDir: File) {
        require(!localDir.exists() || localDir.isDirectory)
        DirectoryDownloader(this, remoteDir, localDir).download()
    }

    fun uploadDir(remoteDir: String, localDir: File) {
        require(!localDir.exists() || localDir.isDirectory)
        DirectoryUpload(this, remoteDir, localDir).upload()
    }

    fun listAllFilesRecursive(remoteDir: String): List<FileReference> {
        val remoteFiles = ArrayList<FileReference>()
        val allFilesInDirectory = listFiles(remoteDir)
        remoteFiles.addAll(allFilesInDirectory.filter { !it.isDirectory })

        val directoryFiles = allFilesInDirectory.filter { it.isDirectory }
        for (remoteDirectoryFile in directoryFiles) {
            val subDirFiles = listAllFilesRecursive(remoteDirectoryFile.path)
            remoteFiles.addAll(subDirFiles)
        }
        return remoteFiles
    }

    fun downloadFileOrDir(remoteFile: FileReference, localDir: File) {
        if (remoteFile.isDirectory) {
            downloadDir(remoteFile, File(localDir, remoteFile.name))
        } else {
            downloadFile(remoteFile, File(localDir, remoteFile.name))
        }
    }

    /**
     * Synchronizes the specified [remoteDir] with the [localDir] in the specified [direction]
     * The content of the receiver side will be completely overridden
     */
    fun synchronizeDirectory(remoteDir: String, localDir: File, direction: SynchronizationDirection) {
        when (direction) {
            SynchronizationDirection.SERVER_TO_CLIENT -> {
                ServerToClientSynchronizer(remoteDir, localDir, this).synchronize()
            }
            SynchronizationDirection.CLIENT_TO_SERVER -> {
                ClientToServerSynchronizer(remoteDir, localDir, this).synchronize()
            }
        }
    }

}