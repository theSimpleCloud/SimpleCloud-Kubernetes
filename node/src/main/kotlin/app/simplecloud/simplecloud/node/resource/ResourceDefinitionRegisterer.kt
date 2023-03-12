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

package app.simplecloud.simplecloud.node.resource

import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinitionService
import app.simplecloud.simplecloud.node.connect.DistributedRepositories
import app.simplecloud.simplecloud.node.resource.group.*
import app.simplecloud.simplecloud.node.resource.onlinestrategy.V1Beta1OnlineCountStrategyPreProcessor
import app.simplecloud.simplecloud.node.resource.onlinestrategy.V1Beta1ProcessOnlineCountStrategySpec
import app.simplecloud.simplecloud.node.resource.permissiongroup.V1Beta1PermissionGroupPreProcessor
import app.simplecloud.simplecloud.node.resource.permissiongroup.V1Beta1PermissionGroupSpec
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerPreProcessor
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerSpec
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerStatus
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerStatusGeneration
import app.simplecloud.simplecloud.node.resource.staticserver.*

/**
 * Date: 07.03.23
 * Time: 12:53
 * @author Frederick Baier
 *
 */
class ResourceDefinitionRegisterer(
    private val resourceDefinitionService: ResourceDefinitionService,
    private val distributedRepositories: DistributedRepositories,
) {

    fun registerDefinitions() {
        registerLobbyGroupDefinition()
        registerProxyGroupDefinition()
        registerServerGroupDefinition()

        registerStaticLobbyDefinition()
        registerStaticProxyDefinition()
        registerStaticServerDefinition()

        registerPermissionGroupDefinition()
        registerOnlineCountStrategyDefinition()

        registerCloudPlayerDefinition()
    }

    private fun registerCloudPlayerDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("CloudPlayer")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1CloudPlayerSpec::class.java)
        v1VersionBuilder.setStatusSchemaClass(V1Beta1CloudPlayerStatus::class.java)
        v1VersionBuilder.setStatusGenerationFunction(V1Beta1CloudPlayerStatusGeneration(this.distributedRepositories.cloudPlayerRepository))
        v1VersionBuilder.setPreProcessor(
            V1Beta1CloudPlayerPreProcessor(this.distributedRepositories.cloudPlayerRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerOnlineCountStrategyDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("ProcessOnlineCountStrategy")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1ProcessOnlineCountStrategySpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1OnlineCountStrategyPreProcessor(this.distributedRepositories.distributedOnlineCountStrategyRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerPermissionGroupDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("PermissionGroup")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1PermissionGroupSpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1PermissionGroupPreProcessor(this.distributedRepositories.permissionGroupRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerStaticLobbyDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("StaticLobby")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1StaticLobbySpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1StaticLobbyPreProcessor(this.distributedRepositories.staticProcessTemplateRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerStaticProxyDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("StaticProxy")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1StaticProxySpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1StaticProxyPreProcessor(this.distributedRepositories.staticProcessTemplateRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerStaticServerDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("StaticServer")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1StaticServerSpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1StaticServerPreProcessor(this.distributedRepositories.staticProcessTemplateRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerLobbyGroupDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("LobbyGroup")

        val v1VersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1VersionBuilder.setName("v1beta1")
        v1VersionBuilder.setSpecSchemaClass(V1Beta1LobbyGroupSpec::class.java)
        v1VersionBuilder.setPreProcessor(
            V1Beta1LobbyGroupPreProcessor(this.distributedRepositories.cloudProcessGroupRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1VersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerProxyGroupDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("ProxyGroup")

        val v1ProxyVersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1ProxyVersionBuilder.setName("v1beta1")
        v1ProxyVersionBuilder.setSpecSchemaClass(V1Beta1ProxyGroupSpec::class.java)
        v1ProxyVersionBuilder.setPreProcessor(
            V1Beta1ProxyGroupPreProcessor(this.distributedRepositories.cloudProcessGroupRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1ProxyVersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

    private fun registerServerGroupDefinition() {
        val resourceBuilder = this.resourceDefinitionService.newResourceDefinitionBuilder()
        resourceBuilder.setGroup("core")
        resourceBuilder.setKind("ServerGroup")

        val v1ServerVersionBuilder = resourceBuilder.newResourceVersionBuilder()
        v1ServerVersionBuilder.setName("v1beta1")
        v1ServerVersionBuilder.setSpecSchemaClass(V1Beta1ServerGroupSpec::class.java)
        v1ServerVersionBuilder.setPreProcessor(
            V1Beta1ServerGroupPreProcessor(this.distributedRepositories.cloudProcessGroupRepository)
        )

        resourceBuilder.addVersionAsDefaultVersion(v1ServerVersionBuilder.build())
        this.resourceDefinitionService.createResource(resourceBuilder.build())
    }

}