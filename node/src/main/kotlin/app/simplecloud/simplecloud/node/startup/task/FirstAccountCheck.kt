package app.simplecloud.simplecloud.node.startup.task

import app.simplecloud.simplecloud.node.repository.mongo.player.MongoCloudPlayerRepository
import app.simplecloud.simplecloud.node.startup.setup.task.FirstWebUserSetup
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import com.google.inject.Inject

/**
 * Date: 22.03.22
 * Time: 13:34
 * @author Frederick Baier
 *
 */
class FirstAccountCheck @Inject constructor(
    private val cloudPlayerRepository: MongoCloudPlayerRepository,
    private val tokenHandler: JwtTokenHandler,
    private val restSetupManager: RestSetupManager
) {

    fun checkForAccount() {
        val count = this.cloudPlayerRepository.count().join()
        if (count == 0L) {
            return executeFirstUserSetup()
        }
    }

    private fun executeFirstUserSetup() {
        return FirstWebUserSetup(this.restSetupManager, this.cloudPlayerRepository, this.tokenHandler).executeSetup()
    }

}