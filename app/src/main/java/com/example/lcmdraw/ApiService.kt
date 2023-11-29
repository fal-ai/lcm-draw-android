package com.example.lcmdraw
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

data class ImageRequest(val image_url: String, val prompt: String)
data class ImageItem(val url: String)
data class ImageResponse(val images: List<ImageItem>)

class ApiService {

    interface FalApi {
        @POST("/")
        suspend fun sendImage(@Body request: ImageRequest): ImageResponse
    }
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Key ${BuildConfig.FAL_KEY}")
                .build()
            chain.proceed(newRequest)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://110602490-lcm-sd15-i2i.gateway.alpha.fal.ai/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val api = retrofit.create(FalApi::class.java)

    suspend fun sendImage(base64: String): String {
        val request = ImageRequest(base64, "a green ribbon in the sky")
        val response = api.sendImage(request)
        return response.images.firstOrNull()?.url ?: ""
    }
}
