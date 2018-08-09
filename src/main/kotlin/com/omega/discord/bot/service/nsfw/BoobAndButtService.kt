package com.omega.discord.bot.service.nsfw

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.IOException


object BoobAndButtService {

    private val logger = LoggerFactory.getLogger(javaClass)

    private const val BOOBS_MEDIA_BASE_URL = "http://media.oboobs.ru/"
    private const val BUTTS_MEDIA_BASE_URL = "http://media.obutts.ru/"

    private val client = OkHttpClient()

    fun getRandomImages(imageType: ImageType, count: Int, resultCallback: ResultCallback) {

        val requestUrl = when (imageType) {
            ImageType.BOOBS -> "http://api.oboobs.ru/boobs/0/$count/random"
            ImageType.BUTTS -> "http://api.obutts.ru/butts/0/$count/random"
        }
        val request = Request.Builder()
                .url(requestUrl)
                .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                logger.error("Request failure", e)

                resultCallback.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {

                val bodyData = response.body()!!.string()
                val responseJSON = JSONArray(bodyData)

                val results = mutableListOf<NSFWResult>()
                responseJSON.forEach { it ->
                    val jsonObject = (it as JSONObject)

                    val modelName =
                            if (jsonObject.isNull("model")) null
                            else jsonObject.getString("model")
                                    .let {
                                        if (it.isBlank()) null else it
                                    }
                    val imagePath = jsonObject.getString("preview")

                    val baseUrl = if (imageType == ImageType.BOOBS) BOOBS_MEDIA_BASE_URL else BUTTS_MEDIA_BASE_URL
                    val imageUrl = "$baseUrl$imagePath"

                    results += NSFWResult(imageUrl, modelName)
                }

                resultCallback.onResult(results)
            }
        })
    }

    enum class ImageType {

        BOOBS, BUTTS
    }

    interface ResultCallback {

        fun onFailure(e: IOException)

        fun onResult(results: List<NSFWResult>)
    }

    data class NSFWResult(val imageUrl: String, val modelName: String?)
}