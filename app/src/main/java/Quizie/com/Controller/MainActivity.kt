package Quizie.com.Controller

import Quizie.com.Model.DownloadingObject
import Quizie.com.Model.ParsePlantUtility
import Quizie.com.Model.Plant
import Quizie.com.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.getSystemService
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var cameraButton : Button? = null
    private var photoGalleryButton : Button? = null
    private var imageTaken : ImageView? = null

    val OPEN_CAMERA_BUTTON_REQUEST_ID = 1000
    val OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID = 2000

    var correctAnswerIndex: Int = 0
    var correctPlant : Plant? = null

    var numberOfTimesUserAnsweredCorrectly: Int = 0
    var numberOfTimesUserAnsweredInCorrectly: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

      //  progressBar.setVisibility(View.GONE)
        setProgressBar(false)
        displayUIWidgets(false)

        YoYo.with(Techniques.Pulse)
                .duration(700)
                .repeat(5)
                .playOn(btnNextPlant)


//        if(checkForInternetConnection()){
//        val innerClassObject = DownloadingPlantTask()
//        innerClassObject.execute()
//        }

      /*  Toast.makeText(this,"On create method called",Toast.LENGTH_SHORT).show()
        val myPlant: Plant = Plant( "", "","","","",
                "", 0, 0)

      //  Plant("Koelreuteria", "paniculata", "", "Golden Rain Tree", "Koelreuteria_paniculata_branch.JPG",
       //         "Branch of Koelreuteria paniculata", 3, 24 )

        myPlant.plantName = "Wadas Memory Magnolia"
        var nameOfPlant = myPlant.plantName


        var flower = Plant()
        var tree = Plant()

        var collectionOfPlants : ArrayList<Plant> = ArrayList()

        collectionOfPlants.add(flower)
        collectionOfPlants.add(tree)*/
        

        cameraButton = findViewById<Button>(R.id.btnOpenCamera)
        photoGalleryButton = findViewById<Button>(R.id.btnOpenPhotoGallery)
        imageTaken = findViewById<ImageView>(R.id.imgTaken)

        cameraButton?.setOnClickListener(View.OnClickListener {

            Toast.makeText(this,"Camera Button called",Toast.LENGTH_SHORT).show()

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, OPEN_CAMERA_BUTTON_REQUEST_ID)

        })

        photoGalleryButton?.setOnClickListener(View.OnClickListener {

            Toast.makeText(this,"Photo Gallery Button called",Toast.LENGTH_SHORT).show()

            val galleryIntent = Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(galleryIntent,OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID)
        })

        // FAB next plant
        btnNextPlant.setOnClickListener(View.OnClickListener {
            if(checkForInternetConnection()){
               // progressBar.setVisibility(View.VISIBLE)
                setProgressBar(true)
                try {
                    val innerClassObject = DownloadingPlantTask()
                    innerClassObject.execute()
                } catch (e: Exception){
                    e.printStackTrace()
                }

//                button1.setBackgroundColor(Color.LTGRAY)
//                button2.setBackgroundColor(Color.LTGRAY)
//                button3.setBackgroundColor(Color.LTGRAY)
//                button4.setBackgroundColor(Color.LTGRAY)

                var gradientColors: IntArray = IntArray(2)
                gradientColors.set(0, Color.parseColor("#FFFF66"))
                gradientColors.set(1, Color.parseColor("#ff0008"))

                var gradientDrawable: GradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,gradientColors)

                var convertDipValue = dipToFloat(this@MainActivity, 50f)
                gradientDrawable.setCornerRadius(convertDipValue)
                gradientDrawable.setStroke(5,Color.parseColor("#ffffff"))

                button1.setBackground(gradientDrawable)
                button2.setBackground(gradientDrawable)
                button3.setBackground(gradientDrawable)
                button4.setBackground(gradientDrawable)



            }
        })
    }

    fun dipToFloat(context: Context, dipValue: Float): Float{
        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_CAMERA_BUTTON_REQUEST_ID){

            if (resultCode == Activity.RESULT_OK){

                val imageData = data?.getExtras()?.get("data") as Bitmap
                imageTaken?.setImageBitmap(imageData)

            }
        }

        if (requestCode == OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID) {

            if (resultCode == Activity.RESULT_OK) {

                val contentURI = data?.getData()
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                imageTaken?.setImageBitmap(bitmap)


            }
        }

    }

    fun button1IsClicked(buttonView: View){
//        Toast.makeText(this,"Button1 called",Toast.LENGTH_SHORT).show()
//        var myNumber=20
//        val myName: String="DEVARSH"
//        var numberOfLetters=myName.length
//
//        var animalName: String? = null
//        var noOfCharOfAnimalName = animalName?.length ?: 100

        specifyTheRightAndWrongAnswer(0)



    }

    fun button2IsClicked(buttonView: View){
       // Toast.makeText(this,"Button2 called",Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(1)
    }

    fun button3IsClicked(buttonView: View){
        //Toast.makeText(this,"Button3 called",Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(2)
    }

    fun button4IsClicked(buttonView: View){
        //Toast.makeText(this,"Button4 called",Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(3)
    }

    inner class DownloadingPlantTask: AsyncTask<String, Int, List<Plant>>(){

        override fun doInBackground(vararg params: String?): List<Plant>?{
            // can access background thread, not user interface thread

//            val downloadingObject: DownloadingObject = DownloadingObject()
//            var jsonData = downloadingObject.downloadJSONDataFromLink("http://plantplaces.com/perl/mobile/flashcard.pl")
//
//            Log.i("JSON",jsonData)

            val parsePlant = ParsePlantUtility()
            return parsePlant.parsePlantObjectsFromJSONData()
        }

        override fun onPostExecute(result: List<Plant>?){
            super.onPostExecute(result)
            // can access UI thread , not background thread

            var numberOfPlants = result?.size ?: 0

            if(numberOfPlants>0){

                var randomPlantIndexForButton1 : Int =(Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton2 : Int =(Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton3 : Int =(Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton4 : Int =(Math.random() * result!!.size).toInt()

                var allRandomPlants = ArrayList<Plant>()
                allRandomPlants.add(result.get(randomPlantIndexForButton1))
                allRandomPlants.add(result.get(randomPlantIndexForButton2))
                allRandomPlants.add(result.get(randomPlantIndexForButton3))
                allRandomPlants.add(result.get(randomPlantIndexForButton4))

                button1.text = result.get(randomPlantIndexForButton1).toString()
                button2.text = result.get(randomPlantIndexForButton2).toString()
                button3.text = result.get(randomPlantIndexForButton3).toString()
                button4.text = result.get(randomPlantIndexForButton4).toString()

                correctAnswerIndex = (Math.random() * allRandomPlants.size).toInt()
                correctPlant = allRandomPlants.get(correctAnswerIndex)

                val downloadingImageTask = DownloadingImageTask()
                downloadingImageTask.execute(allRandomPlants.get(correctAnswerIndex).pictureName)
            }


        }
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this,"onStart method called",Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this,"onResume method called",Toast.LENGTH_SHORT).show()

        checkForInternetConnection()
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(this,"onPause method called",Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(this,"onStop method called",Toast.LENGTH_SHORT).show()
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(this,"onRestart method called",Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this,"onDestroy method called",Toast.LENGTH_SHORT).show()
    }

    fun imageViewIsClicked(view: View) {

        val randomNumber: Int = (Math.random() * 6).toInt() + 1
        Log.i("Tag", "Random Number is $randomNumber")

      /*  if (randomNumber == 1){
            btnOpenCamera.setBackgroundColor(Color.BLUE)
        } else if ( randomNumber == 2){
            btnOpenPhotoGallery.setBackgroundColor(Color.GREEN)
        }else if ( randomNumber == 3){
            button1.setBackgroundColor(Color.MAGENTA)
        }
        else if ( randomNumber == 4){
            button2.setBackgroundColor(Color.YELLOW)
        }
        else if ( randomNumber == 5){
            button3.setBackgroundColor(Color.GRAY)
        }else if ( randomNumber == 6){
            button4.setBackgroundColor(Color.CYAN)
        }*/

        when(randomNumber) {
            1 -> btnOpenCamera.setBackgroundColor(Color.BLUE)
            2 -> btnOpenPhotoGallery.setBackgroundColor(Color.GREEN)
            3 -> button1.setBackgroundColor(Color.MAGENTA)
            4 -> button2.setBackgroundColor(Color.YELLOW)
            5 -> button3.setBackgroundColor(Color.GRAY)
            6 -> button4.setBackgroundColor(Color.CYAN)
        }

    }

    private fun checkForInternetConnection(): Boolean{

        val connectivityManager: ConnectivityManager = this.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isDeviceConnectedToInternet = networkInfo != null && networkInfo.isConnectedOrConnecting

        if(isDeviceConnectedToInternet){
            return true
        }else {
            createAlert()
            return false
        }

    }

    private fun createAlert(){

        val alertDialog: AlertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Network Error")
        alertDialog.setMessage("Please check your internet connection")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK",{ dialog: DialogInterface?, which: Int -> startActivity(Intent(Settings.ACTION_SETTINGS)) })

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancel", { dialog: DialogInterface?, which: Int -> Toast.makeText(this@MainActivity, "You must be connected to internet", Toast.LENGTH_SHORT).show()
            finish()

        })

     alertDialog.show()

    }

    //Specify right and wrong answer
    private fun specifyTheRightAndWrongAnswer(userGuess: Int){

        when(correctAnswerIndex){
            0 -> button1.setBackgroundColor(Color.CYAN)
            1 -> button2.setBackgroundColor(Color.CYAN)
            2 -> button3.setBackgroundColor(Color.CYAN)
            3 -> button4.setBackgroundColor(Color.CYAN)
        }

        if (userGuess == correctAnswerIndex){
            txtState.setText("Right!")
            numberOfTimesUserAnsweredCorrectly++
            txtRightAnswers.setText("$numberOfTimesUserAnsweredCorrectly")
        }else{
            var correctPlantName = correctPlant.toString()
            txtState.setText("Wrong. Choose : $correctPlantName")
            numberOfTimesUserAnsweredInCorrectly++
            txtWrongAnswers.setText("$numberOfTimesUserAnsweredInCorrectly")
        }
    }


    //Downloading Image Process
    inner class DownloadingImageTask: AsyncTask<String, Int, Bitmap?>(){

        override fun doInBackground(vararg pictureName: String?): Bitmap? {

            try{
                val downloadingObject = DownloadingObject()
                val plantBitmap: Bitmap? = downloadingObject.downloadPlantPicture(pictureName[0])
                return plantBitmap
            } catch (e: Exception){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            setProgressBar(false)
            displayUIWidgets(true)

            playAnimationOnView(imgTaken, Techniques.Tada)
            playAnimationOnView(button1, Techniques.RollIn)
            playAnimationOnView(button2, Techniques.RollIn)
            playAnimationOnView(button3, Techniques.RollIn)
            playAnimationOnView(button4, Techniques.RollIn)
            playAnimationOnView(txtState, Techniques.Swing)
            playAnimationOnView(txtWrongAnswers, Techniques.FlipInX)
            playAnimationOnView(txtRightAnswers, Techniques.Landing)

            imgTaken.setImageBitmap(result)
        }
    }

    private fun setProgressBar(show: Boolean){
        if(show){

            linearLayoutProgress.setVisibility(View.VISIBLE) // SHOW LL VER
            progressBar.setVisibility(View.VISIBLE) // SHOW PROGRESS BAR
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

        else if (!show){
            linearLayoutProgress.setVisibility(View.GONE) // HIDE LL VER
            progressBar.setVisibility(View.GONE) // HIDE PROGRESS BAR
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    //set Visibility of UI Widgets
    private fun displayUIWidgets(display: Boolean){
        if(display)
        {
            imgTaken.setVisibility(View.VISIBLE)
            button1.setVisibility(View.VISIBLE)
            button2.setVisibility(View.VISIBLE)
            button3.setVisibility(View.VISIBLE)
            button4.setVisibility(View.VISIBLE)
            txtState.setVisibility(View.VISIBLE)
            txtWrongAnswers.setVisibility(View.VISIBLE)
            txtRightAnswers.setVisibility(View.VISIBLE)

        } else if (!display){
            imgTaken.setVisibility(View.INVISIBLE)
            button1.setVisibility(View.INVISIBLE)
            button2.setVisibility(View.INVISIBLE)
            button3.setVisibility(View.INVISIBLE)
            button4.setVisibility(View.INVISIBLE)
            txtState.setVisibility(View.INVISIBLE)
            txtWrongAnswers.setVisibility(View.INVISIBLE)
            txtRightAnswers.setVisibility(View.INVISIBLE)
        }
    }

    //Playing Animations
    private fun playAnimationOnView(view: View?, technique: Techniques){
        YoYo.with(technique)
                .duration(700)
                .repeat(0)
                .playOn(view)
    }
}