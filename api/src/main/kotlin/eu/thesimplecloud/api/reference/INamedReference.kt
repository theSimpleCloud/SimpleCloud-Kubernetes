package eu.thesimplecloud.api.reference


/**
 * A special reference type which resolves the object by its name
 */
interface INamedReference<T : Any> : IReference<T> {

    /**
     * Returns the name to resolve the object from
     */
    fun getName(): String

}