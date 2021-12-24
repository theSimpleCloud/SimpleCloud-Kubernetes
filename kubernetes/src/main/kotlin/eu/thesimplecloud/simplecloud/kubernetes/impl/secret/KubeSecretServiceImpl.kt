package eu.thesimplecloud.simplecloud.kubernetes.impl.secret

import com.google.inject.Inject
import eu.thesimplecloud.simplecloud.kubernetes.api.secret.KubeSecret
import eu.thesimplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import eu.thesimplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Secret

class KubeSecretServiceImpl @Inject constructor(
    private val api: CoreV1Api
) : KubeSecretService {

    override fun createSecret(name: String, secretSpec: SecretSpec) {
        val secret = V1Secret()
            .metadata(V1ObjectMeta().name(name))
            .type("Opaque")
            .data(secretSpec.data)
        this.api.createNamespacedSecret("default", secret, null, null, null)
    }

    override fun getSecret(name: String): KubeSecret {
        return KubeSecretImpl(name, api)
    }


}