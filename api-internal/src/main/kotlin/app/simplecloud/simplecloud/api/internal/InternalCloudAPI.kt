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

package app.simplecloud.simplecloud.api.internal

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 19:57
 * @author Frederick Baier
 */
abstract class InternalCloudAPI : CloudAPI() {

    init {
        instance = this
    }

    abstract override fun getProcessService(): InternalCloudProcessService

    abstract override fun getProcessGroupService(): InternalCloudProcessGroupService

    companion object {
        @JvmStatic
        lateinit var instance: InternalCloudAPI
            private set
    }

}