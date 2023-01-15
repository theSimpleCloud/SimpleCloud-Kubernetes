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
import app.simplecloud.simplecloud.module.api.error.configuration.ErrorCreateConfiguration
import app.simplecloud.simplecloud.module.api.image.ImageApplier
import app.simplecloud.simplecloud.module.api.image.ImageProvider
import app.simplecloud.simplecloud.module.api.service.ErrorService
import app.simplecloud.simplecloud.node.image.functions.NoImageForTemplateResolvedFunction

class MissingImageHandler(
    private val template: ProcessTemplate,
    private val nameToImageProvider: Map<String, ImageProvider>,
    private val imageApplier: ImageApplier,
    private val errorService: ErrorService,
    private val environmentVariables: EnvironmentVariables,
    private val cacheHandler: CacheHandler,
) {


    private val provisionResults: List<ImageProvider.ImageProvisionResult> =
        this.nameToImageProvider.values.map { it.provideImage(this.template) }

    fun handleMissingImage() {
        val immediateProvisionResults = getImmediateProvisions()
        if (immediateProvisionResults.isNotEmpty()) {
            val firstResult = immediateProvisionResults.first()
            applyImage(firstResult.imageName)
            return
        }
        val delayedProvisionResults = getDelayedProvisionResults()
        val delayedImageResult = delayedProvisionResults.minByOrNull { it.timeout.inWholeMilliseconds }
        if (delayedImageResult == null) {
            createNoImageForTemplateError()
            return
        }
        handleDelayedImageResult(delayedImageResult)
    }

    private fun handleDelayedImageResult(delayedImageResult: ImageProvider.DelayedProvisionResult) {
        //DelayedImageResultHandler(delayedImageResult, environmentVariables, cacheHandler).handle()
    }

    private fun getDelayedProvisionResults(): List<ImageProvider.DelayedProvisionResult> {
        return this.provisionResults.filterIsInstance<ImageProvider.DelayedProvisionResult>()
    }

    private fun createNoImageForTemplateError() {
        val templateName = if (template.isStatic()) "static ${template.getName()}" else template.getName()
        this.errorService.createCreateRequest(
            ErrorCreateConfiguration(
                "Image for group / static template $templateName is missing and non of the registered image providers was able to provide an image!",
                "List of Image Providers: ${this.nameToImageProvider.keys}",
                "Cloud",
                mapOf(
                    "template" to this.template.getName(),
                    "static" to this.template.isStatic()
                ),
                NoImageForTemplateResolvedFunction()
            )
        ).submit()
    }

    private fun applyImage(imageName: String) {
        this.imageApplier.applyImage(this.template, imageName)
    }

    private fun getImmediateProvisions(): List<ImageProvider.ImmediateProvisionResult> {
        return this.provisionResults.filterIsInstance<ImageProvider.ImmediateProvisionResult>()
    }

}
