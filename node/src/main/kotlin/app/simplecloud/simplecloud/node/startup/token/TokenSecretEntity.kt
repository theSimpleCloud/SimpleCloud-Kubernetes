package app.simplecloud.simplecloud.node.startup.token

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import org.apache.commons.lang.RandomStringUtils

/**
 * Date: 19.03.22
 * Time: 10:01
 * @author Frederick Baier
 *
 */
@Entity("token_secret")
class TokenSecretEntity(
    @Id
    val identifier: String = KEY,
    val secret: String = RandomStringUtils.randomAlphanumeric(32),
) {

    companion object {
        const val KEY = "key"
    }

}