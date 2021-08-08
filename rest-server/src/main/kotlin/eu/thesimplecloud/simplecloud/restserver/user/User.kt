/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.restserver.user

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import eu.thesimplecloud.simplecloud.restserver.annotation.exclude.WebExcludeOutgoing
import eu.thesimplecloud.simplecloud.api.utils.IIdentifiable

/**
 * Created by IntelliJ IDEA.
 * Date: 23.06.2021
 * Time: 10:04
 * @author Frederick Baier
 */
@Entity("web_user")
open class User(
    @Id
    val username: String,
    @WebExcludeOutgoing
    val password: String
) : IIdentifiable<String> {

    //Default constructor for jackson
    private constructor() : this("", "")

    @JsonIgnore
    override fun getIdentifier(): String {
        return this.username
    }

    open fun hasPermission(permission: String): Boolean {
        return true
    }

}