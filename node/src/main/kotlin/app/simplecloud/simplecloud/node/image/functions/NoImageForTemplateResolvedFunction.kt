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

package app.simplecloud.simplecloud.node.image.functions

import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.module.api.NodeCloudAPI
import app.simplecloud.simplecloud.module.api.error.Error
import app.simplecloud.simplecloud.module.api.error.ResolveFunction
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.23
 * Time: 11:04
 * @author Frederick Baier
 *
 */
class NoImageForTemplateResolvedFunction : ResolveFunction {

    override fun isResolved(error: Error, nodeCloudAPI: NodeCloudAPI): CompletableFuture<Boolean> {
        val templateName = error.getErrorData()["template"] as String
        val isStatic = error.getErrorData()["static"] as Boolean
        val templateFuture = if (isStatic) {
            nodeCloudAPI.getStaticProcessTemplateService().findByName(templateName)
        } else {
            nodeCloudAPI.getProcessGroupService().findByName(templateName)
        }
        return templateFuture.thenApply { hasImage(it) }
    }

    private fun hasImage(template: ProcessTemplate): Boolean {
        try {
            template.getImage()
            return true
        } catch (e: Exception) {
            return false
        }
    }

}