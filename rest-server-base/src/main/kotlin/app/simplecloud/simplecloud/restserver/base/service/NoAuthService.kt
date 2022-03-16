package app.simplecloud.simplecloud.restserver.base.service

import app.simplecloud.simplecloud.restserver.base.user.EmptyRequestEntity
import app.simplecloud.simplecloud.restserver.base.user.RequestEntity
import io.ktor.application.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 14.03.22
 * Time: 13:04
 * @author Frederick Baier
 *
 */
class NoAuthService : AuthService {
    override fun authenticate(usernameAndPasswordCredentials: UsernameAndPasswordCredentials): CompletableFuture<String> {
        throw UnsupportedOperationException("Authentication is currently not supported")
    }

    override fun getRequestEntityFromCall(call: ApplicationCall): CompletableFuture<RequestEntity> {
        return CompletableFuture.completedFuture(EmptyRequestEntity)
    }
}