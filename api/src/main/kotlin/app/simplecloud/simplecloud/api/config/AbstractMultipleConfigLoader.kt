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

import app.simplecloud.simplecloud.api.utils.Nameable
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
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