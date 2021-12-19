package eu.thesimplecloud.simplecloud.api.reference

/**
 * Used to store identifiers to a name and resolve them later
 */
interface ReferenceHolder<I : Any> {

    /**
     * Returns a reference with the found identifier or null if there was no identifier
     */
    fun <T : Any> getReference(referenceName: String): Reference<T>?

    /**
     * Links the [referenceName] to the specified [identifier]
     */
    fun putReference(referenceName: String, identifier: I)

}