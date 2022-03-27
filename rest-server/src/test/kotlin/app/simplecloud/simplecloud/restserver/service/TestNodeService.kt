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

package app.simplecloud.simplecloud.restserver.service

import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.service.NodeService
import com.google.inject.Singleton
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07/07/2021
 * Time: 20:41
 * @author Frederick Baier
 */
@Singleton
class TestNodeService : NodeService {

    override fun findAll(): CompletableFuture<List<Node>> {
        TODO("Not yet implemented")
    }

    override fun findByUniqueId(uniqueId: UUID): CompletableFuture<Node> {
        TODO("Not yet implemented")
    }

    override fun findFirst(): CompletableFuture<Node> {
        TODO("Not yet implemented")
    }
}