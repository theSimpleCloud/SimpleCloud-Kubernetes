package app.simplecloud.simplecloud.restserver.base.exception

import io.ktor.http.*

/**
 * Date: 14.03.22
 * Time: 16:31
 * @author Frederick Baier
 *
 */
class BodyParseException(
    className: String
) : HttpException(HttpStatusCode.BadRequest, "Failed to parse body to class '${className}'") {
}