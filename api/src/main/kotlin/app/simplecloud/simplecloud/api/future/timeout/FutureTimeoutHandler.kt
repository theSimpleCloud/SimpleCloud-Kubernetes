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

package app.simplecloud.simplecloud.api.future.timeout

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.exception.TimeoutException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 13:03
 * @author Frederick Baier
 */
object FutureTimeoutHandler {

    private val futureTimeouts = CopyOnWriteArrayList<FutureTimeout>()

    init {
        thread(true, true) {
            while (true) {
                checkTimeouts()
                Thread.sleep(12)
            }
        }
    }

    private fun checkTimeouts() {
        for (futureTimeout in futureTimeouts) {
            if (futureTimeout.isTimedOut()) {
                futureTimeouts.remove(futureTimeout)
                val future = futureTimeout.future
                if (!future.isDone) {
                    val future2 = future as CloudCompletableFuture
                    val originException = future2.originException
                    future2.completeExceptionally(TimeoutException(originException))
                }
            }
        }
    }

    fun addFuture(future: CompletableFuture<*>, timeout: Long) {
        futureTimeouts.add(FutureTimeout(future, timeout))
    }

    class FutureTimeout(
        val future: CompletableFuture<*>,
        val timeout: Long,
        val initTimeStamp: Long = System.currentTimeMillis()
    ) {

        fun isTimedOut(): Boolean {
            return System.currentTimeMillis() > (initTimeStamp + timeout)
        }

    }

}

fun CompletableFuture<*>.timout(milliseconds: Long) {
    FutureTimeoutHandler.addFuture(this, milliseconds)
}