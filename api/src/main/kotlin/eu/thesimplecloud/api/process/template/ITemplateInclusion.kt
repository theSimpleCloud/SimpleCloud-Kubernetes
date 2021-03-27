package eu.thesimplecloud.api.process.template

import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 15:06
 * @author Frederick Baier
 *
 * Represents files included when copying the template.
 * These files might be on the disk or are downloaded from an url.
 *
 */
interface ITemplateInclusion {

    /**
     * Includes the files
     */
    fun includeResource(processDir: File)

}