package eu.thesimplecloud.simplecloud.api.impl.exception

class NoImageProvidedException(groupName: String) : Exception("No Image provided for group $groupName") {
}