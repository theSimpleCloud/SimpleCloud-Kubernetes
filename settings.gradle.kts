rootProject.name = "simplecloud"
include("application-loader")
include("container")
include("container:container-local")
findProject(":container:container-local")?.name = "container-local"
