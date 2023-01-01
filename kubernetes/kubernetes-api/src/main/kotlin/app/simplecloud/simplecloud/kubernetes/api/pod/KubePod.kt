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

package app.simplecloud.simplecloud.kubernetes.api.pod

import app.simplecloud.simplecloud.kubernetes.api.Label

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 19:11
 * @author Frederick Baier
 */
interface KubePod {

    /**
     * Returns the name of this container
     */
    fun getName(): String

    /**
     * Executes the specified [command]
     */
    fun execute(command: String)

    /**
     * Starts this container
     */
    fun start(podSpec: PodSpec)

    /**
     * Returns weather the pod is active
     */
    fun isActive(): Boolean

    /**
     * Returns weather the pod is failed
     */
    fun isFailed(): Boolean

    /**
     * Shuts this container down
     */
    fun delete()

    /**
     * Shuts this container down immediately
     */
    fun forceShutdown()

    /**
     * Returns whether this container is running
     */
    fun exists(): Boolean

    /**
     * Returns the logs saved
     */
    fun getLogs(): String

    /**
     * Returns the labels of this pod
     */
    fun getLabels(): List<Label>


    interface Factory {

        /**
         * Creates a container
         */
        fun create(
            name: String
        ): KubePod

    }

}