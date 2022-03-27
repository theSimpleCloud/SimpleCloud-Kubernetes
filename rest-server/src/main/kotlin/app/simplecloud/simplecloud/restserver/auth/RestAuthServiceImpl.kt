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

