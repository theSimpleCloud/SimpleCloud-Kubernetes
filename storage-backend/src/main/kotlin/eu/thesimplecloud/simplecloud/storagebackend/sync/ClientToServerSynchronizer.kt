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

package eu.thesimplecloud.simplecloud.storagebackend.sync

import eu.thesimplecloud.simplecloud.storagebackend.FileReference
import eu.thesimplecloud.simplecloud.storagebackend.IStorageBackend
import eu.thesimplecloud.simplecloud.storagebackend.RelativeDirectory
import eu.thesimplecloud.simplecloud.storagebackend.util.FileListUtil
import java.io.File

class ClientToServerSynchronizer(
    remoteDir: String,
    localDir: File,
    storageBackend: IStorageBackend
) : AbstractSynchronizer(remoteDir, localDir, storageBackend) {


    override fun synchronize() {
        uploadCreatedFiles()
        uploadChangedFiles()
        deleteDeletedFiles()
    }

    private fun deleteDeletedFiles() {
        val filesToDelete = getFilesToDelete(localRelativeDir, remoteRelativeDir)
        filesToDelete.forEach {
            deleteRemoteFile(it)
        }
    }

    private fun deleteRemoteFile(relativePath: String) {
        val absoluteRemotePath = this.remoteRelativeDir.getFileReferenceByRelativePath(relativePath)!!.path
        this.storageBackend.deleteFile(absoluteRemotePath)
    }

    private fun uploadCreatedFiles() {
        val createdFiles = getCreatedFiles(localRelativeDir, remoteRelativeDir)
        createdFiles.forEach {
            uploadFileByPath(it)
        }
    }

    private fun uploadChangedFiles() {
        val changedFiles = getChangedFiles(localRelativeDir, remoteRelativeDir)
        changedFiles.forEach {
            uploadFileByPath(it)
        }
    }

    private fun uploadFileByPath(relativePath: String) {
        val localFileToUpload = this.localRelativeDir.getFileReferenceByRelativePath(relativePath)!!.toLocalFile()
        val remotePath = this.remoteRelativeDir.generateAbsolutePathByRelativePath(relativePath)
        this.storageBackend.uploadFile(remotePath, localFileToUpload)
    }

}
