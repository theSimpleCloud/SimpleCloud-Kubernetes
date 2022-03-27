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

package app.simplecloud.simplecloud.node.startup.setup.task

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.node.repository.mongo.player.CloudPlayerEntity
import app.simplecloud.simplecloud.node.repository.mongo.player.MongoCloudPlayerRepository
import app.simplecloud.simplecloud.node.startup.setup.body.FirstUserSetupResponseBody
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import app.simplecloud.simplecloud.restserver.setup.type.Setup
import com.google.common.hash.Hashing
import org.apache.logging.log4j.LogManager
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * Date: 07/08/2021
 * Time: 00:06
 * @author Frederick Baier
 */
class FirstWebUserSetup(
    private val restSetupManager: RestSetupManager,
    private val playerRepository: MongoCloudPlayerRepository,
    private val jwtTokenHandler: JwtTokenHandler
) {

    fun executeSetup() {
        logger.info("Executing First User Setup")
        val responseBody = this.restSetupManager.setNextSetup(createFirstUserSetup()).join()
        setEndToken(responseBody)
        saveResponseToMongoDatabase(responseBody)
    }

    private fun setEndToken(responseBody: FirstUserSetupResponseBody) {
        val expireDate = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
        val token = this.jwtTokenHandler.makeToken(
            JwtTokenHandler.TokenData(JwtTokenHandler.TokenMode.PLAYER, responseBody.uniqueId),
            expireDate
        )
        this.restSetupManager.setEndToken(token)
    }

    private fun createFirstUserSetup(): Setup<FirstUserSetupResponseBody> {
        return Setup("firstuser", "", FirstUserSetupResponseBody::class)
    }

    private fun saveResponseToMongoDatabase(response: FirstUserSetupResponseBody) {
        val entity = createPlayerEntityFromResponseBody(response)
        this.playerRepository.save(response.uniqueId, entity).exceptionally { it.printStackTrace() }
    }

    private fun createPlayerEntityFromResponseBody(response: FirstUserSetupResponseBody): CloudPlayerEntity {
        val passwordHash = Hashing.sha512().hashString(response.password, StandardCharsets.UTF_8).toString()
        return CloudPlayerEntity(
            response.uniqueId,
            response.playerName,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            0L,
            response.playerName,
            PlayerConnectionConfiguration(
                response.uniqueId,
                -1,
                response.playerName,
                Address("127.0.0.1", -1),
                true
            ),
            PlayerWebConfig(passwordHash, true),
            PermissionPlayerConfiguration(
                response.uniqueId,
                listOf(
                    PermissionConfiguration("*", true, -1, null)
                )
            )
        )
    }

    companion object {
        private val logger = LogManager.getLogger(FirstWebUserSetup::class.java)
    }

}