package app.simplecloud.simplecloud.api.permission.repository

import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.repository.Repository

/**
 * Date: 19.03.22
 * Time: 21:24
 * @author Frederick Baier
 *
 */
interface PermissionGroupRepository : Repository<String, PermissionGroupConfiguration>