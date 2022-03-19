package app.simplecloud.simplecloud.restserver.base.service

import app.simplecloud.rest.Context
import app.simplecloud.simplecloud.permission.entity.EmptyPermissionEntity
import app.simplecloud.simplecloud.permission.entity.PermissionEntity
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

    override fun getRequestEntityFromContext(context: Context): CompletableFuture<PermissionEntity> {
        return CompletableFuture.completedFuture(EmptyPermissionEntity)
    }
}