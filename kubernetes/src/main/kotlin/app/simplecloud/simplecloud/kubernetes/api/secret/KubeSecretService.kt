package app.simplecloud.simplecloud.kubernetes.api.secret

interface KubeSecretService {

    fun createSecret(name: String, secretSpec: SecretSpec)

    fun getSecret(name: String): KubeSecret

}