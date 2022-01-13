package app.simplecloud.simplecloud.application.loader

import com.google.inject.Injector

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.03.2021
 * Time: 15:30
 */
class ExtensionLoader<C>(
    private val injector: Injector,
    private val classLoader: ClassLoader,
    private val parentClass: Class<C>
) {

    fun loadClassInstance(classpath: String): C {
        val newClass = loadClass(classpath)
        return this.injector.createChildInjector().getInstance(newClass)
    }

    private fun loadClass(classpath: String): Class<out C> {
        val clazz = Class.forName(classpath, true, this.classLoader)
        return clazz.asSubclass(this.parentClass)
    }

}