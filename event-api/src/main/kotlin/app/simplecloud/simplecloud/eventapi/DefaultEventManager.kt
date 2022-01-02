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

package app.simplecloud.simplecloud.eventapi

import app.simplecloud.simplecloud.eventapi.exception.EventException
import com.google.common.collect.Maps
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

class DefaultEventManager : IEventManager {

    private val listeners = Maps.newConcurrentMap<Class<out IEvent>, MutableList<RegisteredEventHandler>>()

    override fun registerListener(registerer: IEventRegisterer, listener: IListener) {
        for (method in getValidMethods(listener::class.java)) {
            val eventClass = method.parameterTypes[0] as Class<out IEvent>
            addRegisteredEvent(RegisteredEventHandler.fromEventMethod(registerer, eventClass, listener, method))
        }
    }

    override fun registerEvent(registerer: IEventRegisterer, eventClass: Class<out IEvent>, listener: IListener, eventExecutor: IEventExecutor) {
        addRegisteredEvent(RegisteredEventHandler(registerer, eventClass, listener, eventExecutor))
    }

    override fun unregisterListener(listener: IListener) {
        val allRegisteredEventHandlers = this.listeners.values.flatten()
        val handlersToUnregister = allRegisteredEventHandlers.filter { it.listener === listener }
        handlersToUnregister.forEach { removeRegisteredEvent(it) }
    }

    override fun call(event: IEvent) {
        this.listeners[event::class.java]?.forEach { registeredEvent ->
            registeredEvent.eventExecutor.execute(event)
        }
    }

    override fun unregisterAllListenersByRegisterer(registerer: IEventRegisterer) {
        listeners.values.forEach { list -> list.removeIf { it.registerer == registerer } }
    }

    override fun unregisterAll() {
        this.listeners.clear()
    }


    private fun getValidMethods(listenerClass: Class<out IListener>): List<Method> {
        val methods = listenerClass.declaredMethods
            .filter { it.isAnnotationPresent(CloudEventHandler::class.java) && it.parameterTypes.size == 1 && IEvent::class.java.isAssignableFrom(it.parameterTypes[0]) }
        methods.forEach { it.isAccessible = true }
        return methods
    }


    private fun addRegisteredEvent(registeredEventHandler: RegisteredEventHandler) {
        this.listeners.getOrPut(registeredEventHandler.eventClass, { CopyOnWriteArrayList() }).add(registeredEventHandler)
    }


    private fun removeRegisteredEvent(registeredEventHandler: RegisteredEventHandler) {
        this.listeners[registeredEventHandler.eventClass]?.remove(registeredEventHandler)
    }


    data class RegisteredEventHandler(val registerer: IEventRegisterer, val eventClass: Class<out IEvent>, val listener: IListener, val eventExecutor: IEventExecutor) {

        companion object {

            fun fromEventMethod(registerer: IEventRegisterer, eventClass: Class<out IEvent>, listener: IListener, method: Method): RegisteredEventHandler {
                return RegisteredEventHandler(registerer, eventClass, listener, object : IEventExecutor {

                    override fun execute(event: IEvent) {
                        if (!eventClass.isAssignableFrom(event.javaClass))
                            return
                        try {
                            method.invoke(listener, event)
                        } catch (ex: Exception) {
                            throw EventException(event, ex)
                        }
                    }
                })
            }
        }

    }

}
