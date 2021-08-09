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

import eu.thesimplecloud.simplecloud.storagebackend.FileReference
import eu.thesimplecloud.simplecloud.storagebackend.sftp.config.SftpLoginConfiguration
import java.io.File
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.RemoteResourceInfo
import net.schmizz.sshj.sftp.SFTPClient
import net.schmizz.sshj.sftp.SFTPException
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.xfer.FileSystemFile


/**
 * Created by IntelliJ IDEA.
 * Date: 05.06.2021
 * Time: 11:33
 * @author Frederick Baier
 */
class SftpOperations(
    private val loginConfig: SftpLoginConfiguration
) {

    private val sftpClient: SFTPClient

    init {
        val client = SSHClient()
        client.addHostKeyVerifier(PromiscuousVerifier())
        client.connect(this.loginConfig.host, this.loginConfig.port)
        client.authPassword(this.loginConfig.username, this.loginConfig.password)
        this.sftpClient = client.newSFTPClient()
    }

    fun makeDirectory(remotePath: String) {
        try {
            this.sftpClient.mkdir(remotePath)
        } catch (e: SFTPException) {
            //ignore because it is often the root directory
        }
    }

    fun deleteDirectory(remotePath: String) {
        this.sftpClient.rmdir(remotePath)
    }

    fun deleteFile(remotePath: String) {
        this.sftpClient.rm(remotePath)
    }

    fun downloadFile(remotePath: String, fileToSaveTo: File) {
        this.sftpClient.get(remotePath, FileSystemFile(fileToSaveTo))
    }

    fun uploadFile(remotePath: String, fileToUpload: File) {
        this.sftpClient.put(FileSystemFile(fileToUpload), remotePath)
    }

    fun listFiles(remoteDir: String): List<FileReference> {
        val files = executeListFiles(remoteDir)
        return files.map {
            //SFTP servers seme to drop the last 3 digits of lastModified so I add 3 zeros
            val lastModified = it.attributes.mtime * 1000
            FileReference(remoteDir, it.name, it.isDirectory, lastModified, it.attributes.size)
        }
    }

    private fun executeListFiles(remoteDir: String): List<RemoteResourceInfo> {
        return try {
            this.sftpClient.ls(remoteDir)
        } catch (e: SFTPException) {
            return emptyList()
        }
    }


}