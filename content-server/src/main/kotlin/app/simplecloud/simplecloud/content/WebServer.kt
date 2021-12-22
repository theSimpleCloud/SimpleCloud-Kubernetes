package app.simplecloud.simplecloud.content

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.utils.io.*
import java.io.File

class WebServer {

    val fileRepo = FileRepo()

    private val server = embeddedServer(Netty, 8008) {
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            method(HttpMethod.Patch)
            header(HttpHeaders.AccessControlAllowHeaders)
            header(HttpHeaders.ContentType)
            header(HttpHeaders.AccessControlAllowOrigin)
            anyHost()
        }
        routing {
            get("/content/{...}") {
                val filePath = transformUriToFilePath(call.request.uri)
                println("Received get for ${filePath}")

                val file = fileRepo.getFile(filePath)
                if (file.exists()) {
                    call.respondFile(file)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            post("/content/{...}") {
                println("Received post at ${call.request.uri}")
                try {
                    handleUpload(call)
                } catch (e: Exception) {
                    e.printStack()
                }
            }
        }
    }.start(wait = false)

    private fun transformUriToFilePath(uri: String): String {
        return uri.replaceFirst("/content/", "")
    }

    private suspend fun handleUpload(call: ApplicationCall) {
        val filePath = transformUriToFilePath(call.request.uri)
        val multipart = call.receiveMultipart()
        val allParts = multipart.readAllParts()
        if (allParts.isEmpty()) {
            println("no parts")
            call.respond(HttpStatusCode.BadRequest)
            return
        }
        if (allParts[0] !is PartData.FileItem) {
            println("no file")
            call.respond(HttpStatusCode.BadRequest)
            return
        }
        fileRepo.saveFile(filePath, allParts as List<PartData.FileItem>)
        call.respond("Received")
    }

}