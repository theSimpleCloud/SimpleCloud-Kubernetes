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

package app.simplecloud.simplecloud.restserver.base.vmethod

import java.lang.reflect.Method

/**
 * Created by IntelliJ IDEA.
 * Date: 05/08/2021
 * Time: 13:19
 * @author Frederick Baier
 *
 */
@FunctionalInterface
interface VirtualMethod {

    /**
     * @param args the arguments of the method
     */
    fun invoke(vararg args: Any?): Any?


    companion object {
        fun fromRealMethod(method: Method, invokeObj: Any): VirtualMethod {
            return object: VirtualMethod {

                override fun invoke(vararg args: Any?): Any? {
                    return method.invoke(invokeObj, *args)
                }

            }
        }
    }

}