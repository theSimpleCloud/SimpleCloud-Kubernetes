package app.simplecloud.simplecloud.kubernetes.impl.secret

import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecret
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import com.google.inject.Inject
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Secret

class KubeSecretServiceImpl @Inject constructor(
    private val api: CoreV1Api
) : KubeSecretService {

    override fun createSecret(name: String, secretSpec: SecretSpec) {
        val secret = createSecretObj(name, secretSpec)
        this.api.createNamespacedSecret("default", secret, null, null, null)
    }

    private fun createSecretObj(
        name: String,
        secretSpec: SecretSpec
    ): V1Secret {
        return V1Secret()
            .metadata(V1ObjectMeta().name(name))
            .type("Opaque")
            .data(secretSpec.data)
    }

    override fun getSecret(name: String): KubeSecret {
        try {
            return KubeSecretImpl(name, api)
        } catch (e: ApiException) {
            throw KubeSecretException("Kube Secret does not exist", e)
        }
    }

    class KubeSecretException(message: String, cause: Throwable) : Exception(message, cause)


}