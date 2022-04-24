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

package app.simplecloud.simplecloud.database.memory

import app.simplecloud.simplecloud.api.repository.Repository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 23.04.22
 * Time: 17:50
 * @author Frederick Baier
 *
 */
class InMemoryRepositoryTest {

    private var repository: Repository<String, String> = InMemoryRepository<String, String>()

    @BeforeEach
    internal fun setUp() {
        this.repository = InMemoryRepository<String, String>()
    }

    @Test
    fun notSavedKey_doesNotExist() {
        assertFalse(repository.doesExist("test").join())
    }

    @Test
    fun afterSave_keyExist() {
        repository.save("test", "test4")
        assertTrue(repository.doesExist("test").join())
    }

    @Test
    fun afterSaveX_willGetX() {
        repository.save("test", "test4")
        assertEquals("test4", repository.find("test").join())
    }

    @Test
    fun afterSaveXAndYWillGetXAndY() {
        repository.save("test", "test4")
        repository.save("test2", "test1")
        assertEquals("test4", repository.find("test").join())
        assertEquals("test1", repository.find("test2").join())
    }

    @Test
    fun afterSaveAndRemove_WillNotExist() {
        repository.save("test", "test4")
        repository.remove("test")
        assertFalse(repository.doesExist("test").join())
    }

    @Test
    fun afterSaveXKeyYWillNotExist() {
        repository.save("test", "test4")
        assertFalse(repository.doesExist("test1").join())
    }

    @Test
    fun emptyRepository_CountIs0() {
        assertEquals(0, repository.count().join())
    }

    @Test
    fun afterSave_CountWillBe1() {
        repository.save("test", "test4")
        assertEquals(1, repository.count().join())
    }

    @Test
    fun afterSave2_CountWillBe2() {
        repository.save("test", "test4")
        repository.save("test2", "test1")
        assertEquals(2, repository.count().join())
    }

    @Test
    fun afterSaveAndRemove_CountWillBe0() {
        repository.save("test", "test4")
        repository.remove("test")
        assertEquals(0, repository.count().join())
    }


}