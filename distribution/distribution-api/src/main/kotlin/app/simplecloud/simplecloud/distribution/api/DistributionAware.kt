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

/**
 * Date: 14.08.22
 * Time: 08:17
 * @author Frederick Baier
 *
 * Used to get the distribution instance when submitting a Runnable using Distribution ExecutorServices.
 * Before executing the Runnable the [setDistribution] method will be invoked with distribution executing the Runnable.
 * Note that the filed the distribution gets saved in must be annotated with "transient"
 */
interface DistributionAware {

    fun setDistribution(distribution: Distribution)

}