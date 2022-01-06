package app.simplecloud.simplecloud.api.internal.request.process

import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.request.process.ProcessUpdateRequest
import java.util.*

/**
 * Date: 05.01.22
 * Time: 17:13
 * @author Frederick Baier
 *
 * Internal interface, that adds internal methods for updating a process.
 * Do not use this on you own.
 *
 */
interface InternalProcessUpdateRequest : ProcessUpdateRequest {

    /**
     * Sets the ignite id of the process to update
     */
    fun setIgniteId(id: UUID): InternalProcessUpdateRequest

    /**
     * Sets the state of the process to update
     */
    fun setState(processState: ProcessState): InternalProcessUpdateRequest

    override fun setMaxPlayers(maxPlayers: Int): InternalProcessUpdateRequest

    override fun setVisible(visible: Boolean): InternalProcessUpdateRequest

}