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

package app.simplecloud.simplecloud.restserver.impl.auth

import app.simplecloud.simplecloud.restserver.api.auth.Headers
import app.simplecloud.simplecloud.restserver.api.auth.token.TokenHandler
import app.simplecloud.simplecloud.restserver.base.exception.UnauthorizedException
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

/**
 * Date: 15.03.22
 * Time: 11:24
 * @author Frederick Baier
 *
 */
class JwtTokenHandler(
    private val secret: String,
) : TokenHandler {
    private val algorithm = Algorithm.HMAC512(secret)

    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        //.withIssuer(issuer)
        .build()

    override fun verifyToken(headers: Headers): TokenHandler.TokenData {
        try {
            return verify0(headers)
        } catch (ex: Exception) {
            throw UnauthorizedException()
        }
    }

    private fun verify0(headers: Headers): TokenHandler.TokenData {
        if (!headers.hasHeader("Authorization")) throw UnauthorizedException()
        val authHeader = headers.getHeader("Authorization")
        if (!authHeader.startsWith("Bearer ")) throw UnauthorizedException()
        val token = authHeader.replaceFirst("Bearer ", "")
        val decodedJWT = this.verifier.verify(token)
        return createTokenDataFromDecodedJWT(decodedJWT)
    }

    private fun createTokenDataFromDecodedJWT(decodedJWT: DecodedJWT): TokenHandler.TokenData {
        val mode = decodedJWT.getClaim("mode").asString()
        val id = decodedJWT.getClaim("id").asString()
        return TokenHandler.TokenData(TokenHandler.TokenMode.valueOf(mode), UUID.fromString(id))
    }

    /**
     * Produce a token for this combination of User and Account
     */
    override fun makeToken(data: TokenHandler.TokenData, expireDate: Date): String = JWT.create()
        .withSubject("Authentication")
        //.withIssuer(issuer)
        .withClaim("mode", data.tokenMode.name)
        .withClaim("id", data.playerUniqueId.toString())
        .withExpiresAt(expireDate)
        .sign(algorithm)

}