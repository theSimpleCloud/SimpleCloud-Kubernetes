# CONTRIBUTING

Contributions are welcome :)

To contribute you have to license your code under the MIT License. For more information look [here](LICENSE.md).

Feel free to create a pull request [here](https://github.com/theSimpleCloud/SimpleCloud-v3/pulls).

### Setup

To execute SimpleCloud on your pc you have to setup the execution environment by following these steps:

1. Clone the project and make sure you are using Java 17
2. Install Docker
3. Install microk8s by follwowing the insturction on [https://microk8s.io/](https://microk8s.io/)
4. After the installation is completed execute the follwing command: `microk8s enable dashboard dns registry` This command will enable the built-in docker registry.
5. Add the shell script in `microk8s/microk8sScript.sh` to your Run Configurations

![Image](https://i.imgur.com/7KOxYUT.png)

6. Add the Gradle `ShadowJar` task to Before launch

![Image](https://i.imgur.com/pr1U5Jz.png)

![Image](https://i.imgur.com/unk1v7K.png)

![Image](https://i.imgur.com/koLehbZ.png)

7. Add microk8s registry as insecure registry to docker’s deamon.js:
- MacOS path: ~/.docker/daemon.js

```json
{
  ...
  "insecure-registries": [
    "192.168.64.2:32000"
  ]
}
```

8. Restart Docker
9. Run the microk8s script in IntelliJ

