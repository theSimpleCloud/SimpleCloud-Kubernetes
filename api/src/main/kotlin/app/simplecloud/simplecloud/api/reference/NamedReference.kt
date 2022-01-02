package app.simplecloud.simplecloud.api.reference


/**
 * A special reference type which resolves the object by its name
 */
interface NamedReference<T : Any> : Reference<T> {

    /**
     * Returns the name to resolve the object from
     */
    fun getName(): String

}