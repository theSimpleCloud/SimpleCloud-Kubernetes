package eu.thesimplecloud.api.process.version

import eu.thesimplecloud.api.utils.INameable

/**
 * Created by IntelliJ IDEA.
 * Date: 15.03.2021
 * Time: 10:24
 * @author Frederick Baier
 *
 * Represents a version processes can be executed with
 *
 */
interface IProcessVersion : INameable {

    /**
     * Returns the api type
     */
    fun getProcessApiType(): ProcessAPIType

    /**
     * Returns the direct download link to a jar file
     */
    fun getDownloadLink(): String

}