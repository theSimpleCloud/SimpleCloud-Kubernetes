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

package app.simplecloud.simplecloud.api.impl.messagechannel

import app.simplecloud.simplecloud.api.future.failedFuture
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 10:51
 * @author Frederick Baier
 */
class EmptyMessageHandler<T : Any, R : Any> : MessageHandler<T, R> {

    override fun handleMessage(message: T, sender: NetworkComponent): CompletableFuture<R> {
        return failedFuture(EmptyMessageHandlerException())
    }

    class EmptyMessageHandlerException() : Exception()
}