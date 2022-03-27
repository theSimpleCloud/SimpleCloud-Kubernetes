/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.node.startup.setup.task

import app.simplecloud.simplecloud.node.startup.setup.body.MongoSetupResponseBody
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import app.simplecloud.simplecloud.restserver.setup.type.Setup
import org.apache.logging.log4j.LogManager

/**
 * Created by IntelliJ IDEA.
 * Date: 07/08/2021
 * Time: 00:06
 * @author Frederick Baier
 */
class MongoDbSetup(
    private val restSetupManager: RestSetupManager
) {

    fun executeSetup(): String {
        logger.info("Executing MongoDB setup")
        val mongoSetupResponseBody = this.restSetupManager.setNextSetup(createSetup()).join()
        if (mongoSetupResponseBody.mongoMode == MongoSetupResponseBody.MongoMode.CREATE) {
            return createMongoDockerContainer(mongoSetupResponseBody)
        }
        return mongoSetupResponseBody.connectionString
    }

    private fun createSetup(): Setup<MongoSetupResponseBody> {
        return Setup("mongo", getMongoModePossibilities(), MongoSetupResponseBody::class)
    }

    private fun getMongoModePossibilities(): Array<MongoSetupResponseBody.MongoMode> {
        if (false) {
            return MongoSetupResponseBody.MongoMode.values()
        }
        return arrayOf(MongoSetupResponseBody.MongoMode.EXTERNAL)
    }

    private fun createMongoDockerContainer(mongoSetupResponseBody: MongoSetupResponseBody): String {
        throw IllegalStateException("Cannot create mongodb container because docker is not available")
    }

    companion object {
        private val logger = LogManager.getLogger(MongoDbSetup::class.java)
    }

}