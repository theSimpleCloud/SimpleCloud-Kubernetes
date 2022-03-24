package app.simplecloud.simplecloud.api.impl.permission

import app.simplecloud.simplecloud.api.future.exception.FutureOriginException
import app.simplecloud.simplecloud.api.impl.request.permission.PermissionGroupUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.repository.PermissionGroupRepository
import com.google.inject.Guice
import com.google.inject.Injector
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletionException

/**
 * Date: 23.03.22
 * Time: 08:54
 * @author Frederick Baier
 *
 */
class PermissionGroupUpdateRequestTest {

    private lateinit var injector: Injector
    private lateinit var permissionGroupService: InternalPermissionGroupService
    private lateinit var permissionGroupRepository: PermissionGroupRepository


    @BeforeEach
    fun beforeEach() {
        this.injector = Guice.createInjector(BinderModule())
        this.permissionGroupService = this.injector.getInstance(InternalPermissionGroupService::class.java)
        this.permissionGroupRepository = this.injector.getInstance(PermissionGroupRepository::class.java)
    }

    @Test
    fun simple_priority_changed_test() {
        val groupConfiguration = PermissionGroupConfiguration("Test", 5, emptyList())
        this.permissionGroupRepository.save("Test", groupConfiguration)

        val permissionGroup = this.permissionGroupService.findByName("Test").join()
        val updateRequest = this.permissionGroupService.createUpdateRequest(permissionGroup)
        updateRequest.setPriority(2)
        updateRequest.submit().join()

        val changedGroup = this.permissionGroupService.findByName("Test").join()
        Assertions.assertEquals(2, changedGroup.getPriority())
    }

    @Test
    fun self_depend_test() {
        val groupConfiguration = PermissionGroupConfiguration(
            "test",
            5,
            listOf(
                PermissionConfiguration("group.test", true, -1L, null)
            )
        )
        this.permissionGroupRepository.save("test", groupConfiguration)

        val permissionGroup = this.permissionGroupService.findByName("test").join()
        val updateRequest = this.permissionGroupService.createUpdateRequest(permissionGroup)
        Assertions.assertThrows(PermissionGroupUpdateRequestImpl.GroupRecursionException::class.java) {
            try {
                updateRequest.submit().join()
            } catch (e: Exception) {
                throw unpackException(e)
            }
        }
    }

    @Test
    fun recursion_test() {
        val groupConfiguration = PermissionGroupConfiguration(
            "admin",
            5,
            listOf(
                PermissionConfiguration("group.builder", true, -1L, null)
            )
        )

        val groupConfiguration2 = PermissionGroupConfiguration(
            "builder",
            5,
            listOf(
                PermissionConfiguration("group.admin", true, -1L, null)
            )
        )
        this.permissionGroupRepository.save("admin", groupConfiguration)
        this.permissionGroupRepository.save("builder", groupConfiguration2)

        val permissionGroup = this.permissionGroupService.findByName("admin").join()
        val updateRequest = this.permissionGroupService.createUpdateRequest(permissionGroup)

        Assertions.assertThrows(PermissionGroupUpdateRequestImpl.GroupRecursionException::class.java) {
            try {
                updateRequest.submit().join()
            } catch (e: Exception) {
                throw unpackException(e)
            }
        }
    }

    @Test
    fun three_recursion_test() {
        val groupConfiguration = PermissionGroupConfiguration(
            "admin",
            5,
            listOf(
                PermissionConfiguration("group.builder", true, -1L, null)
            )
        )

        val groupConfiguration2 = PermissionGroupConfiguration(
            "builder",
            5,
            listOf(
                PermissionConfiguration("group.mod", true, -1L, null)
            )
        )

        val groupConfiguration3 = PermissionGroupConfiguration(
            "mod",
            5,
            listOf(
                PermissionConfiguration("group.admin", true, -1L, null)
            )
        )
        this.permissionGroupRepository.save("admin", groupConfiguration)
        this.permissionGroupRepository.save("builder", groupConfiguration2)
        this.permissionGroupRepository.save("mod", groupConfiguration3)

        val permissionGroup = this.permissionGroupService.findByName("admin").join()
        val updateRequest = this.permissionGroupService.createUpdateRequest(permissionGroup)

        Assertions.assertThrows(PermissionGroupUpdateRequestImpl.GroupRecursionException::class.java) {
            try {
                updateRequest.submit().join()
            } catch (e: Exception) {
                throw unpackException(e)
            }
        }
    }

    private fun unpackException(ex: Throwable): Throwable {
        if (ex is CompletionException) {
            return unpackException(ex.cause!!)
        }
        if (ex is InvocationTargetException) {
            return unpackException(ex.cause!!)
        }
        if (ex is FutureOriginException) {
            return unpackException(ex.cause!!)
        }
        return ex
    }


}