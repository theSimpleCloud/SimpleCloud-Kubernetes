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

package eu.thesimplecloud.simplecloud.api.impl.process.version

import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.process.version.ProcessAPIType
import eu.thesimplecloud.simplecloud.api.process.version.ProcessVersionLoadType
import eu.thesimplecloud.simplecloud.api.process.version.configuration.ProcessVersionConfiguration
import java.net.URL

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 11:53
 * @author Frederick Baier
 */
class ProcessVersion(
    private val configuration: ProcessVersionConfiguration
) : IProcessVersion {

    override fun getProcessApiType(): ProcessAPIType {
        return this.configuration.apiType
    }

    override fun getLoadType(): ProcessVersionLoadType {
        return this.configuration.loadType
    }

    override fun getDownloadLink(): String {
        return this.configuration.downloadLink
    }

    override fun getJavBaseImageName(): String {
        return this.configuration.javaBaseImageName
    }

    override fun getName(): String {
        return this.configuration.name
    }

    override fun getIdentifier(): String {
        return getName()
    }

    override fun toConfiguration(): ProcessVersionConfiguration {
        return this.configuration
    }

}