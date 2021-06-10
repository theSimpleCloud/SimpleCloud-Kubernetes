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
import eu.thesimplecloud.simplecloud.storagebackend.util.getElementsAlsoContainedInList
import eu.thesimplecloud.simplecloud.storagebackend.util.getElementsNotContainedInList
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

abstract class AbstractSynchronizer(
    private val remoteDir: String,
    private val localDir: File,
    protected val storageBackend: IStorageBackend
) {

    private val allFilesInLocalDir = FileListUtil.getAllFilesInDirectoryAndSubDirectories(this.localDir)
        .map { FileReference.fromRealFile(it) }
    private val allRemoteFiles = storageBackend.listAllFilesRecursive(this.remoteDir)

    protected val localRelativeDir = RelativeDirectory(this.localDir.path, allFilesInLocalDir)
    protected val remoteRelativeDir = RelativeDirectory(remoteDir, allRemoteFiles)


    abstract fun synchronize()

    /**
     * Determines the "absolute" path for the files to be deleted
     * @return the file paths to be deleted on the receiver side
     */
    fun getFilesToDelete(
        senderRelativeDirectory: RelativeDirectory,
        receiverRelativeDirectory: RelativeDirectory
    ): List<String> {
        val senderRelativePaths = senderRelativeDirectory.getAllRelativeFiles().map { it.relativePath }
        val receiverRelativePaths = receiverRelativeDirectory.getAllRelativeFiles().map { it.relativePath }

        return receiverRelativePaths.getElementsNotContainedInList(senderRelativePaths)
    }

    /**
     * Determines the "absolute" path for the files to be uploaded
     * @return the file paths to be uploaded
     */
    fun getCreatedFiles(
        senderRelativeDirectory: RelativeDirectory,
        receiverRelativeDirectory: RelativeDirectory
    ): List<String> {
        val senderRelativePaths = senderRelativeDirectory.getAllRelativeFiles().map { it.relativePath }
        val receiverRelativePaths = receiverRelativeDirectory.getAllRelativeFiles().map { it.relativePath }

        return senderRelativePaths.getElementsNotContainedInList(receiverRelativePaths)
    }

    /**
     * Determines the "absolute" path for the files to be deleted
     * @return the file paths to be deleted on the receiver side
     */
    fun getChangedFiles(
        senderRelativeDirectory: RelativeDirectory,
        receiverRelativeDirectory: RelativeDirectory
    ): List<String> {
        val senderRelativeFiles = senderRelativeDirectory.getAllRelativeFiles()
        val receiverRelativeFiles = receiverRelativeDirectory.getAllRelativeFiles()
        val senderRelativePaths = senderRelativeFiles.map { it.relativePath }
        val receiverRelativePaths = receiverRelativeFiles.map { it.relativePath }
        val matchingFilePaths = senderRelativePaths.getElementsAlsoContainedInList(receiverRelativePaths)

        return matchingFilePaths.mapNotNull {
            val senderFile = senderRelativeDirectory.getFileReferenceByRelativePath(it)!!
            val receiverFile = receiverRelativeDirectory.getFileReferenceByRelativePath(it)!!
            if (areFilesLastModifiedEqual(senderFile, receiverFile)) {
                null
            } else {
                it
            }
        }
    }

    private fun areFilesLastModifiedEqual(fileOne: FileReference, fileTwo: FileReference): Boolean {
        val lastModifiedOne = fileOne.lastModified / 1000
        val lastModifiedTwo = fileTwo.lastModified / 1000

        return lastModifiedOne == lastModifiedTwo
    }


}
