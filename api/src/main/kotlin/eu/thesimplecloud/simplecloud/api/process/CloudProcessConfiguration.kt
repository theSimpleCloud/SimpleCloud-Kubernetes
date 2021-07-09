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

package eu.thesimplecloud.simplecloud.api.process

import eu.thesimplecloud.simplecloud.api.process.group.ProcessGroupType
import eu.thesimplecloud.simplecloud.api.process.state.ProcessState
import eu.thesimplecloud.simplecloud.api.utils.Address
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 09/07/2021
 * Time: 13:06
 * @author Frederick Baier
 */
data class CloudProcessConfiguration(
    val groupName: String,
    val uniqueId: UUID,
    val processNumber: Int,
    val state: ProcessState,
    val maxMemory: Int,
    val usedMemory: Int,
    val maxPlayers: Int,
    val address: Address,
    val static: Boolean,
    val processGroupType: ProcessGroupType,
    val versionName: String,
    val templateName: String,
    val nodeNameRunningOn: String,
    val jvmArgumentsName: String?,
    val igniteId: UUID?
)