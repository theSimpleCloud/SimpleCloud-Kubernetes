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


interface EventManager {

    /**
     * Registers a listener
     */
    fun registerListener(eventRegisterer: EventRegisterer, listener: Listener)

    /**
     * Registers an event
     */
    fun registerEvent(
        eventRegisterer: EventRegisterer,
        eventClass: Class<out Event>,
        listener: Listener,
        eventExecutor: EventExecutor
    )

    /**
     * Unregisters the all [EventExecutor]s associated with the specified [listener]
     */
    fun unregisterListener(listener: Listener)

    /**
     * Calls the specified [event] so it will be handed to the executors registered
     */
    fun call(event: Event)

    /**
     * Unregisters all [Listener]s associated with the specified [EventRegisterer]
     */
    fun unregisterAllListenersByRegisterer(registerer: EventRegisterer)

    /**
     * Unregisters all listeners
     */
    fun unregisterAll()

}
