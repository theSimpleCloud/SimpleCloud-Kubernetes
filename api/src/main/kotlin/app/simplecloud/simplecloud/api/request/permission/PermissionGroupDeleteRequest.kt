package app.simplecloud.simplecloud.api.request.permission

import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.utils.Request

/**
 * Date: 20.03.22
 * Time: 18:45
 * @author Frederick Baier
 *
 */
interface PermissionGroupDeleteRequest : Request<Unit> {

    /**
     * Returns the group to be deleted
     */
    fun getPermissionGroup(): PermissionGroup

}