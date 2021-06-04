package eu.thesimplecloud.api.impl.process.version

import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.process.version.ProcessAPIType
import java.net.URL

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 11:53
 * @author Frederick Baier
 */
class ProcessVersion(
    private val name: String,
    private val apiType: ProcessAPIType,
    private val downloadLink: URL
) : IProcessVersion {

    override fun getProcessApiType(): ProcessAPIType {
        return this.apiType
    }

    override fun getDownloadLink(): URL {
        return this.downloadLink
    }

    override fun getName(): String {
        return this.name
    }

    override fun getIdentifier(): String {
        return getName()
    }
}