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
include("task")
