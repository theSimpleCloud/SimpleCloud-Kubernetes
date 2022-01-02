package app.simplecloud.simplecloud.api.text

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 20:17
 * @author Frederick Baier
 *
 * A text to be sent as chat message to a player
 *
 */
class CloudText(val text: String) {


    /**
     * The text shall be shown when hovering over the [text]
     */
    @Volatile var hover: String? = null
        private set

    /**
     * The action content to perform when the text is clicked.
     */
    @Volatile var click: String? = null
        private set

    /**
     * The action that will be performed when the text is clicked.
     */
    @Volatile var clickEventType: ClickEventType? = null
        private set

    /**
     * The [CloudText] to append to this text.
     */
    @Volatile var appendedCloudText: CloudText? = null
        private set

    fun addHover(hover: String): CloudText {
        this.hover = hover
        return this
    }

    fun addClickEvent(clickEventType: ClickEventType, value: String): CloudText {
        this.clickEventType = clickEventType
        click = value
        return this
    }

    fun setAppendedCloudText(cloudText: CloudText): CloudText {
        this.appendedCloudText = cloudText
        return this
    }

    enum class ClickEventType {
        RUN_COMMAND, SUGGEST_COMMAND, OPEN_URL
    }

}