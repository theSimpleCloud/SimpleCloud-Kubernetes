package app.simplecloud.simplecloud.restserver.defaultcontroller.v1

import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.service.NodeProcessOnlineStrategyService
import app.simplecloud.simplecloud.restserver.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.annotation.RestController
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.controller.Controller
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.dto.OnlineStrategyUpdateRequestDto
import com.google.inject.Inject

/**
 * Date: 27.03.22
 * Time: 09:39
 * @author Frederick Baier
 *
 */
@RestController(1, "cloud/onlinestrategy")
class OnlineStrategyController @Inject constructor(
    private val service: NodeProcessOnlineStrategyService
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.onlinestrategy.get")
    fun handleGroupGetAll(): List<ProcessOnlineCountStrategyConfiguration> {
        val strategies = this.service.findAll().join()
        return strategies.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.onlinestrategy.get")
    fun handleGroupGetOne(@RequestPathParam("name") name: String): ProcessOnlineCountStrategyConfiguration {
        val group = this.service.findByName(name).join()
        return group.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.onlinestrategy.create")
    fun handleGroupCreate(
        @RequestBody(
            types = [],
            classes = [ProcessOnlineCountStrategyConfiguration::class]
        ) configuration: ProcessOnlineCountStrategyConfiguration
    ): ProcessOnlineCountStrategyConfiguration {
        val completableFuture = this.service.createCreateRequest(configuration).submit()
        val group = completableFuture.join()
        return group.toConfiguration()
    }

    @RequestMapping(RequestType.PUT, "", "web.cloud.onlinestrategy.update")
    fun handleGroupUpdate(
        @RequestBody(
            types = [],
            classes = [OnlineStrategyUpdateRequestDto::class]
        ) configuration: OnlineStrategyUpdateRequestDto
    ): Boolean {
        val strategy = this.service.findByName(configuration.name).join()
        val updateRequest = this.service.createUpdateRequest(strategy)
        updateRequest.clearTargetGroups()
        configuration.targetGroupNames.forEach { updateRequest.addTargetGroup(it) }
        updateRequest.setData(configuration.data)
        updateRequest.submit().join()
        return true
    }

    @RequestMapping(RequestType.DELETE, "{name}", "we.cloud.onlinestrategy.delete")
    fun handleDelete(@RequestPathParam("name") groupName: String): Boolean {
        val strategy = this.service.findByName(groupName).join()
        val groupDeleteRequest = this.service.createDeleteRequest(strategy)
        groupDeleteRequest.submit().join()
        return true
    }

}