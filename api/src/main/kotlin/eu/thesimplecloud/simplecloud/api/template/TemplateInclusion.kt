package eu.thesimplecloud.simplecloud.api.template

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
interface TemplateInclusion {

    /**
     * Includes the files
     */
    fun includeResource(processDir: File)

}