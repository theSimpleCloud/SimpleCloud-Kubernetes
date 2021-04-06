package eu.thesimplecloud.api.internal

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.internal.service.IInternalCloudProcessGroupService
import eu.thesimplecloud.api.internal.service.IInternalCloudProcessService
import eu.thesimplecloud.api.service.group.ICloudProcessGroupService

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 19:57
 * @author Frederick Baier
 */
abstract class InternalCloudAPI : CloudAPI() {

    init {
        instance = this
    }

    abstract override fun getProcessService(): IInternalCloudProcessService

    abstract override fun getProcessGroupService(): IInternalCloudProcessGroupService

    companion object {
        @JvmStatic
        lateinit var instance: InternalCloudAPI
            private set
    }

}