package app.simplecloud.simplecloud.kubernetes.impl.secret

import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecret
import io.kubernetes.client.openapi.apis.CoreV1Api
import java.nio.charset.StandardCharsets

class KubeSecretImpl(
    name: String,
    private val api: CoreV1Api
) : KubeSecret {

    private val name: String = name.lowercase()

    private val secret = api.readNamespacedSecret(this.name, "default", null)

    override fun getStringValueOf(key: String): String {
        val bytes = secret.data?.get(key)!!
        return String(bytes, StandardCharsets.UTF_8)
    }

    override fun getName(): String {
        return this.name
    }


}