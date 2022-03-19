package app.simplecloud.simplecloud.permission


data class Permission(
    val permissionString: String,
    val active: Boolean,
    private val targetProcessGroup: String? = null
) {

    fun matches(permission: String, processGroup: String?): Boolean {
        if (this.targetProcessGroup == null || this.targetProcessGroup == processGroup)
            return matchesPermission(permission)
        return false

    }

    private fun matchesPermission(permission: String): Boolean {
        val endsWithStar = this.permissionString.endsWith(".*")
        return if (endsWithStar) {
            matchesWithStarAtTheEnd(permission)
        } else {
            isPermissionToCheckEqualToThisPermission(permission)
        }
    }

    private fun isPermissionToCheckEqualToThisPermission(permission: String): Boolean {
        return permission == this.permissionString
    }

    private fun matchesWithStarAtTheEnd(permission: String): Boolean {
        val checkPermissionComponents = permission.split(".")
        val thisPermissionComponents = this.permissionString.split(".")

        val isPermissionToCheckLongEnough = checkPermissionComponents.size >= thisPermissionComponents.size
        return if (isPermissionToCheckLongEnough) {
            areAllPermissionComponentsEqual(checkPermissionComponents, thisPermissionComponents)
        } else {
            false
        }
    }

    private fun areAllPermissionComponentsEqual(
        checkPermissionComponents: List<String>,
        thisPermissionComponents: List<String>
    ): Boolean {
        val thisPermissionComponentsWithoutStar = thisPermissionComponents.dropLast(1)
        val checkComponentsWithLimit = checkPermissionComponents.take(thisPermissionComponentsWithoutStar.size)
        return checkComponentsWithLimit == thisPermissionComponentsWithoutStar
    }

}