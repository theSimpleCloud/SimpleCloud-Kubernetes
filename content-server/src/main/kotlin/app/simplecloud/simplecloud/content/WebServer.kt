/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.content

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.utils.io.*

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