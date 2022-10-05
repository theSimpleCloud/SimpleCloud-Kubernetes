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

package app.simplecloud.simplecloud.module.api.impl

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock


/**
 * Date: 04.10.22
 * Time: 11:08
 * @author Frederick Baier
 *
 */
/**
 * A light wrapper around the [ThreadPoolExecutor]. It allows for you to pause execution and
 * resume execution when ready. It is very handy for games that need to pause.
 *
 * @author Matthew A. Johnston (warmwaffles)
 */
class PausableThreadPoolExecutor(
    corePoolSize: Int,
) : ScheduledThreadPoolExecutor(corePoolSize) {
    var isPaused = false
        private set
    private val lock: ReentrantLock
    private val condition: Condition

    /**
     * @param corePoolSize    The size of the pool
     * @param maximumPoolSize The maximum size of the pool
     * @param keepAliveTime   The amount of time you wish to keep a single task alive
     * @param unit            The unit of time that the keep alive time represents
     * @param workQueue       The queue that holds your tasks
     * @see {@link ThreadPoolExecutor.ThreadPoolExecutor
     */
    init {
        lock = ReentrantLock()
        condition = lock.newCondition()
    }

    /**
     * @param thread   The thread being executed
     * @param runnable The runnable task
     * @see {@link ThreadPoolExecutor.beforeExecute
     */
    override fun beforeExecute(thread: Thread, runnable: Runnable) {
        super.beforeExecute(thread, runnable)
        lock.lock()
        try {
            while (isPaused) condition.await()
        } catch (ie: InterruptedException) {
            thread.interrupt()
        } finally {
            lock.unlock()
        }
    }

    val isRunning: Boolean
        get() = !isPaused

    /**
     * Pause the execution
     */
    fun pause() {
        lock.lock()
        isPaused = try {
            true
        } finally {
            lock.unlock()
        }
    }

    /**
     * Resume pool execution
     */
    fun resume() {
        lock.lock()
        try {
            isPaused = false
            condition.signalAll()
        } finally {
            lock.unlock()
        }
    }
}