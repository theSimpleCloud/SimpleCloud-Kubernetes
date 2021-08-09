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

import com.google.inject.AbstractModule
import eu.thesimplecloud.simplecloud.storagebackend.IStorageBackend
import eu.thesimplecloud.simplecloud.storagebackend.sftp.config.SftpConfigLoader
import eu.thesimplecloud.simplecloud.storagebackend.sftp.config.SftpLoginConfiguration

/**
 * Created by IntelliJ IDEA.
 * Date: 09.06.2021
 * Time: 17:13
 * @author Frederick Baier
 */
class SftpModule : AbstractModule() {

    override fun configure() {
        bind(SftpLoginConfiguration::class.java).toProvider(SftpConfigLoader::class.java)
        bind(IStorageBackend::class.java).to(SftpStorageBackend::class.java)
    }

}