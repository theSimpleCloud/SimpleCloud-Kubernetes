package app.simplecloud.simplecloud.kubernetes.api.secret

import app.simplecloud.simplecloud.api.utils.Nameable

interface KubeSecret : Nameable {

    fun getStringValueOf(key: String): String

}