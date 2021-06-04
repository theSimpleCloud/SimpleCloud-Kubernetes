package eu.thesimplecloud.api

import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.repository.group.ICloudProcessGroupRepository
import eu.thesimplecloud.api.service.group.ICloudProcessGroupService
import eu.thesimplecloud.api.service.jvmargs.IJvmArgumentsService
import eu.thesimplecloud.api.service.node.INodeService
import eu.thesimplecloud.api.service.onlinecount.IProcessOnlineCountService
import eu.thesimplecloud.api.service.process.ICloudProcessService
import eu.thesimplecloud.api.service.processversion.IProcessVersionService
import eu.thesimplecloud.api.service.template.ITemplateService

/**
 * Created by IntelliJ IDEA.
 * Date: 28.03.2021
 * Time: 10:31
 * @author Frederick Baier
 */
abstract class CloudAPI {

    init {
        instance = this
    }


    abstract fun getProcessGroupService(): ICloudProcessGroupService

    abstract fun getProcessService(): ICloudProcessService

    abstract fun getTemplateService(): ITemplateService

    abstract fun getProcessVersionService(): IProcessVersionService

    abstract fun getJvmArgumentsService(): IJvmArgumentsService

    abstract fun getNodeService(): INodeService

    abstract fun getProcessOnlineCountService(): IProcessOnlineCountService


    companion object {
        @JvmStatic
        lateinit var instance: CloudAPI
            private set
    }

}