package app.simplecloud.simplecloud.restserver.base

import app.simplecloud.simplecloud.restserver.base.route.Route
import app.simplecloud.simplecloud.restserver.base.service.AuthService

/**
 * Date: 12.03.22
 * Time: 22:36
 * @author Frederick Baier
 *
 */
interface RestServer {

    //auth, user, controller, annotations, logic for change auth,
    //add routes / remove dynamically

    fun setAuthService(authService: AuthService)

    fun registerRoute(route: Route)

    fun unregisterRoute(route: Route)




}