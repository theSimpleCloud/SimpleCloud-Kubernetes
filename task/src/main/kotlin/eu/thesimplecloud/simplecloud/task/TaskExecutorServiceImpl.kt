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
import eu.thesimplecloud.simplecloud.task.executor.TaskExecutorImpl
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitter
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitterImpl
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class TaskExecutorServiceImpl(
    private val name: String
) : TaskExecutorService {

    private val executorService = Executors.newCachedThreadPool()
    private val executingTasks = CopyOnWriteArrayList<TaskExecutor>()

    override fun getName(): String {
        return this.name
    }

    override fun createSubmitter(name: String): TaskSubmitter {
        return TaskSubmitterImpl(name, this)
    }

    override fun <T> submit(builtTask: BuiltTask<T>): CompletableFuture<T> {
        val taskFuture = CompletableFuture<T>()
        executeTask(taskFuture, builtTask)
        return taskFuture
    }

    override fun getExecutingTasks(): List<TaskExecutor> {
        return this.executingTasks
    }

    private fun <T> executeTask(taskFuture: CompletableFuture<T>, builtTask: BuiltTask<T>) {
        val taskExecutor = TaskExecutorImpl(taskFuture, builtTask)
        handleRegisterAndUnregisterOfTaskExecutor(taskExecutor, taskFuture)
        this.executorService.submit(taskExecutor)
    }

    private fun handleRegisterAndUnregisterOfTaskExecutor(taskExecutor: TaskExecutor, future: CompletableFuture<*>) {
        this.executingTasks.add(taskExecutor)
        future.thenAccept { this.executingTasks.remove(taskExecutor) }
    }

    override fun shutdown() {
        this.executorService.shutdown()
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit) {
        this.executorService.awaitTermination(timeout, unit)
    }

}
