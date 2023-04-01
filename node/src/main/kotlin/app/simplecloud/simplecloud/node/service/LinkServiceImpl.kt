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

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.resourcedefinition.link.LinkConfiguration
import app.simplecloud.simplecloud.database.api.DatabaseLinkRepository
import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.link.LinkDefinitionBuilderImpl
import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.link.LinkImpl
import app.simplecloud.simplecloud.module.api.internal.service.InternalLinkService
import app.simplecloud.simplecloud.module.api.resourcedefinition.link.Link
import app.simplecloud.simplecloud.module.api.resourcedefinition.link.LinkDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.link.LinkDefinitionBuilder
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.module.api.service.ResourceDefinitionService
import app.simplecloud.simplecloud.node.repository.distributed.DistributedLinkRepository
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 29.03.23
 * Time: 10:54
 * @author Frederick Baier
 *
 */
class LinkServiceImpl(
    private val distributedRepository: DistributedLinkRepository,
    private val databaseLinkRepository: DatabaseLinkRepository,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val requestHandler: ResourceRequestHandler,
) : InternalLinkService {

    private val linkDefinitions = CopyOnWriteArrayList<LinkDefinition>()

    override fun newLinkDefinitionBuilder(): LinkDefinitionBuilder {
        return LinkDefinitionBuilderImpl()
    }

    override fun createLink(linkDefinition: LinkDefinition) {
        this.linkDefinitions.add(linkDefinition)
    }

    override fun findLinkDefinitionByName(name: String): LinkDefinition {
        return this.linkDefinitions.firstOrNull { it.getName() == name }
            ?: throw NoSuchElementException("No LinkDefinition found by name '$name'")
    }

    override fun findAllLinkDefinitions(): List<LinkDefinition> {
        return this.linkDefinitions
    }

    override fun findLinksByDefinitionName(name: String): CompletableFuture<List<Link>> {
        val linkConfigs = this.distributedRepository.findByDefinitionName(name)
        return linkConfigs.thenApply { it.map { LinkImpl(it) } }
    }

    override suspend fun createLinkInternal(configuration: LinkConfiguration) {
        val linkDefinition = findLinkDefinitionByName(configuration.linkType)
        distributedRepository.save(configuration.linkType + "/" + configuration.oneResourceName, configuration).await()
        databaseLinkRepository.save(configuration)
        //updateEffectedResourcesCatching(configuration)
    }

    private fun updateResource(resourceGroup: String, resourceKind: String, resourceName: String) {
        val resourceDefinition = this.resourceDefinitionService.findResourceDefinition(resourceGroup, resourceKind)
        val defaultVersion = resourceDefinition.getDefaultVersion()
        val resourceSpec = this.requestHandler.handleGetOneSpec<Any>(
            resourceGroup,
            resourceKind,
            defaultVersion.getName(),
            resourceName
        )
        this.requestHandler.handleUpdate(
            resourceGroup,
            resourceKind,
            defaultVersion.getName(),
            resourceName,
            resourceSpec.getSpec()
        )
    }

    override suspend fun deleteLinkInternal(type: String, oneResourceName: String) {
        val identifier = "$type/$oneResourceName"
        val configuration = distributedRepository.find(identifier).await()
        distributedRepository.remove(identifier).await()
        databaseLinkRepository.delete(type, oneResourceName)
        //updateEffectedResourcesCatching(configuration)
    }

    private fun updateEffectedResourcesCatching(configuration: LinkConfiguration) {
        try {
            updateEffectedResources(configuration)
        } catch (ex: Exception) {
            //resources could not be found and not be updated
            ex.printStackTrace()
        }
    }

    private fun updateEffectedResources(configuration: LinkConfiguration) {
        val linkDefinition = findLinkDefinitionByName(configuration.linkType)
        updateResource(
            linkDefinition.getOneResourceGroup(),
            linkDefinition.getOneResourceKind(),
            configuration.oneResourceName
        )
        updateResource(
            linkDefinition.getManyResourceGroup(),
            linkDefinition.getManyResourceKind(),
            configuration.manyResourceName
        )
    }

}