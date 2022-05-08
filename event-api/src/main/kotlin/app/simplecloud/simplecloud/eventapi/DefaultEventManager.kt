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

package app.simplecloud.simplecloud.eventapi

import app.simplecloud.simplecloud.eventapi.exception.EventException
import com.google.common.collect.Maps
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

class DefaultEventManager : EventManager {

    private val listeners = Maps.newConcurrentMap<Class<out Event>, MutableList<RegisteredEventHandler>>()

    override fun registerListener(registerer: EventRegisterer, listener: Listener) {
        for (method in getValidMethods(listener::class.java)) {
            val eventClass = method.parameterTypes[0] as Class<out Event>
            addRegisteredEvent(RegisteredEventHandler.fromEventMethod(registerer, eventClass, listener, method))
        }
    }

    override fun registerEvent(
        registerer: EventRegisterer,
        eventClass: Class<out Event>,
        listener: Listener,
        eventExecutor: EventExecutor
    ) {
        addRegisteredEvent(RegisteredEventHandler(registerer, eventClass, listener, eventExecutor))
    }

    override fun unregisterListener(listener: Listener) {
        val allRegisteredEventHandlers = this.listeners.values.flatten()
        val handlersToUnregister = allRegisteredEventHandlers.filter { it.listener === listener }
        handlersToUnregister.forEach { removeRegisteredEvent(it) }
    }

    override fun call(event: Event) {
        this.listeners[event::class.java]?.forEach { registeredEvent ->
            registeredEvent.eventExecutor.execute(event)
        }
    }

    override fun unregisterAllListenersByRegisterer(registerer: EventRegisterer) {
        listeners.values.forEach { list -> list.removeIf { it.registerer == registerer } }
    }

    override fun unregisterAll() {
        this.listeners.clear()
    }


    private fun getValidMethods(listenerClass: Class<out Listener>): List<Method> {
        val methods = listenerClass.declaredMethods
            .filter {
                it.isAnnotationPresent(CloudEventHandler::class.java) && it.parameterTypes.size == 1 && Event::class.java.isAssignableFrom(
                    it.parameterTypes[0]
                )
            }
        methods.forEach { it.isAccessible = true }
        return methods
    }


    private fun addRegisteredEvent(registeredEventHandler: RegisteredEventHandler) {
        this.listeners.getOrPut(registeredEventHandler.eventClass, { CopyOnWriteArrayList() }).add(registeredEventHandler)
    }


    private fun removeRegisteredEvent(registeredEventHandler: RegisteredEventHandler) {
        this.listeners[registeredEventHandler.eventClass]?.remove(registeredEventHandler)
    }


    data class RegisteredEventHandler(
        val registerer: EventRegisterer,
        val eventClass: Class<out Event>,
        val listener: Listener,
        val eventExecutor: EventExecutor
    ) {

        companion object {

            fun fromEventMethod(
                registerer: EventRegisterer,
                eventClass: Class<out Event>,
                listener: Listener,
                method: Method
            ): RegisteredEventHandler {
                return RegisteredEventHandler(registerer, eventClass, listener, object : EventExecutor {

                    override fun execute(event: Event) {
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
