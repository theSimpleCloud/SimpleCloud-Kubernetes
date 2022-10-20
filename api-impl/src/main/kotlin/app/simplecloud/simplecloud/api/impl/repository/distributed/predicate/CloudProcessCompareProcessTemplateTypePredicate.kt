package app.simplecloud.simplecloud.api.impl.repository.distributed.predicate

import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.template.ProcessTemplateType
import app.simplecloud.simplecloud.distribution.api.Predicate

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 20.10.22
 * Time: 14:15
 */
class CloudProcessCompareProcessTemplateTypePredicate(
    private val compareTemplateType: ProcessTemplateType,
) : Predicate<String, CloudProcessConfiguration> {

    override fun apply(uuid: String, configuration: CloudProcessConfiguration): Boolean {
        return configuration.processTemplateType == compareTemplateType
    }

}