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

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.node.mongo.player.CloudPlayerEntity
import app.simplecloud.simplecloud.node.mongo.player.MongoCloudPlayerRepository
import app.simplecloud.simplecloud.node.startup.setup.body.FirstUserSetupResponseBody
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import app.simplecloud.simplecloud.restserver.setup.type.Setup
import com.ea.async.Async.await
import com.google.common.hash.Hashing
import org.apache.logging.log4j.LogManager
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * Date: 07/08/2021
 * Time: 00:06
 * @author Frederick Baier
 */
class FirstWebUserSetupTask(
    private val restSetupManager: RestSetupManager,
    private val playerRepository: MongoCloudPlayerRepository,
    private val jwtTokenHandler: JwtTokenHandler
) {

    fun run(): CompletableFuture<Unit> {
        logger.info("Executing First User Setup")
        val setupFuture = this.restSetupManager.setNextSetup(createFirstUserSetup())
        val responseBody = await(setupFuture)
        setEndToken(responseBody)
        saveResponseToMongoDatabase(responseBody)
        return unitFuture()
    }

    private fun setEndToken(responseBody: FirstUserSetupResponseBody) {
        val expireDate = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
        this.restSetupManager.setEndToken(
            this.jwtTokenHandler.makeToken(
                JwtTokenHandler.TokenData(JwtTokenHandler.TokenMode.PLAYER, responseBody.uniqueId),
                expireDate
            )
        )
    }

    private fun createFirstUserSetup(): Setup<FirstUserSetupResponseBody> {
        return Setup("firstuser", "", FirstUserSetupResponseBody::class)
    }

    private fun saveResponseToMongoDatabase(response: FirstUserSetupResponseBody) {
        val entity = createPlayerEntityFromResponseBody(response)
        this.playerRepository.save(response.uniqueId, entity)
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
            PlayerWebConfig(passwordHash, true)
        )
    }

    companion object {
        private val logger = LogManager.getLogger(FirstWebUserSetupTask::class.java)
    }

}