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

package app.simplecloud.simplecloud.kubernetes.api.image

import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 10.04.2021
 * Time: 21:27
 * @author Frederick Baier
 *
 * Represents a file to include into an image
 *
 */
interface ImageInclusion {

    /**
     * Returns the file to include
     * This method may be blocking to download a file for example
     */
    fun getFile(): File

    /**
     * Returns the path of the inclusion when in included in an image
     */
    fun getPathInImage(): String

}