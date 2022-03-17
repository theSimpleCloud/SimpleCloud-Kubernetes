package app.simplecloud.simplecloud.restserver.base.auth

import app.simplecloud.rest.Context
import app.simplecloud.simplecloud.restserver.base.exception.UnauthorizedException
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

/**
 * Date: 15.03.22
 * Time: 11:24
 * @author Frederick Baier
 *
 */
class JwtTokenHandler(
    private val secret: String,
    private val validityInMs: Long
) {
    private val algorithm = Algorithm.HMAC512(secret)

    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        //.withIssuer(issuer)
        .build()

    fun verify(context: Context): String {
        try {
            return verify0(context)
        } catch (ex: Exception) {
            throw UnauthorizedException()
        }
    }

    private fun verify0(context: Context): String {
        if (!context.hasRequestHeader("Authorization")) throw UnauthorizedException()
        val authHeader = context.getRequestHeader("Authorization")
        if (!authHeader.startsWith("Bearer ")) throw UnauthorizedException()
        val token = authHeader.replaceFirst("Bearer ", "")
        val decodedJWT = this.verifier.verify(token)
        return decodedJWT.getClaim("claim").asString()
    }

    /**
     * Produce a token for this combination of User and Account
     */
    fun makeToken(claim: String): String = JWT.create()
        .withSubject("Authentication")
        //.withIssuer(issuer)
        .withClaim("claim", claim)
        .sign(algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}