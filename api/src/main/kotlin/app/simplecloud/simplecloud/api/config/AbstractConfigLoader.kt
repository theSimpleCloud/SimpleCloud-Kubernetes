/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.api.config

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