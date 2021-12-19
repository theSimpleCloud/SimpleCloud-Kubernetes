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

package eu.thesimplecloud.simplecloud.api.template

import eu.thesimplecloud.simplecloud.api.template.configuration.TemplateConfiguration
import eu.thesimplecloud.simplecloud.api.utils.Identifiable
import eu.thesimplecloud.simplecloud.api.utils.Nameable
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 16.03.2021
 * Time: 19:55
 * @author Frederick Baier
 *
 * Represents a template used to create processes from
 * The files associated with this template will be copied to a process before the process gets started
 *
 */
interface Template : Nameable, Identifiable<String> {

    /**
     * Returns the parent template or null if this template has no parent
     */
    fun getParentTemplate(): CompletableFuture<Template>

    /**
     * Returns whether this template has a parent
     */
    fun hasParent(): Boolean

    /**
     * Returns the configuration of this template
     */
    fun toConfiguration(): TemplateConfiguration

}