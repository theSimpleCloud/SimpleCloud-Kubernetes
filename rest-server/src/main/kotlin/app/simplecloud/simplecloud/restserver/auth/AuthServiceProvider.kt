package app.simplecloud.simplecloud.restserver.auth

import app.simplecloud.simplecloud.restserver.base.service.AuthService

/**
 * Date: 18.03.22
 * Time: 21:30
 * @author Frederick Baier
 *
 */
interface AuthServiceProvider {

    fun getAuthService(): AuthService

}