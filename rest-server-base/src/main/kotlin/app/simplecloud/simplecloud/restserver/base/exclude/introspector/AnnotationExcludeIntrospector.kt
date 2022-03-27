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

package app.simplecloud.simplecloud.restserver.base.exclude.introspector

import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector

/**
 * Created by IntelliJ IDEA.
 * Date: 25.06.2021
 * Time: 12:22
 * @author Frederick Baier
 */
class AnnotationExcludeIntrospector(
    vararg excludeAnnotations: Class<out Annotation>
): JacksonAnnotationIntrospector() {

    private val excludeAnnotations = excludeAnnotations.toList()

    override fun _isIgnorable(a: Annotated): Boolean {
        return hasAnnyCustomAnnotation(a) || super._isIgnorable(a)
    }

    private fun hasAnnyCustomAnnotation(a: Annotated): Boolean {
        return excludeAnnotations.any { a.hasAnnotation(it) }
    }


}