package app.simplecloud.simplecloud.api.utils

/**
 * Created by IntelliJ IDEA.
 * Date: 26.03.2021
 * Time: 21:16
 * @author Frederick Baier
 *
 * Represents an object that has a unique identifier
 * @param I the type of the identifier
 *
 */
interface Identifiable<I : Any> {

    /**
     * Returns the identifier
     */
    fun getIdentifier(): I

}