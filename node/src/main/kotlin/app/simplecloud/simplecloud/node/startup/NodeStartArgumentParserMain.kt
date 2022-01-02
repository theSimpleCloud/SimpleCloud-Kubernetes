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

package app.simplecloud.simplecloud.node.startup

import app.simplecloud.simplecloud.api.utils.Address
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 10:08
 * @author Frederick Baier
 */
class NodeStartArgumentParserMain : CliktCommand() {

    val webinterfaceMode: WebinterfaceMode by option(help = "Sets the mode for the webinterface")
        .enum<WebinterfaceMode>()
        .default(WebinterfaceMode.DOCKER)

    val mongoDbConnectionString: String? by option(help = "Sets the connection string for MongoDB")

    val bindAddress: Address? by option(help = "Sets the address for the node to bind to").convert { Address.fromIpString(it) }
    val maxMemory: Int? by option(help = "Let the node generate a random name").int()
    val randomNodeName: Boolean by option(help = "Let the node generate a random name").flag()


    override fun run() {
        NodeStartup(this).start()
    }

}