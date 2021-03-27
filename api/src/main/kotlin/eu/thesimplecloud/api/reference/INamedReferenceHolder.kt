package eu.thesimplecloud.api.reference

/**
 * Special tpy of [IReferenceHolder] which uses a [String] as identifier
 */
interface INamedReferenceHolder : IReferenceHolder<String> {

    override fun <T : Any> getReference(referenceName: String): INamedReference<T>?

    override fun putReference(referenceName: String, identifier: String)

}