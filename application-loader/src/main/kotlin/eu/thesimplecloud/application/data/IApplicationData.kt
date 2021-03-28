package eu.thesimplecloud.application.data

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 26.03.2021
 * Time: 21:20
 */
interface IApplicationData {

    fun getName(): String

    fun getAuthor(): String

    fun getVersion(): String

    fun getClassNameToLoad(): String

}