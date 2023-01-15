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
import app.simplecloud.simplecloud.distribution.api.Cache
import app.simplecloud.simplecloud.module.api.image.DelayedImageProvider
import app.simplecloud.simplecloud.module.api.image.ImageApplier
import app.simplecloud.simplecloud.module.api.image.ImageProvider
import java.util.concurrent.TimeUnit

class DelayedImageResultHandler(
    private val template: ProcessTemplate,
    private val delayedImageResult: ImageProvider.DelayedProvisionResult,
    private val environmentVariables: EnvironmentVariables,
    private val imageApplier: ImageApplier,
    private val cacheHandler: CacheHandler,
) {

    fun handle() {
        if (isAlreadyProviding())
            return

        provideImage()
    }

    private fun isAlreadyProviding(): Boolean {
        val buildCache = getCache()
        return buildCache.containsKey(getTemplateName())
    }

    private fun setProviding() {
        val buildCache = getCache()
        buildCache.set(getTemplateName(), true)
    }

    private fun getCache(): Cache<String, Boolean> {
        return this.cacheHandler.getOrCreateCache<String, Boolean>("cloud-image-build")
    }

    private fun provideImage() {
        setProviding()
        val timeoutInMs = delayedImageResult.timeout.inWholeMilliseconds
        val buildKitAddr = this.environmentVariables.get("BUILDKIT_ADDR")
        val registryAddr = this.environmentVariables.get("REBUILD_REGISTRY")
        val delayedImageProvider = delayedImageResult.delayedImageProvider
        val shutdownHookThread = registerShutdownHook(delayedImageProvider)
        val imageNameFuture = delayedImageProvider.buildImage(registryAddr, buildKitAddr)
        val futureWithTimout = imageNameFuture.orTimeout(timeoutInMs, TimeUnit.MILLISECONDS)
        futureWithTimout.handle { _, _ -> unregisterShutdownHook(shutdownHookThread) }
        futureWithTimout
            .thenApply { applyImage(it) }
            .exceptionally { handleBuildFailure() }
    }

    private fun handleBuildFailure() {

    }

    private fun applyImage(imageName: String) {
        this.imageApplier.applyImage(this.template, imageName)
    }

    private fun registerShutdownHook(delayedImageProvider: DelayedImageProvider): Thread {
        val thread = Thread {
            delayedImageProvider.cancel()
        }
        Runtime.getRuntime().addShutdownHook(thread)
        return thread
    }

    private fun unregisterShutdownHook(thread: Thread) {
        Runtime.getRuntime().removeShutdownHook(thread)
    }


    private fun getTemplateName(): String {
        if (this.template.isStatic())
            return "static-" + template.getName()
        return this.template.getName()
    }


}
