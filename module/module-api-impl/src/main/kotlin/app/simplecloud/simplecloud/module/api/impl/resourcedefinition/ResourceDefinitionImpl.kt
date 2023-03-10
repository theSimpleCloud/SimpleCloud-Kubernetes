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

package app.simplecloud.simplecloud.module.api.impl.resourcedefinition

import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionConverter

/**
 * Date: 19.01.23
 * Time: 14:07
 * @author Frederick Baier
 *
 */
class ResourceDefinitionImpl(
    private val group: String,
    private val kind: String,
    private val versions: List<ResourceVersion>,
    private val defaultVersion: ResourceVersion,
    private val versionConverters: List<ResourceVersionConverter<*, *>>,
) : ResourceDefinition {

    override fun getGroup(): String {
        return this.group
    }

    override fun getKind(): String {
        return this.kind
    }

    override fun getVersions(): List<ResourceVersion> {
        return this.versions
    }

    override fun getVersionByName(name: String): ResourceVersion {
        return this.versions.firstOrNull { it.getName() == name }
            ?: throw NoSuchElementException("ResourceVersion does not exist")
    }

    override fun getDefaultVersion(): ResourceVersion {
        return this.defaultVersion
    }

    override fun getVersionConverterFromVersionToDefaultVersion(resourceVersion: ResourceVersion): ResourceVersionConverter<Any, Any> {
        if (resourceVersion == this.defaultVersion)
            return IdentityResourceVersionConverter<Any>(resourceVersion.getName())
        val versionConverter = this.versionConverters.firstOrNull {
            it.getNewerVersionName() == this.defaultVersion.getName() && it.getOlderVersionName() == resourceVersion.getName()
        }
        versionConverter ?: throw NoSuchElementException(
            "Cannot find converter from version '${resourceVersion.getName()}' to default version '${this.defaultVersion.getName()}'"
        )
        return versionConverter as ResourceVersionConverter<Any, Any>
    }

}