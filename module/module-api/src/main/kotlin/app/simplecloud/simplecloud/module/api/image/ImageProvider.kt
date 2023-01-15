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

import app.simplecloud.simplecloud.api.template.ProcessTemplate
import kotlin.time.Duration

/**
 * Date: 06.01.23
 * Time: 23:47
 * @author Frederick Baier
 *
 */
interface ImageProvider {

    fun provideImage(template: ProcessTemplate): ImageProvisionResult

    /**
     * Result of [provideImage]
     */
    sealed interface ImageProvisionResult

    /**
     * Result of [provideImage]
     * Used when the image was already built
     * @param imageName the already built image
     */
    class ImmediateProvisionResult(val imageName: String) : ImageProvisionResult

    /**
     *  Result of [provideImage]
     *  Used when the image needs to be built or fetched from a server and is unknown at the moment
     *  @param delayedImageProvider
     *  @param timeout
     */
    class DelayedProvisionResult(val delayedImageProvider: DelayedImageProvider, val timeout: Duration) :
        ImageProvisionResult

    /**
     * Result of [provideImage]
     * Used when no image can be provided
     */
    object EmptyProvisionResult : ImageProvisionResult

}