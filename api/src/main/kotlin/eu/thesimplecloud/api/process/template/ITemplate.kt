package eu.thesimplecloud.api.process.template

import eu.thesimplecloud.api.utils.INameable

/**
 * Created by IntelliJ IDEA.
 * Date: 16.03.2021
 * Time: 19:55
 * @author Frederick Baier
 *
 * Represents a template used to create processes from
 * The files associated with this template will be copied to a process before the process gets started
 *
 */
interface ITemplate : INameable {

    /**
     * Returns the parent template or null if this template has no parent
     */
    fun getParentTemplate(): ITemplate?

    /**
     * Returns all [ITemplateInclusion]s for this template
     */
    fun getTemplateInclusions(): List<ITemplateInclusion>

}