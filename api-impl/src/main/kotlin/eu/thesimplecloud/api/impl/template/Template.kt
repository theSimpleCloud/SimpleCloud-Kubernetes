package eu.thesimplecloud.api.impl.template

import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.template.ITemplateInclusion
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 11:55
 * @author Frederick Baier
 */
class Template(
    private val name: String,
    private val parent: ITemplate?
) : ITemplate {

    private val templateInclusions = CopyOnWriteArrayList<ITemplateInclusion>()

    override fun getParentTemplate(): ITemplate? {
        return this.parent
    }

    override fun getTemplateInclusions(): List<ITemplateInclusion> {
        return this.templateInclusions
    }

    override fun getName(): String {
        return this.name
    }

    override fun getIdentifier(): String {
        return getName()
    }
}