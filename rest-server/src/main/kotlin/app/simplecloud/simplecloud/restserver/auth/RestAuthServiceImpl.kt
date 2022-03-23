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

package app.simplecloud.simplecloud.restserver.auth

import app.simplecloud.rest.Context
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.permission.EmptyPermissionEntity
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.restserver.base.exception.UnauthorizedException
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import app.simplecloud.simplecloud.restserver.base.service.UsernameAndPasswordCredentials
import com.google.common.hash.Hashing
import com.google.inject.Inject
import com.google.inject.Singleton
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * Date: 23.06.2021
 * Time: 14:52
 * @author Frederick Baier
 */
@Singleton
class RestAuthServiceImpl @Inject constructor(
    private val playerService: CloudPlayerService,
    private val tokenHandler: JwtTokenHandler
) : AuthService {

    override suspend fun authenticate(usernameAndPasswordCredentials: UsernameAndPasswordCredentials): String {
        val offlineCloudPlayer = this.playerService.findOfflinePlayerByName(usernameAndPasswordCredentials.username)
            .await()

        val hashedPassword = hashPassword(usernameAndPasswordCredentials.password)

        if (hashedPassword != offlineCloudPlayer.getWebConfig().password) {
            throw UnauthorizedException()
        }
        return generateTokenForPlayer(offlineCloudPlayer.getUniqueId())
    }

    private fun hashPassword(password: String): String {
        return Hashing.sha512()
            .hashString(password, StandardCharsets.UTF_8)
            .toString()
    }

    override suspend fun getRequestEntityFromContext(context: Context): PermissionEntity {
        val tokenData = this.tokenHandler.verify(context)
        if (tokenData.tokenMode == JwtTokenHandler.TokenMode.PLAYER) {
            return this.playerService.findOfflinePlayerByUniqueId(tokenData.playerUniqueId).await()
        }
        return EmptyPermissionEntity
    }

    private fun generateTokenForPlayer(uniqueId: UUID): String {
        val expireDate = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(10))
        return this.tokenHandler.makeToken(
            JwtTokenHandler.TokenData(JwtTokenHandler.TokenMode.PLAYER, uniqueId),
            expireDate
        )
    }

}

