package com.example.lcmdraw
import ai.fal.falclient.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.delay


class ApiService {
    private val app = "110602490-lcm-sd15-i2i"
    private val authKey = BuildConfig.FAL_KEY

    private var webSocketConnection: WebSocketConnection? = null

    var onImageReceived: ((String) -> Unit)? = null

    private val onMessage: (String) -> Unit = { message ->
        println("Received message: $message")

        val jsonObject = JsonParser().parse(message).asJsonObject
        val images = jsonObject.getAsJsonArray("images")
        val url = images[0].asJsonObject.get("url").asString

        onImageReceived?.invoke(url)
    }

    private val onError: (Throwable) -> Unit = { error ->
        println("Error occurred: ${error.message}")
    }

    suspend fun sendImage(base64: String) {
        // Define the input parameters for the function
        val inputMap = hashMapOf(
            "prompt" to "a green ribbon in the sky",
            "image_url" to base64,
            "seed" to 6252023,
            "sync_mode" to 1,
            "strength" to 0.8,
            "num_inference_steps" to 4,
        )

        // Check if the connection is already open
        if (webSocketConnection?.isConnected() == true) {
            // Use the existing connection
            webSocketConnection?.send(Gson().toJson(inputMap))
        } else {
            // Open a new connection
            webSocketConnection = WebSocketConnection(app, onMessage, onError)
            webSocketConnection?.connect(authKey)

            while(!webSocketConnection?.isConnected()!!) {
                delay(100)
            }

            webSocketConnection?.send(Gson().toJson(inputMap))
        }

        // Close the connection if not used within 1000ms
        delay(1000)
        if (webSocketConnection?.isConnected() == true) {
            webSocketConnection?.close()
            webSocketConnection = null
        }
    }
}
