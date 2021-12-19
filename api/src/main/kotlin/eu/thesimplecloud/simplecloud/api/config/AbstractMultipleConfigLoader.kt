/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

package eu.thesimplecloud.simplecloud.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import eu.thesimplecloud.simplecloud.api.utils.Nameable
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 12.12.2020
 * Time: 11:32
 * @author Frederick Baier
 */
abstract class AbstractMultipleConfigLoader<T : Nameable>(
    private val clazz: Class<T>,
    private val directory: File,
    private val defaultValues: List<T>,
    private val saveDefaultOnFirstLoad: Boolean
)  {

    private val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    fun save(value: T) {
        this.objectMapper.writeValue(getFileByObject(value), value)
    }

    fun delete(value: T) {
        getFileByObject(value).delete()
    }

    fun loadAll(): Set<T> {
        if (!directory.exists() && saveDefaultOnFirstLoad) saveDefaults()
        return this.directory.listFiles()?.mapNotNull { this.objectMapper.readValue(it, this.clazz) }?.toSet()
            ?: emptySet()
    }

    private fun saveDefaults() {
        defaultValues.forEach { save(it) }
    }

    private fun getFileByObject(value: T): File {
        return File(this.directory, value.getName() + ".json")
    }
}