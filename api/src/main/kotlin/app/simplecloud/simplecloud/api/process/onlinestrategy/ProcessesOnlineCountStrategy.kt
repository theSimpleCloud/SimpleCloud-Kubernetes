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

package app.simplecloud.simplecloud.api.process.onlinestrategy

import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.utils.Identifiable
import app.simplecloud.simplecloud.api.utils.Nameable

/**
 * Created by IntelliJ IDEA.
 * Date: 16.03.2021
 * Time: 19:50
 * @author Frederick Baier
 *
 * Describes how many processes shall be online
 *
 */
interface ProcessesOnlineCountStrategy : Nameable, Identifiable<String> {

    /**
     * Returns the names of the groups to which this strategy should be applied
     */
    fun getTargetGroupNames(): Set<String>

    /**
     * Returns the amount of processes that should be online at the moment the method gets called
     * According to the returned value processes will be stopped or started
     */
    fun calculateOnlineCount(group: CloudProcessGroup): Int

    /**
     * Returns the configuration of this strategy
     */
    fun toConfiguration(): ProcessOnlineCountStrategyConfiguration

    override fun getIdentifier(): String {
        return getName()
    }

    interface Factory {

        fun create(configuration: ProcessOnlineCountStrategyConfiguration): ProcessesOnlineCountStrategy

    }

}