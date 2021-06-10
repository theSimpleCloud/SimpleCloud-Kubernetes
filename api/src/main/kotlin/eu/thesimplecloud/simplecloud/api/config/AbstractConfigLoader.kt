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
import com.google.inject.Provider
import java.io.File

abstract class AbstractConfigLoader<T : Any>(
    private val configClass: Class<T>,
    private val configFie: File,
    private val lazyDefaultObject: () -> T,
    private val saveDefaultOnFistLoad: Boolean
) : Provider<T> {

    private val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    fun loadConfig(): T {
        if (!doesConfigFileExist()) {
            return saveAndLoadDefaultConfig()
        }
        return objectMapper.readValue(configFie, configClass)
    }

    private fun saveAndLoadDefaultConfig(): T {
        val defaultConfig = this.lazyDefaultObject()
        saveConfig(defaultConfig)
        return defaultConfig
    }

    fun saveConfig(value: T) {
        objectMapper.writeValue(configFie, value)
    }

    fun doesConfigFileExist(): Boolean = this.configFie.exists()

    override fun get(): T {
        return loadConfig()
    }

}