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

package app.simplecloud.simplecloud.api.validator

import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * Created by IntelliJ IDEA.
 * Date: 12/07/2021
 * Time: 11:47
 * @author Frederick Baier
 */
@Singleton
class ValidatorServiceImpl @Inject constructor(
    private val groupConfigurationValidator: GroupConfigurationValidator
) : ValidatorService {

    private val clazzToValidator = mapOf<Class<*>, Validator<*>>(
        AbstractCloudProcessGroupConfiguration::class.java to groupConfigurationValidator,
    )

    override fun <T> getValidator(clazz: Class<T>): Validator<T> {
        val validationClass = clazzToValidator.keys.firstOrNull { it.isAssignableFrom(clazz) }
            ?: throw NoSuchElementException("Validator does not exist for clazz ${clazz.name}")
        return clazzToValidator[validationClass] as Validator<T>
    }

}