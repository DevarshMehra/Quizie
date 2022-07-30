package Quizie.com.Model

import android.util.Log

class Plant(var genus: String,var species: String, var cultivar: String, var common: String,
            var pictureName: String, var description: String, var difficulty: Int, var id: Int = 0) {
    // class Plant(genus .. ) -> this is the primary constructor
    // var genus -> by putting var, we convert 'genus' from a parameter to a property
    // we can individually access properties

    // from JSON

    constructor() : this( "", "", "", "", "", "", 0, 0)
    // secondary constructor
    // this -> primary constructor ... we must call primary constructor from secondary constructor

    private var _plantName: String? = null
    // it is a convention to put '_' b4 variable to show that it is an instance variable
    // it is necessary that we declare are instance (member) variables as private so that other classes don't access it

    // getters and setters are used because when other classes want to access our instance variables they will access through getters and setters
    // and hence there is no direct contact and data is safe

    var plantName: String?
    // by default its a public variable

    get()= _plantName // getter -> to give value to outside class
    set(value){ // setter -> if user want to enter value
        _plantName = value
    }

    override fun toString(): String {
        Log.i("PLANT","$common - $species")
        // to check values in logcat

        return "$common $species"
        // will put this in button text

    }
}