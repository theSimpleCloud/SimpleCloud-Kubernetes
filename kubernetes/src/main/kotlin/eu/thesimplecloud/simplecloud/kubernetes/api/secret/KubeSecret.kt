package eu.thesimplecloud.simplecloud.kubernetes.api.secret

import eu.thesimplecloud.simplecloud.api.utils.Nameable

interface KubeSecret : Nameable {

    fun getStringValueOf(key: String): String

}