package eu.thesimplecloud.application.loader

import com.google.common.collect.Maps
import java.net.URL
import java.net.URLClassLoader

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 26.03.2021
 * Time: 21:39
 */
class ApplicationClassLoader(
    urls: List<URL>,
    parent: ClassLoader
) : URLClassLoader(urls.toTypedArray(), parent) {

    @Volatile
    private var closed: Boolean = false
    private val cachedClasses: MutableMap<String, Class<*>> = Maps.newConcurrentMap()

    companion object {
        init {
            ClassLoader.registerAsParallelCapable()
        }
    }

    override fun findClass(name: String): Class<*> {
        if (closed) throw IllegalStateException("ApplicationClassLoader is already closed")
        return findClass0(name)
    }

    private fun findClass0(name: String): Class<*> {
        val clazz = this.cachedClasses[name]
        if (clazz != null) return clazz
        val classByName = runCatching { super.findClass(name) }.getOrNull()
        if (classByName != null) {
            this.cachedClasses[name] = classByName
            return classByName
        }
        throw ClassNotFoundException(name)
    }

    override fun close() {
        super.close()
        this.closed = true
    }

    fun isClosed(): Boolean {
        return this.closed
    }

}