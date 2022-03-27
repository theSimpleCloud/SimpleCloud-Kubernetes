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

package app.simplecloud.simplecloud.kubernetes.api.image

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 12/08/2021
 * Time: 08:43
 * @author Frederick Baier
 */
class ImageBuildInstructions {

    val commandInstructions = CopyOnWriteArrayList<Instructions>()

    fun from(imageName: String): ImageBuildInstructions {
        this.commandInstructions.add(Instructions("FROM", imageName))
        return this
    }

    fun copy(from: String, to: String): ImageBuildInstructions {
        this.commandInstructions.add(Instructions("COPY", "$from $to"))
        return this
    }

    fun run(command: String): ImageBuildInstructions {
        this.commandInstructions.add(Instructions("RUN", command))
        return this
    }

    fun expose(port: Int): ImageBuildInstructions {
        this.commandInstructions.add(Instructions("EXPOSE", port.toString()))
        return this
    }

    fun workdir(workdir: String): ImageBuildInstructions {
        this.commandInstructions.add(Instructions("WORKDIR", workdir))
        return this
    }

    fun cmd(vararg commandArray: String): ImageBuildInstructions {
        val value = commandArray.joinToString("\", \"", "[\"", "\"]")
        this.commandInstructions.add(Instructions("CMD", value))
        return this
    }

    fun getAllInstructions(): List<String> {
        return this.commandInstructions.map { it.commandName + " " + it.commandValue }
    }

    class Instructions(
        val commandName: String,
        val commandValue: String
    )


}