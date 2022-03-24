/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.node.startup.setup.task

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.node.mongo.player.CloudPlayerEntity
import app.simplecloud.simplecloud.node.mongo.player.MongoCloudPlayerRepository
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