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
include("storage-backend")
include("storage-backend:storage-backend-sftp")
findProject(":storage-backend:storage-backend-sftp")?.name = "storage-backend-sftp"
include("rest-server")
include("node")
include("task")
include("container:container-docker")
findProject(":container:container-docker")?.name = "container-docker"
