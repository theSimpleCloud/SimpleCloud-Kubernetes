package app.simplecloud.simplecloud.api.player

/**
 * Date: 18.03.22
 * Time: 09:28
 * @author Frederick Baier
 *
 */
class PlayerWebConfig(
    /**
     * The encrypted password
     */
    val password: String,
    /**
     * Whether the user has access to the dashboard
     */
    val hasAccess: Boolean
) {

    private constructor() : this("", false)

}