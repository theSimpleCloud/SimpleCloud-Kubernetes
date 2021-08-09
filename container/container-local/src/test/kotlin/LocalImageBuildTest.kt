import eu.thesimplecloud.simplecloud.container.FileImageInclusion
import eu.thesimplecloud.simplecloud.container.local.LocalImage
import eu.thesimplecloud.simplecloud.container.local.LocalImageFactory
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 18.04.2021
 * Time: 10:11
 * @author Frederick Baier
 */
class LocalImageBuildTest {

    private val testDir = File("imageBuildTest/")
    private val templatesDir = File("imageBuildTest/templates/")
    private val extraDir = File("imageBuildTest/extras/")

    private val factory = LocalImageFactory()

    private fun createTestTemplateFiles(): File {
        val testTemplateDir = File(templatesDir, "Test")
        testTemplateDir.mkdirs()

        val testFile1 = File(testTemplateDir, "Test1.txt")
        val testFile2 = File(testTemplateDir, "Test2.txt")
        testFile1.createNewFile()
        testFile2.createNewFile()
        return testTemplateDir
    }

    private fun createTest2TemplateFiles(): File {
        val testTemplateDir = File(templatesDir, "Test2")
        testTemplateDir.mkdirs()

        val testFile1 = File(testTemplateDir, "Test6.txt")
        val testFile2 = File(testTemplateDir, "Test7.txt")
        testFile1.createNewFile()
        testFile2.createNewFile()
        return testTemplateDir
    }

    private fun createExtraFile(): File {
        val extraFile = File(extraDir, "extra.txt")
        extraDir.mkdirs()
        extraFile.createNewFile()
        return extraFile
    }


    @Test
    fun buildImageFromOneDirectory() {
        val templateDir = createTestTemplateFiles()
        val image = factory.create("Test", listOf(templateDir))
        image.build().join()
        Assertions.assertTrue(File(image.imageDir, "Test1.txt").exists())
        Assertions.assertTrue(File(image.imageDir, "Test2.txt").exists())
        Assertions.assertFalse(File(image.imageDir, "Test6.txt").exists())
        Assertions.assertFalse(File(image.imageDir, "Test7.txt").exists())
    }

    @Test
    fun buildImageFromTwoDirectories() {
        val templateDir = createTestTemplateFiles()
        val template2Dir = createTest2TemplateFiles()
        val image = factory.create("Test", listOf(templateDir, template2Dir))
        image.build().join()
        Assertions.assertTrue(File(image.imageDir, "Test1.txt").exists())
        Assertions.assertTrue(File(image.imageDir, "Test2.txt").exists())
        Assertions.assertTrue(File(image.imageDir, "Test6.txt").exists())
        Assertions.assertTrue(File(image.imageDir, "Test7.txt").exists())
    }

    @Test
    fun buildImageFromTwoDirectoriesAndExtra() {
        val templateDir = createTestTemplateFiles()
        val template2Dir = createTest2TemplateFiles()
        val extraFile = createExtraFile()
        val inclusion = FileImageInclusion(extraFile, "plugins/extra2.txt")
        val image = factory.create("Test", listOf(templateDir, template2Dir), listOf(inclusion))
        image.build().join()
        println(image.imageDir.absolutePath)
        Assertions.assertTrue(File(image.imageDir, "Test1.txt").exists())
        Assertions.assertTrue(File(image.imageDir, "Test2.txt").exists())
        Assertions.assertTrue(File(image.imageDir, "Test6.txt").exists())
        Assertions.assertTrue(File(image.imageDir, "Test7.txt").exists())
        Assertions.assertTrue(File(image.imageDir, "plugins/extra2.txt").exists())
    }

    @AfterEach
    fun tearDown() {
        val imagesDir = File(LocalImage.IMAGES_DIR)
        FileUtils.deleteDirectory(imagesDir)
        FileUtils.deleteDirectory(testDir)
    }



}