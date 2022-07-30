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
    // ? -> nullable ... with this at run-time null ptr exception won't come

    private var photoGalleryButton : Button? = null
    private var imageTaken : ImageView? = null // saving the image that is clicked in this

    val OPEN_CAMERA_BUTTON_REQUEST_ID = 1000 // value should be unique
    // this a request code that we pass to cameraIntent
    // its a convention to capitalise instance variables

    val OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID = 2000

    // we will use these first in onPostExecute() of DownloadingPlantTask
    var correctAnswerIndex: Int = 0
    var correctPlant : Plant? = null

    var numberOfTimesUserAnsweredCorrectly: Int = 0
    var numberOfTimesUserAnsweredInCorrectly: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) { // this func is called whenever this activity is called
        // whenever app is started onCreate is called

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main) // put activity_main.xml on mobile screen
        setSupportActionBar(findViewById(R.id.toolbar)) // this puts toolbar on the top of our layout

        setProgressBar(false) // we dont want progress bar to come up when the app is just started
        displayUIWidgets(false) // to not show UI - func defined at the end -- starting of app we dont want to show all UI

        YoYo.with(Techniques.Pulse)
                .duration(700)
                .repeat(5)
                .playOn(btnNextPlant) // animation on btnNextPlant

        cameraButton = findViewById<Button>(R.id.btnOpenCamera)
        photoGalleryButton = findViewById<Button>(R.id.btnOpenPhotoGallery)
        imageTaken = findViewById<ImageView>(R.id.imgTaken)

        cameraButton?.setOnClickListener(View.OnClickListener { // {} -> anonymous function
            // ? -> if cameraButton is NULL, NULL will be returned and setOnClickListner won't be executed

            Toast.makeText(this,"Camera Button called",Toast.LENGTH_SHORT).show()

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // implicit intent -> we just care about output and not about how we got the output
            // its a good practice to first always use val (constant) -> cuz then it cant be changed
            // if later needed, we can change it to var

            startActivityForResult(cameraIntent, OPEN_CAMERA_BUTTON_REQUEST_ID)
            // assigning the previously declared requestCode [OPEN_CAMERA_BUTTON_REQUEST_ID] to cameraIntent

            // with this we can open camera and click a picture

        })

        photoGalleryButton?.setOnClickListener(View.OnClickListener {

            Toast.makeText(this,"Photo Gallery Button called",Toast.LENGTH_SHORT).show()

            val galleryIntent = Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // ACTION_PICK -> to access external photos
            // to access photo gallery we need to pass proper URI (Universal Resource Indicator)

            startActivityForResult(galleryIntent,OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID)
        })

        // FAB next plant
        // Floating Action Button is used to call next plant
        // btnNextPlant -> id of FAB
        btnNextPlant.setOnClickListener(View.OnClickListener {
            // .setOnClickListener -> will be executed when user taps on that UI

            if(checkForInternetConnection()){

                setProgressBar(true) // we want progress bar to be shown when we click on FAB
                try {
                    // always good to use try catch to avoid crashing

                    val innerClassObject = DownloadingPlantTask()
                    innerClassObject.execute()

                } catch (e: Exception){ // Exception -> superclass of all exceptions, we can catch all exceptions using this
                    e.printStackTrace() // will come in log

                    // if try is executed, catch won't be executed
                }

                // gradient color -> array of colors
                var gradientColors: IntArray = IntArray(2)
                gradientColors.set(0, Color.parseColor("#FFFF66")) // start color
                gradientColors.set(1, Color.parseColor("#ff0008")) // end color

                var gradientDrawable: GradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,gradientColors)
                // grade colors from top to bottom in button

                var convertDipValue = dipToFloat(this@MainActivity, 50f)
                // dipToFloat() func defined just below (after oncreate)

                gradientDrawable.setCornerRadius(convertDipValue)
                gradientDrawable.setStroke(5,Color.parseColor("#ffffff")) // #ffffff - white

                button1.setBackground(gradientDrawable)
                button2.setBackground(gradientDrawable)
                button3.setBackground(gradientDrawable)
                button4.setBackground(gradientDrawable)

            }
        })
    }

    fun dipToFloat(context: Context, dipValue: Float): Float{ // got this from StackOverflow :)
        // we use this to get roundness of buttons
        // cuz in button_border.xml -> 50dp
        // and above in btnNextPlant we put radius 50f
        // hence we have to convert dp to float get roundness of buttons

        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // this is responsible to get something back from intent

        // requestCode -> whether user accepts or cancels picture
        // data -> picture taken from camera

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_CAMERA_BUTTON_REQUEST_ID){ // Camera

            if (resultCode == Activity.RESULT_OK){

                val imageData = data?.getExtras()?.get("data") as Bitmap
                // casting image to Bitmap (Bitmap is used to maintain images)

                imageTaken?.setImageBitmap(imageData)
                // now image will be saved in imageView

            }
        }

        if (requestCode == OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID) { // Photo Gallery

            if (resultCode == Activity.RESULT_OK) {

                val contentURI = data?.getData()
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                // contentResolver is required to get content from other app, here, photo gallery

                imageTaken?.setImageBitmap(bitmap)


            }
        }

    }

    fun button1IsClicked(buttonView: View){
        specifyTheRightAndWrongAnswer(0) // passing 0 into function
    }

    fun button2IsClicked(buttonView: View){
        specifyTheRightAndWrongAnswer(1)
    }

    fun button3IsClicked(buttonView: View){
        specifyTheRightAndWrongAnswer(2)
    }

    fun button4IsClicked(buttonView: View){
        specifyTheRightAndWrongAnswer(3)
    }

    inner class DownloadingPlantTask: AsyncTask<String, Int, List<Plant>>(){
        // creating a inner class -> a class within a class ... this inner class can access members of its parent class
        // but parent class members cant access inner class members

        // we're making an inner class because we want to keep our background and UI threads separate, so that it doesnt interfere and app doesnt crash
        // like while downloading from internet, this process should only happen in background and not come in UI

        // DowloadingPlantTask is inheriting from AsyncTask
        // Int -> this shows % of download
        // List<Plant> -> return type

        override fun doInBackground(vararg params: String?): List<Plant>?{ // Background Thread

            // override -> this method already exists in AsyncTask (initially empty) .. hence we're overriding it with our stuff
            // varag params -> array of JSON objects

            // can access background thread, not user interface thread

            val parsePlant = ParsePlantUtility()

            return parsePlant.parsePlantObjectsFromJSONData()
            // will return List<Plant>
        }

        override fun onPostExecute(result: List<Plant>?){ // UI Thread -> like accessing buttons, we cant do this in doInBackground

            // result -> will contain the returned value of doInBackground method
            // since onPostExecute takes returned value of doInBackground, if onPostExecute is executed that means doInBackground in successfully executed

            // this will execute after downloading is complete in doInBackground
            // List<Plant> is the same List<Plant> that is automatically sent to onPostExecute when it is returned from doInBackground

            super.onPostExecute(result)
            // can access UI thread , not background thread

            var numberOfPlants = result?.size ?: 0
            // result? -> ? -> if null, will not execute .size() -> it will assign 0

            if(numberOfPlants>0){

                var randomPlantIndexForButton1 : Int =(Math.random() * result!!.size).toInt()
                // this assigns a random index number (plant index)
                // Math.random() -> returns anything between 0(incld) and 1(not incld)
                // .toInt() -> converts to integer (if decimal)

                var randomPlantIndexForButton2 : Int =(Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton3 : Int =(Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton4 : Int =(Math.random() * result!!.size).toInt()

                var allRandomPlants = ArrayList<Plant>()

                allRandomPlants.add(result.get(randomPlantIndexForButton1))
                // adding objects of the specified index

                allRandomPlants.add(result.get(randomPlantIndexForButton2))
                allRandomPlants.add(result.get(randomPlantIndexForButton3))
                allRandomPlants.add(result.get(randomPlantIndexForButton4))

                // out of the below 4 buttons, 1 is the correct answer
                button1.text = result.get(randomPlantIndexForButton1).toString()
                button2.text = result.get(randomPlantIndexForButton2).toString()
                button3.text = result.get(randomPlantIndexForButton3).toString()
                button4.text = result.get(randomPlantIndexForButton4).toString()

                correctAnswerIndex = (Math.random() * allRandomPlants.size).toInt()
                // will give random value from 0,1,2,3 buttons

                correctPlant = allRandomPlants.get(correctAnswerIndex)
                // getting object of correct answer

                val downloadingImageTask = DownloadingImageTask()
                downloadingImageTask.execute(allRandomPlants.get(correctAnswerIndex).pictureName)
                // we're downloading the image of the correct answer only
            }


        }
    }

    override fun onStart() {
        super.onStart()
        // super refers to super class in which onStart() is there

        Toast.makeText(this,"onStart method called",Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this,"onResume method called",Toast.LENGTH_SHORT).show()

        checkForInternetConnection()
    }

    override fun onPause() {
        // here we deal with the user leaving our activity, any changes made by the user at this point should be committed/saved
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
        // content_main->image->onClick-> ___ we have entered this same fun name here, so that when we click the image, this fun runs

        val randomNumber: Int = (Math.random() * 6).toInt() + 1
        // here we are generating a random number between 1-6 (both incld)
        // Math.random() -> generates a decimal (double type) between 0(incld) - 1(not incld)
        // * 6 -> cuz at the end we want random number bet 1-6
        // toInt() -> will give only the first int digit
        // + 1 -> to incld 1 and 6 [ 0.1 * 6 + 1 = 1 ]

        Log.i("Tag", "Random Number is $randomNumber")
        // displaying random number in logcat
        // we open logcat to see generated random number after clicking on image in emulator

        when(randomNumber) { // when is same as 'switch' in c++
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
        // as -> casting to required type -> ConnectivityManager
        // .Context -> allows us to access service outside of our current activity

        val networkInfo = connectivityManager.activeNetworkInfo
        // we need info on network of device
        // .activeNetworkInfo -> access the .ACCESS_NETWORK_STATE in AndroidManifest.xml

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
        // AlertDialog -> DialogBox

        alertDialog.setTitle("Network Error")
        alertDialog.setMessage("Please check your internet connection")

        // There are 2 types of buttons -> + and -
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK",{ dialog: DialogInterface?, which: Int -> startActivity(Intent(Settings.ACTION_SETTINGS)) })
        // creating listner, setButton method tells to execute an action after we click on 'OK'

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancel", { dialog: DialogInterface?, which: Int -> Toast.makeText(this@MainActivity, "You must be connected to internet", Toast.LENGTH_SHORT).show()
            finish()
            // finish -> close current activity if user clicked on Cancel

        })

     alertDialog.show() // will show dialogBox

    }

    //Specify right and wrong answer
    private fun specifyTheRightAndWrongAnswer(userGuess: Int){

        when(correctAnswerIndex){ // correctAnswerIndex -> index of correct answer
            0 -> button1.setBackgroundColor(Color.CYAN) // CYAN (green) -> color of correct answer
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
        // Int -> progress bar
        // Bitmap -> return this

        override fun doInBackground(vararg pictureName: String?): Bitmap? { // to not interact with UI
            // varag -> like array .. this array will hold only 1 object

            try{
                // downloading picture from net is an error prone process, hence put inside try block

                val downloadingObject = DownloadingObject()

                val plantBitmap: Bitmap? = downloadingObject.downloadPlantPicture(pictureName[0])
                // accessing the only object that is there in pictureName
                // 0 ->index

                return plantBitmap
            } catch (e: Exception){
                e.printStackTrace() // will show error in logcat
            }
            return null // if try is not executed, return null
        }

        override fun onPostExecute(result: Bitmap?) { // UI Thread
            super.onPostExecute(result)
            setProgressBar(false)
            displayUIWidgets(true) // from here on we want to show all UI as onPostExecute of DownloadImageTask is the last to be executed

            playAnimationOnView(imgTaken, Techniques.Tada)
            playAnimationOnView(button1, Techniques.RollIn)
            playAnimationOnView(button2, Techniques.RollIn)
            playAnimationOnView(button3, Techniques.RollIn)
            playAnimationOnView(button4, Techniques.RollIn)
            playAnimationOnView(txtState, Techniques.Swing)
            playAnimationOnView(txtWrongAnswers, Techniques.FlipInX)
            playAnimationOnView(txtRightAnswers, Techniques.Landing)

            imgTaken.setImageBitmap(result) // result -> will get this returned from doInBackground
        }
    }

    private fun setProgressBar(show: Boolean){
        if(show){

            linearLayoutProgress.setVisibility(View.VISIBLE) // SHOW LL VER
            progressBar.setVisibility(View.VISIBLE) // SHOW PROGRESS BAR
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            // .setFlags -> to prevent user from interacting with UI

        }

        else if (!show){
            linearLayoutProgress.setVisibility(View.GONE) // HIDE LL VER
            progressBar.setVisibility(View.GONE) // HIDE PROGRESS BAR
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            // .clearFlags -> allow user to interact with UI
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
            // INVISIBLE - UI is there, but not visible to user
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
        // Techniques -> contains animation effects that we're gonna put in views
        YoYo.with(technique)
                .duration(700)
                .repeat(0) // to decide how many times should animation be repeated
                .playOn(view)
    }
}

//