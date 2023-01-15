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

package app.simplecloud.simplecloud.module.api.image

import java.util.concurrent.CompletableFuture

/**
 * Date: 06.01.23
 * Time: 23:52
 * @author Frederick Baier
 *
 */
interface DelayedImageProvider {

    /**
     * This function builds an image and returns the image name as a string
     */
    fun buildImage(localRegistryAddr: String, localBuildkitAddr: String): CompletableFuture<String>

    /**
     * Cancels the build if already triggered
     */
    fun cancel()

}