package com.elmz.shelfthing.util

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

object Util {
    private val TAG = makeTag(Util::class.java)

    @JvmStatic
    fun makeTag(c: Class<*>): String {
        return c.name
    }

    @JvmStatic
    fun inputStreamToJSON(inputStream: InputStream): JSONObject? {
        try {
            val temp = inputStreamToBufferedString(inputStream) ?: return null
            return JSONObject(temp)
        } catch (e: JSONException) {
            Log.e(TAG, "Exception", e)
        }

        return null
    }

    @JvmStatic
    fun inputStreamToJSONArray(inputStream: InputStream): JSONArray? {
        try {
            val temp = inputStreamToBufferedString(inputStream) ?: return null
            return JSONArray(temp)
        } catch (e: JSONException) {
            Log.e(TAG, "Exception", e)
        }

        return null
    }

    @JvmStatic
    private fun inputStreamToBufferedString(inputStream: InputStream?): String? {
        try {
            val reader = BufferedReader(InputStreamReader(inputStream!!, "UTF-8"), 8)
            val sb = StringBuilder()
            var line = reader.readLine()
            while (line != null) {
                sb.append(line).append("\n")
                line = reader.readLine()
            }
            return sb.toString()
        } catch (e: IOException) {
            Log.e(TAG, "Exception", e)
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }

    @JvmStatic
    @Throws(JSONException::class)
    fun JSONArrayToStringArray(array: JSONArray): Array<String> {
        val generic = arrayOfNulls<String>(array.length())
        for (i in generic.indices) {
            generic[i] = array.getString(i)
        }
        return generic as Array<String>
    }

    @JvmStatic
    @Throws(JSONException::class)
    fun JSONArrayToIntArray(array: JSONArray): IntArray {
        val generic = IntArray(array.length())
        for (i in generic.indices) {
            generic[i] = array.getInt(i)
        }
        return generic
    }

    @JvmStatic
    @Throws(JSONException::class)
    fun <E> JSONArrayToList(array: JSONArray): ArrayList<E> {
        val length = array.length()
        val list = ArrayList<E>(length)
        (0 until length).mapTo(list) { array.get(it) as E }
        return list
    }

    @JvmStatic
    @JvmOverloads
    fun pxToDp(dp: Int, resources: Resources = Resources.getSystem()): Int {
        return (dp * resources.displayMetrics.density + 0.5).toInt()
    }

    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }
}
