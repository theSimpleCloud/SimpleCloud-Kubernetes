package eu.thesimplecloud.api.reference

/**
 * Used to store identifiers to a name and resolve them later
 */
interface IReferenceHolder<I : Any> {

    /**
     * Returns a reference with the found identifier or null if there was no identifier
     */
    fun <T : Any> getReference(referenceName: String): IReference<T>?

    /**
     * Links the [referenceName] to the specified [identifier]
     */
    fun putReference(referenceName: String, identifier: I)

}