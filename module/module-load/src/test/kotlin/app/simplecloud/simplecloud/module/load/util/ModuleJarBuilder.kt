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

package app.simplecloud.simplecloud.module.load.util

import java.io.File

/**
 * Date: 05.09.22
 * Time: 12:19
 * @author Frederick Baier
 *
 */


class ModuleJarBuilder(
    private val moduleName: String,
) {

    @Volatile
    private var mainClass = ""

    @Volatile
    private var depends: Array<String> = emptyArray()

    @Volatile
    private var softDepends: Array<String> = emptyArray()

    fun mainClass(mainClassType: MainClassType): ModuleJarBuilder {
        this.mainClass = mainClassType.mainClass
        return this
    }

    fun depends(depends: Array<String>): ModuleJarBuilder {
        this.depends = depends
        return this
    }

    fun softDepends(softDepends: Array<String>): ModuleJarBuilder {
        this.softDepends = softDepends
        return this
    }

    fun build(): File {
        return ModuleJarCreator(
            this.moduleName,
            this.mainClass,
            this.depends,
            this.depends
        ).createJar()
    }

    enum class MainClassType(
        val mainClass: String,
    ) {

        EMPTY("app/simplecloud/simplecloud/module/load/testmodule/EmptyModuleMain.class"),

        FAILING("app/simplecloud/simplecloud/module/load/testmodule/FailingModuleMain.class"),

        ENABLE_AWARE("app/simplecloud/simplecloud/module/load/testmodule/OnEnableAwareModuleMain.class");

        fun dotString(): String {
            return this.mainClass.replace("/", ".").replace(".class", "")
        }

    }

}