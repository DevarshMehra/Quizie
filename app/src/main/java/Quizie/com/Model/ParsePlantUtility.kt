package Quizie.com.Model

import org.json.JSONArray
import org.json.JSONObject

class ParsePlantUtility {

    fun parsePlantObjectsFromJSONData() :List<Plant>? {

        var allPlantObjects: ArrayList<Plant> = ArrayList()
        var downloadingObject = DownloadingObject()
        var topLevelPlantJSONData = downloadingObject.downloadJSONDataFromLink("http://plantplaces.com/perl/mobile/flashcard.pl")
        var topLevelJSONObject: JSONObject = JSONObject(topLevelPlantJSONData)
        var plantObjectsArray: JSONArray = topLevelJSONObject.getJSONArray("values")

        var index: Int = 0

        while (index<plantObjectsArray.length())
        {
            var plantObject: Plant = Plant()
            var jsonObject = plantObjectsArray.getJSONObject(index)

            /* var genus: String,var species: String, var cultivar: String, var common: String,
            var pictureName: String, var description: String, var difficulty: Int, var id: Int = 0
              */

            with(jsonObject){
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