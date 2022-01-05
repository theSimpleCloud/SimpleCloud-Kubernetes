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

package app.simplecloud.simplecloud.kubernetes.api.service

import app.simplecloud.simplecloud.kubernetes.api.Label
import java.util.concurrent.CopyOnWriteArrayList

class ServiceSpec {

    val labels = CopyOnWriteArrayList<Label>()

    @Volatile
    var containerPort: Int = -1
        private set

    @Volatile
    var clusterPort: Int = -1
        private set

    @Volatile
    var publicPort: Int = -1
        private set

    fun withClusterPort(port: Int): ServiceSpec {
        this.clusterPort = port
        return this
    }

    fun withContainerPort(port: Int): ServiceSpec {
        this.containerPort = port
        return this
    }

    fun withPublicPort(port: Int): ServiceSpec {
        this.publicPort = port
        return this
    }

    fun withLabels(vararg labels: Label): ServiceSpec {
        this.labels.addAll(labels)
        return this
    }

}