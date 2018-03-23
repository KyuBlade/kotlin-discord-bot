package com.omega.discord.bot.service.nsfw

import okhttp3.*
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.IOException


object BoobAndButtService {

    private val logger = LoggerFactory.getLogger(javaClass)

    private const val BOOBS_MEDIA_BASE_URL = "http://media.oboobs.ru/"
    private const val BUTTS_MEDIA_BASE_URL = "http://media.obutts.ru/"

    private val client = OkHttpClient()
    private val boobRequest = Request.Builder()
            .url("http://api.oboobs.ru/boobs/0/1/random")
            .build()
    private val buttRequest = Request.Builder()
            .url("http://api.obutts.ru/butts/0/1/random")
            .build()

    fun getRandomImage(imageType: ImageType, resultCallback: ResultCallback) {

        val request = if (imageType == ImageType.BOOBS) boobRequest else buttRequest

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                logger.error("Request failure", e)

                resultCallback.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {

                val bodyData = response.body()!!.string()
                val responseJSON = JSONArray(bodyData)

                val imagePath = responseJSON.getJSONObject(0).getString("preview")

                val baseUrl = if (imageType == ImageType.BOOBS) BOOBS_MEDIA_BASE_URL else BUTTS_MEDIA_BASE_URL
                val imageUrl = "$baseUrl$imagePath"

                resultCallback.onResult(imageUrl)
            }
        })
    }

    enum class ImageType {

        BOOBS, BUTTS
    }

    interface ResultCallback {

        fun onFailure(e: IOException)

        fun onResult(imageUrl: String)
    }
}