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

package app.simplecloud.simplecloud.module.api.impl.resourcedefinition.builder

import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.ResourceDefinitionImpl
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionConverter
import app.simplecloud.simplecloud.module.api.resourcedefinition.builder.ResourceDefinitionBuilder
import app.simplecloud.simplecloud.module.api.resourcedefinition.builder.ResourceVersionBuilder
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 19.01.23
 * Time: 13:14
 * @author Frederick Baier
 *
 */
class ResourceDefinitionBuilderImpl : ResourceDefinitionBuilder {

    @Volatile
    private var group: String? = null

    @Volatile
    private var kind: String? = null

    @Volatile
    private var defaultVersion: ResourceVersion? = null

    private val versions = CopyOnWriteArrayList<ResourceVersion>()

    private val versionConverters = CopyOnWriteArrayList<ResourceVersionConverter<*, *>>()


    override fun setGroup(group: String): ResourceDefinitionBuilder {
        this.group = group
        return this
    }

    override fun setKind(kind: String): ResourceDefinitionBuilder {
        this.kind = kind
        return this
    }

    override fun addVersion(resourceVersion: ResourceVersion): ResourceDefinitionBuilder {
        this.versions.add(resourceVersion)
        return this
    }

    override fun addVersionAsDefaultVersion(resourceVersion: ResourceVersion): ResourceDefinitionBuilder {
        addVersion(resourceVersion)
        this.defaultVersion = resourceVersion
        return this
    }

    override fun addVersionConverter(resourceVersionConverter: ResourceVersionConverter<*, *>): ResourceDefinitionBuilder {
        this.versionConverters.add(resourceVersionConverter)
        return this
    }

    override fun build(): ResourceDefinition {
        if (this.group == null)
            throw IllegalArgumentException("Group of ResourceDefinition must not be null")
        if (this.kind == null)
            throw IllegalArgumentException("Kind of ResourceDefinition must not be null")
        if (this.versions.isEmpty())
            throw IllegalArgumentException("There must be at least one version")
        if (this.defaultVersion == null)
            throw IllegalArgumentException("There must be a default version")
        return ResourceDefinitionImpl(
            this.group!!,
            this.kind!!,
            this.versions,
            this.defaultVersion!!,
            this.versionConverters
        )
    }

    override fun newResourceVersionBuilder(): ResourceVersionBuilder {
        return ResourceVersionBuilderImpl()
    }
}