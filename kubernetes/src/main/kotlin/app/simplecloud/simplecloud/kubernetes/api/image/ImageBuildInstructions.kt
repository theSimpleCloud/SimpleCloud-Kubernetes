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