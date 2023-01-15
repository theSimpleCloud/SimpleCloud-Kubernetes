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

package app.simplecloud.simplecloud.node.image

import app.simplecloud.simplecloud.api.cache.CacheHandler
import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.module.api.image.ImageApplier
import app.simplecloud.simplecloud.module.api.image.ImageHandler
import app.simplecloud.simplecloud.module.api.image.ImageProvider
import app.simplecloud.simplecloud.module.api.service.ErrorService
import com.google.common.collect.Maps

/**
 * Date: 07.01.23
 * Time: 01:03
 * @author Frederick Baier
 *
 */
class ImageHandlerImpl(
    private val errorService: ErrorService,
    private val environmentVariables: EnvironmentVariables,
    private val cacheHandler: CacheHandler,
) : ImageHandler {

    @Volatile
    private var imageApplier: ImageApplier = DefaultImageApplier()

    private val nameToImageProvider = Maps.newConcurrentMap<String, ImageProvider>()

    override fun setImageApplier(imageApplier: ImageApplier) {
        this.imageApplier = imageApplier
    }

    override fun registerImageProvider(name: String, imageProvider: ImageProvider) {
        this.nameToImageProvider[name] = imageProvider
    }

    override fun getRegisteredImageProviderNames(): Collection<String> {
        return this.nameToImageProvider.keys
    }

    fun handleImageMissing(template: ProcessTemplate) {
        //check cluster cache for already building
        MissingImageHandler(
            template,
            this.nameToImageProvider,
            this.imageApplier,
            this.errorService,
            this.environmentVariables,
            this.cacheHandler
        ).handleMissingImage()
    }

}