package app.simplecloud.simplecloud.restserver.base.service

import app.simplecloud.rest.Context
import app.simplecloud.simplecloud.api.permission.EmptyPermissionEntity
import app.simplecloud.simplecloud.api.permission.PermissionEntity

/**
 * Date: 14.03.22
 * Time: 13:04
 * @author Frederick Baier
 *
 */
class NoAuthService : AuthService {

    override suspend fun authenticate(usernameAndPasswordCredentials: UsernameAndPasswordCredentials): String {
        throw UnsupportedOperationException("Authentication is currently not supported")
    }

    override suspend fun getRequestEntityFromContext(context: Context): PermissionEntity {
        return EmptyPermissionEntity
    }
}