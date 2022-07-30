package Quizie.com.Model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class DownloadingObject {
    // this is responsible for downloading JSON data from the internet

    @Throws(IOException::class)
    // means this class can throw this exception and we need to handle it

    fun downloadJSONDataFromLink(link: String): String {

        val stringBuilder: StringBuilder = StringBuilder()
        // assigning object of type StringBuilder to the constant

        /*
        Note on StringBuilder

        main operations are append and insert methods which are overloaded so as to accept data of any type,
        each method converts given data to string and then appends or insert the characters of that string to the string builder
        the append method always adds these characters at the end of the Builder
        insert method adds the characters at the specified position
         */

        val url: URL = URL(link)
        // passing link argument to constructor to create object URL

        val urlConnection = url.openConnection() as HttpURLConnection
        // casting to HttpURLConnection
        // openConnection() -> creating connection to the URL in the net
        // here connection is made

        try { // to handle exceptions of the below code

            val bufferedInputString: BufferedInputStream = BufferedInputStream(urlConnection.inputStream)
            // .inputStream -> we've created the connection and now we're taking inputStream
            // BufferedInputStream -> helps to get data from the internet

            /*
            Basically the BufferedInputStream creates a internal buffer array which takes elements from input stream (from the connection made)
            as and when required
             */

            val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(bufferedInputString))
            // Reading from inputStream (which we got in the prev line of code)

            //temporary string to hold each line read from the BufferedReader
            var inputLineString: String?
            inputLineString = bufferedReader.readLine()
            // .readLine() -> reading line and then returning read string

            while (inputLineString != null){ // getting whole JSON Data
                stringBuilder.append(inputLineString)
                inputLineString = bufferedReader.readLine()
            }
        } finally { // doesn't care what happened to try block, finally block will always be executed

            // regardless of success or failure of try block, we will disconnect , to not get memory leakage error
            urlConnection.disconnect()
            // if we don't put this, we'll have memory leak problem
        }
        return stringBuilder.toString() // .toString() returns the whole JSON data that we've read
    }

    fun downloadPlantPicture(pictureName: String?): Bitmap? {
        // Bitmap -> way to represent images

        var bitmap: Bitmap? = null // this will be returned

        val pictureLink: String = PLANTPLACES_COM + "/photos/$pictureName"
        // getting picture from link (concatenating in link)
        // pictureName -> specific image

        val pictureURL = URL(pictureLink) // converting string to URL

        val inputStream = pictureURL.openConnection().getInputStream()
        // making connection and getting input stream

        if (inputStream != null)
        {
            bitmap = BitmapFactory.decodeStream(inputStream)

        }
        return bitmap
    }

    companion object{
        val PLANTPLACES_COM = "http://www.plantplaces.com"
        // companion (friend) to DownloadingObject class .. can access its private members
        // OOP concept used
    }
}