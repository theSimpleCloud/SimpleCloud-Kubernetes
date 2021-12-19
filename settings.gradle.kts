rootProject.name = "simplecloud"
include("api")
include("api-impl")
include("api-internal")
include("event-api")
include("ignite")
include("application-loader")
include("module-loader")
include("container")
include("container:container-local")
findProject(":container:container-local")?.name = "container-local"
include("rest-server")
include("node")
include("container:container-docker")
findProject(":container:container-docker")?.name = "container-docker"
include("plugin")
include("container-kubernetes")
include("container:container-kubernetes")
findProject(":container:container-kubernetes")?.name = "container-kubernetes"
