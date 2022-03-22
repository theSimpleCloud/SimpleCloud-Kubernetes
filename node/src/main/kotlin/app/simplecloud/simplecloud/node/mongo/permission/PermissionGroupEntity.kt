package app.simplecloud.simplecloud.node.mongo.permission

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id

/**
 * Date: 20.03.22
 * Time: 13:21
 * @author Frederick Baier
 *
 */
@Entity("permission_groups")
class PermissionGroupEntity(
    @Id
    val name: String,
    val priority: Int,
    val permissions: List<PermissionConfiguration>
) {

    private constructor() : this(
        "<empty>",
        -1,
        emptyList()
    )

    fun toConfiguration(): PermissionGroupConfiguration {
        return PermissionGroupConfiguration(
            this.name,
            this.priority,
            this.permissions
        )
    }

    companion object {
        fun fromConfiguration(configuration: PermissionGroupConfiguration): PermissionGroupEntity {
            return PermissionGroupEntity(configuration.name, configuration.priority, configuration.permissions)
        }
    }

}