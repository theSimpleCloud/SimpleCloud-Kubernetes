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

package eu.thesimplecloud.simplecloud.task

import eu.thesimplecloud.simplecloud.task.executor.TaskExecutor
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

interface TaskExecutorService {

    fun getName(): String

    /**
     * Creates a submitter for this [TaskExecutorService]
     * @see [TaskExecutorService]
     */
    fun createSubmitter(name: String): TaskSubmitter

    /**
     * Submits a task directly to the [TaskExecutorService]
     */
    fun <T> submit(builtTask: BuiltTask<T>): CompletableFuture<T>

    /**
     * Returns running [TaskExecutor]s
     */
    fun getExecutingTasks(): List<TaskExecutor>

    /**
     * Shuts this [TaskExecutorService] down
     * All submitted tasks will be executes but no new tasks will be accepted
     * This method does not wait for all submitted tasks to complete. Use [awaitTermination] for this.
     */
    fun shutdown()

    /**
     * Blocks the current thread until all submitted tasks were completed
     */
    fun awaitTermination(timeout: Long, unit: TimeUnit)

}
