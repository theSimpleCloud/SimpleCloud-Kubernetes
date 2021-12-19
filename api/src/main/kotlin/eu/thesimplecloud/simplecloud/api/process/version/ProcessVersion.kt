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

package eu.thesimplecloud.simplecloud.api.process.version

import eu.thesimplecloud.simplecloud.api.process.version.configuration.ProcessVersionConfiguration
import eu.thesimplecloud.simplecloud.api.utils.Identifiable
import eu.thesimplecloud.simplecloud.api.utils.Nameable

/**
 * Created by IntelliJ IDEA.
 * Date: 15.03.2021
 * Time: 10:24
 * @author Frederick Baier
 *
 * Represents a version processes can be executed with
 *
 */
interface ProcessVersion : Nameable, Identifiable<String> {

    /**
     * Returns the api type
     */
    fun getProcessApiType(): ProcessAPIType

    /**
     * Returns the load type
     */
    fun getLoadType(): ProcessVersionLoadType

    /**
     * Returns the direct download link to a jar file
     */
    fun getDownloadLink(): String

    /**
     * Returns the name of the java base image
     */
    fun getJavBaseImageName(): String

    /**
     * Returns the configuration of this process version
     */
    fun toConfiguration(): ProcessVersionConfiguration

}