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

package app.simplecloud.simplecloud.node.api.onlinestrategy

import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.node.assertContains
import app.simplecloud.simplecloud.node.assertNotContains
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 23.08.22
 * Time: 11:00
 * @author Frederick Baier
 *
 */
class NodeAPIOnlineStrategyUpdateTest : NodeAPIOnlineStrategyBaseTest() {

    private lateinit var existingStrategy: ProcessesOnlineCountStrategy

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.existingStrategy = this.onlineStrategyService.createCreateRequest(createStrategyConfigWithName("TestN"))
            .submit().join()
    }

    @Test
    fun addTargetGroupTest() {
        val groupToAdd = "MyGroup2"
        addTargetGroupToStrategy(groupToAdd, existingStrategy)

        assertContains(fetchExistingStrategy().getTargetGroupNames(), groupToAdd)
    }

    @Test
    fun removeTargetGroupTest() {
        val targetGroup = "MyGroup2"
        addTargetGroupToStrategy(targetGroup, existingStrategy)

        this.onlineStrategyService.createUpdateRequest(this.existingStrategy)
            .removeTargetGroup(targetGroup)
            .submit().join()

        assertNotContains(fetchExistingStrategy().getTargetGroupNames(), targetGroup)
    }

    @Test
    fun clearTargetGroupTest() {
        val targetGroup = "MyGroup2"
        addTargetGroupToStrategy(targetGroup, existingStrategy)

        this.onlineStrategyService.createUpdateRequest(this.existingStrategy)
            .clearTargetGroups()
            .submit().join()

        Assertions.assertTrue(fetchExistingStrategy().getTargetGroupNames().isEmpty())
    }


    private fun addTargetGroupToStrategy(targetGroupName: String, strategy: ProcessesOnlineCountStrategy) {
        this.onlineStrategyService.createUpdateRequest(strategy)
            .addTargetGroup(targetGroupName)
            .submit().join()
    }

    private fun fetchExistingStrategy(): ProcessesOnlineCountStrategy {
        return this.onlineStrategyService.findByName(this.existingStrategy.getName()).join()
    }


}