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

package app.simplecloud.simplecloud.distribution.api

import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 08.04.22
 * Time: 17:42
 * @author Frederick Baier
 *
 */
abstract class AbstractTestDistribution : Distribution {

    private val selfMember = MemberImpl(UUID.randomUUID())

    private val members = CopyOnWriteArrayList<Member>()

    abstract val messageManager: TestMessageManager

    init {
        this.members.add(this.selfMember)
    }

    override fun getSelfMember(): Member {
        return this.selfMember
    }

    override fun getMembers(): List<Member> {
        return this.members
    }

    override fun getMessageManager(): MessageManager {
        return this.messageManager
    }

    override fun <K, V> getOrCreateCache(name: String): Cache<K, V> {
        return getVirtualCluster().getOrCreateCache(name)
    }

    fun onMemberJoin(member: Member) {
        if (!this.members.contains(member))
            this.members.add(member)
    }

    abstract fun getVirtualCluster(): VirtualCluster

}