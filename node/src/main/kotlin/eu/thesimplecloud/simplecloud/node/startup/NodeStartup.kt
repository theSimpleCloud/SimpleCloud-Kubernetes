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

package eu.thesimplecloud.simplecloud.node.startup

import com.google.inject.Injector
import dev.morphia.Datastore
import eu.thesimplecloud.simplecloud.node.connect.NodeClusterConnect
import eu.thesimplecloud.simplecloud.node.startup.task.NodeStartupTask
import eu.thesimplecloud.simplecloud.node.util.Logger


/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 10:23
 * @author Frederick Baier
 */
class NodeStartup(
    private val startArguments: NodeStartArgumentParserMain
) {

    fun start() {
        val injector = NodeStartupTask(this.startArguments).run().join()
        executeClusterConnect(injector)
    }

    private fun executeClusterConnect(injector: Injector) {
        try {
            executeClusterConnect0(injector)
        } catch (e: Exception) {
            Logger.error(e)
        }
    }

    private fun executeClusterConnect0(injector: Injector) {
        val task = NodeClusterConnect(
            injector,
            injector.getInstance(Datastore::class.java),
        )
        task.run().join()
    }

}