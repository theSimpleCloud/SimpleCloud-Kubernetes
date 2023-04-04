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

package app.simplecloud.simplecloud.node.startup.prepare

import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.api.resourcedefinition.Resource
import app.simplecloud.simplecloud.database.api.DatabaseResourceRepository
import app.simplecloud.simplecloud.module.api.service.ResourceDefinitionService
import app.simplecloud.simplecloud.node.resource.permissiongroup.V1Beta1PermissionConfiguration
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerSpec
import app.simplecloud.simplecloud.node.resource.player.V1Beta1PermissionPlayerConfiguration
import app.simplecloud.simplecloud.node.resource.player.V1Beta1PlayerConnectionConfiguration
import app.simplecloud.simplecloud.node.resource.player.V1Beta1PlayerWebConfig
import eu.thesimplecloud.jsonlib.JsonLib
import org.apache.logging.log4j.LogManager
import java.util.*

/**
 * Date: 02.04.23
 * Time: 18:43
 * @author Frederick Baier
 *
 */
class FirstUserChecker(
    private val databaseResourceRepository: DatabaseResourceRepository,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val envVariables: EnvironmentVariables,
) {

    private val playerResourceDefinition = resourceDefinitionService.findResourceDefinition("core", "CloudPlayer")
    private val playerVersionName = playerResourceDefinition.getDefaultVersion().getName()

    fun checkFirstUser() {
        val doesAnyPlayerExist = databaseResourceRepository.exists("core/${playerVersionName}", "CloudPlayer")
        if (!doesAnyPlayerExist) {
            createFirstUser()
        }
    }

    private fun createFirstUser() {
        logger.info("Creating first user from environment variables")
        val initUserName = this.envVariables.get("INIT_USER_NAME")
        val initUserUUIDString = this.envVariables.get("INIT_USER_UUID")
        val initPassword = this.envVariables.get("INIT_USER_PASSWORD")
        if (initUserName == null || initUserUUIDString == null || initPassword == null) {
            logger.warn("Cannot create first user: a required environment variable was not set")
            return
        }
        val parsedUUID = UUID.fromString(initUserUUIDString)
        val playerSpec = V1Beta1CloudPlayerSpec(
            initUserName,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            0L,
            initUserName,
            V1Beta1PlayerConnectionConfiguration(parsedUUID, -1, initUserName, "0.0.0.0", -1, true),
            V1Beta1PlayerWebConfig(initPassword, true),
            V1Beta1PermissionPlayerConfiguration(
                parsedUUID, arrayOf(
                    V1Beta1PermissionConfiguration(
                        "*",
                        true,
                        -1,
                        null
                    )
                )
            )
        )

        this.databaseResourceRepository.save(
            Resource(
                "core/${this.playerVersionName}",
                "CloudPlayer",
                parsedUUID.toString(),
                JsonLib.fromObject(playerSpec).getObject(Map::class.java) as Map<String, Any>
            )
        )
    }

    companion object {
        private val logger = LogManager.getLogger(FirstUserChecker::class.java)
    }

}