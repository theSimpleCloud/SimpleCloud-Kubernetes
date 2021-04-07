package eu.thesimplecloud.module.loader

import com.fasterxml.jackson.databind.JsonNode
import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import eu.thesimplecloud.application.ILoadedApplication
import eu.thesimplecloud.application.data.DefaultApplicationData
import eu.thesimplecloud.application.data.IApplicationData
import eu.thesimplecloud.application.exception.InvalidApplicationEntryPointFileException
import eu.thesimplecloud.application.loader.AbstractApplicationLoader
import eu.thesimplecloud.application.loader.ApplicationClassLoader
import eu.thesimplecloud.application.loader.ExtensionLoader
import eu.thesimplecloud.module.LoadedModuleApplication
import eu.thesimplecloud.module.data.IModuleApplicationData
import eu.thesimplecloud.module.data.ModuleApplicationData
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.04.2021
 * Time: 22:03
 */
@Singleton
class ModuleApplicationLoader @Inject constructor(
    private val injector: Injector,
    jarFileLoader: ModuleJarFileLoader
) : AbstractApplicationLoader<LoadedModuleApplication>(jarFileLoader) {

    override fun loadApplication(
        file: File,
        applicationData: IApplicationData
    ): LoadedModuleApplication {
        val classLoader = createModuleClassLoader(file)

        val classNameToLoad = applicationData.getClassNameToLoad()

        val extensionLoader = ExtensionLoader(injector, classLoader, AbstractModule::class.java)
        val abstractModule = extensionLoader.loadClassInstance(classNameToLoad)

        return LoadedModuleApplication(file, applicationData as IModuleApplicationData, abstractModule)
    }

    override fun createModuleClassLoader(file: File): ClassLoader {
        return ApplicationClassLoader(listOf(file.toURI().toURL()), this::class.java.classLoader)
    }

}
