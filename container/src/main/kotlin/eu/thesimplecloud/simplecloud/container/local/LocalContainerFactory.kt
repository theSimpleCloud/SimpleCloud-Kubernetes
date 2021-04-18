package eu.thesimplecloud.simplecloud.container.local

import eu.thesimplecloud.simplecloud.container.IContainer
import eu.thesimplecloud.simplecloud.container.IImage

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 21:17
 * @author Frederick Baier
 */
class LocalContainerFactory : IContainer.Factory {

    override fun createContainer(
        name: String,
        image: IImage,
        startCommand: String,
        stopCommand: String,
        portToExpose: Int
    ): IContainer {
        require(image is LocalImage) { "Image must be a LocalImage" }
        return LocalContainer(name, image, startCommand, stopCommand)
    }
}