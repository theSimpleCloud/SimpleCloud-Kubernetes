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

package eu.thesimplecloud.simplecloud.task.executor

import eu.thesimplecloud.simplecloud.task.BuiltTask
import eu.thesimplecloud.simplecloud.task.Task
import eu.thesimplecloud.simplecloud.task.Timing
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitter
import org.tinylog.Logger
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 02/08/2021
 * Time: 11:05
 * @author Frederick Baier
 *
 * The [TaskExecutorImpl] executes a task, measures the time it took and handles errors
 */
class TaskExecutorImpl<T>(
    private val taskFuture: CompletableFuture<T>,
    private val builtTask: BuiltTask<T>
) : TaskExecutor, Runnable {

    private val timing = Timing()
    @Volatile
    private var taskExecutionState = TaskExecutionState.RUNNING

    override fun getExecutingTask(): Task<*> {
        return this.builtTask.task
    }

    override fun getTaskSubmitter(): TaskSubmitter {
        return this.builtTask.taskSubmitter
    }

    override fun getTimeExecuting(): Long {
        return this.timing.getTime()
    }

    override fun getTaskState(): TaskExecutionState {
        return this.taskExecutionState
    }

    override fun run() {
        logBegin()
        executeWithTiming()
    }

    private fun executeWithTiming() {
        this.timing.start()
        executeTask()
    }

    private fun executeTask() {
        try {
            val future = this.builtTask.execute()
            handleResult(future)
        } catch (ex: Exception) {
            this.taskFuture.completeExceptionally(ex)
            handleResult(this.taskFuture) //just pass the task future, because it failed with the cause
        }
    }

    private fun handleResult(future: CompletableFuture<T>) {
        future.handle { result, throwable ->
            onComplete(result, throwable)
        }
    }

    private fun onComplete(result: T?, throwable: Throwable?) {
        this.taskExecutionState = TaskExecutionState.DONE
        this.timing.stop()
        logEnd(throwable)
        handleCompleteResult(result, throwable)
    }

    private fun handleCompleteResult(result: T?, throwable: Throwable?) {
        //when a future completes with void its return value will be null
        // so checking the throwable for null is the only option to determine success or failure
        if (throwable == null) {
            try {
                this.taskFuture.complete(result)
            } catch (e: Exception) {
                Logger.error(e)
            }
        } else {
            this.taskFuture.completeExceptionally(throwable)
        }
    }


    private fun logBegin() {
        val taskSubmitter = this.builtTask.taskSubmitter
        Logger.info(
            "({}) Start '{}', Executor {}, Submitter {}",
            this.builtTask.id,
            this.builtTask.getTaskName(),
            taskSubmitter.getExecutorService().getName(),
            taskSubmitter.getName()
        )
    }

    private fun logEnd(throwable: Throwable?) {
        val successStatusText = if (throwable == null) "Success" else "Failed"
        val time = this.timing.getTime()

        val templateLogString = if (time > 3000)
            "({}) End '{}'. Took {} seconds. {}"
        else
            "({}) End '{}'. Took {} ms. {}"

        val timeValue = if (time > 3000)
            time / 1000
        else
            time

        if (throwable == null) {
            Logger.info(templateLogString, this.builtTask.id, this.builtTask.getTaskName(), timeValue, successStatusText)
            return
        }
        Logger.error(throwable, templateLogString, this.builtTask.id, this.builtTask.getTaskName(), timeValue, successStatusText)
    }

}