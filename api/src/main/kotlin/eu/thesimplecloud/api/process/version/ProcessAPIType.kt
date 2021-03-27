package eu.thesimplecloud.api.process.version

enum class ProcessAPIType(
    val minecraftEdition: MinecraftEdition,
    val processVersionType: ProcessVersionType
) {

    /**
     * Represents a jar with the spigot api
     */
    SPIGOT(MinecraftEdition.JAVA, ProcessVersionType.SERVER),

    /**
     * Represents a jar with the nukkit api
     */
    NUKKIT(MinecraftEdition.BEDROCK, ProcessVersionType.SERVER),

    /**
     * Represents a jar with the bungeecord api
     */
    BUNGEECORD(MinecraftEdition.JAVA, ProcessVersionType.PROXY),

    /**
     * Represents a jar with the waterdog api
     */
    WATERDOG(MinecraftEdition.BEDROCK, ProcessVersionType.PROXY),

    /**
     * Represents a jar with the velocity api
     */
    VELOCITY(MinecraftEdition.JAVA, ProcessVersionType.PROXY);


}