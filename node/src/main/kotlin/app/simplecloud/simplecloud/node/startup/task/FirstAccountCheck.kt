package app.simplecloud.simplecloud.node.startup.task

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.node.mongo.player.MongoCloudPlayerRepository
import app.simplecloud.simplecloud.node.startup.setup.task.FirstWebUserSetupTask
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import com.ea.async.Async.await
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

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

    fun checkForAccount(): CompletableFuture<Unit> {
        val count = await(cloudPlayerRepository.count())
        if (count == 0L) {
            return executeFirstUserSetup()
        }
        return unitFuture()
    }

    private fun executeFirstUserSetup(): CompletableFuture<Unit> {
        return FirstWebUserSetupTask(this.restSetupManager, this.cloudPlayerRepository, this.tokenHandler).run()
    }

}