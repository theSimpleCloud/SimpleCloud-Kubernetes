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

package app.simplecloud.distribution.test.scheduler

import app.simplecloud.simplecloud.distribution.test.scheduler.Time
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/**
 * Date: 05.08.22
 * Time: 10:18
 * @author Frederick Baier
 *
 */
class FakeTimeTest {

    private var time = Time()

    @BeforeEach
    fun setUp() {
        this.time = Time()
    }

    @Test
    fun test() {
        Assertions.assertEquals(System.currentTimeMillis(), time.currentTime())
    }

    @Test
    fun test2() {
        time.skip(1, TimeUnit.SECONDS)
        Assertions.assertEquals(System.currentTimeMillis() + 1000, time.currentTime())
    }

    @Test
    fun tes3() {
        time.back(1, TimeUnit.SECONDS)
        Assertions.assertEquals(System.currentTimeMillis() - 1000, time.currentTime())
    }

}