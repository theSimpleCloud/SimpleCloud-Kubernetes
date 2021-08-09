package eu.thesimplecloud.application.exception

import java.io.File
import java.lang.RuntimeException

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.03.2021
 * Time: 13:47
 */
class ApplicationLoadException(
    fileToLoad: File, cause: Throwable
) : RuntimeException("An error occurred loading file: ${fileToLoad.path}", cause)