package Quizie.com.Model

import org.json.JSONArray
import org.json.JSONObject

class ParsePlantUtility {

    fun parsePlantObjectsFromJSONData() :List<Plant>? { // List<Plant> -> we will return a list of plant objects

        var allPlantObjects: ArrayList<Plant> = ArrayList() // we return this, will contain all JSON Objects
        var downloadingObject = DownloadingObject() // this object will connect to the internet

        var topLevelPlantJSONData = downloadingObject.downloadJSONDataFromLink("http://plantplaces.com/perl/mobile/flashcard.pl")
        // getting the first whole JSON object which has everything
        // here topLevelJSON Object will be assigned

        // the data that we get above from the internet is in raw string form, so to convert it to JSON form we apply the below code
        var topLevelJSONObject: JSONObject = JSONObject(topLevelPlantJSONData)

        var plantObjectsArray: JSONArray = topLevelJSONObject.getJSONArray("values")
        // values -> key -> can see webpage

        var index: Int = 0 // like array index

        while (index<plantObjectsArray.length())
        {
            var plantObject: Plant = Plant()
            var jsonObject = plantObjectsArray.getJSONObject(index)

            with(jsonObject){
                // by using with() there is no need to repeatedly mention jsonObject.getString() for each line
                // basically no need to write jsonObject -> cleaner code

                plantObject.genus = getString("genus")
                plantObject.species = getString("species")
                plantObject.cultivar = getString("cultivar")
                plantObject.common = getString("common")
                plantObject.pictureName = getString("picture_name")
                plantObject.description = getString("description")
                plantObject.difficulty = getInt("difficulty")
                plantObject.id = getInt("id")

            }

            allPlantObjects.add(plantObject)
            index++
        }

        return allPlantObjects
        
    }
}