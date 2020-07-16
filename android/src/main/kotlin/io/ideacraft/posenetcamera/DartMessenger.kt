package io.ideacraft.posenetcamera

import android.text.TextUtils
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import java.util.*
import kotlin.collections.HashMap

class DartMessenger(messenger: BinaryMessenger?, eventChannelId: Long) {
    private var eventSink: EventSink? = null

    enum class EventType {
        ERROR, CAMERA_CLOSING
    }

    fun sendCameraClosingEvent() {
        send(EventType.CAMERA_CLOSING, null)
    }

    fun send(eventType: EventType, description: String?) {
        if (eventSink == null) {
            return
        }
        val event: MutableMap<String, String?> = HashMap()
        event["eventType"] = eventType.toString().toLowerCase(Locale.ROOT)

        // Only errors have a description.
        if (eventType == EventType.ERROR && !TextUtils.isEmpty(description)) {
            event["errorDescription"] = description
        }
        eventSink!!.success(event)
    }

    init {
        EventChannel(messenger, "posenetCameraViewPlugin/cameraEvents$eventChannelId")
                .setStreamHandler(
                        object : EventChannel.StreamHandler {
                            override fun onListen(arguments: Any?, sink: EventSink?) {
                                eventSink = sink
                            }

                            override fun onCancel(arguments: Any?) {
                                eventSink = null
                            }
                        })
    }
}