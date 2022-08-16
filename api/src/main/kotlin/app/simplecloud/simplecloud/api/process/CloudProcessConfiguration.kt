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

package app.simplecloud.simplecloud.api.process

import app.simplecloud.simplecloud.api.process.group.ProcessTemplateType
import app.simplecloud.simplecloud.api.process.state.ProcessState
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 09/07/2021
 * Time: 13:06
 * @author Frederick Baier
 */
data class CloudProcessConfiguration(
    val processTemplateName: String,
    val uniqueId: UUID,
    val processNumber: Int,
    val state: ProcessState,
    val visible: Boolean,
    val maxMemory: Int,
    val usedMemory: Int,
    val maxPlayers: Int,
    val onlinePlayers: Int,
    val static: Boolean,
    val processTemplateType: ProcessTemplateType,
    val imageName: String,
    val distributionId: UUID?,
) : java.io.Serializable {

    fun getProcessName(): String {
        if (this.static) {
            return this.processTemplateName
        }
        return this.processTemplateName + "-" + this.processNumber
    }

}