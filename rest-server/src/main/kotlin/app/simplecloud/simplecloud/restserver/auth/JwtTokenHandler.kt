package app.simplecloud.simplecloud.restserver.auth

import app.simplecloud.rest.Context
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
) {
    private val algorithm = Algorithm.HMAC512(secret)

    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        //.withIssuer(issuer)
        .build()

    fun verify(context: Context): TokenData {
        try {
            return verify0(context)
        } catch (ex: Exception) {
            throw UnauthorizedException()
        }
    }

    private fun verify0(context: Context): TokenData {
        if (!context.hasRequestHeader("Authorization")) throw UnauthorizedException()
        val authHeader = context.getRequestHeader("Authorization")
        if (!authHeader.startsWith("Bearer ")) throw UnauthorizedException()
        val token = authHeader.replaceFirst("Bearer ", "")
        val decodedJWT =  this.verifier.verify(token)
        return TokenData.fromDecodedJWT(decodedJWT)
    }

    /**
     * Produce a token for this combination of User and Account
     */
    fun makeToken(data: TokenData, expireDate: Date): String = JWT.create()
        .withSubject("Authentication")
        //.withIssuer(issuer)
        .withClaim("mode", data.tokenMode.name)
        .withClaim("id", data.playerUniqueId.toString())
        .withExpiresAt(expireDate)
        .sign(algorithm)

    class TokenData(
        val tokenMode: TokenMode,
        val playerUniqueId: UUID
    ) {

        companion object {
            fun fromDecodedJWT(decodedJWT: DecodedJWT): TokenData {
                val mode = decodedJWT.getClaim("mode").asString()
                val id = decodedJWT.getClaim("id").asString()
                return TokenData(TokenMode.valueOf(mode), UUID.fromString(id))
            }
        }

    }

    enum class TokenMode {
        PLAYER, API
    }

}