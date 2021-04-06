package eu.thesimplecloud.api.jvmargs

import eu.thesimplecloud.api.repository.IIdentifiable
import eu.thesimplecloud.api.utils.INameable

/**
 * Created by IntelliJ IDEA.
 * Date: 18.03.2021
 * Time: 15:25
 * @author Frederick Baier
 *
 * Represents arguments for a JVM
 */
interface IJVMArguments : INameable, IIdentifiable<String> {

    /**
     * Returns the arguments to be applied
     */
    fun getArguments(): List<String>

}