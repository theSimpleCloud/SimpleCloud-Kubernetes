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

package app.simplecloud.simplecloud.api.template.static

import app.simplecloud.simplecloud.api.request.statictemplate.StaticProcessTemplateDeleteRequest
import app.simplecloud.simplecloud.api.request.statictemplate.update.StaticProcessTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.ProcessTemplate

/**
 * Date: 16.08.22
 * Time: 20:48
 * @author Frederick Baier
 *
 */
interface StaticProcessTemplate : ProcessTemplate {

    override fun createUpdateRequest(): StaticProcessTemplateUpdateRequest

    override fun createDeleteRequest(): StaticProcessTemplateDeleteRequest

}