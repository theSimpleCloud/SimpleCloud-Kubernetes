package app.simplecloud.simplecloud.api.reference

/**
 * Special tpy of [ReferenceHolder] which uses a [String] as identifier
 */
interface NamedReferenceHolder : ReferenceHolder<String> {

    override fun <T : Any> getReference(referenceName: String): NamedReference<T>?

    override fun putReference(referenceName: String, identifier: String)

}