package com.yunusemrearslan.kotlinandroidserviceexamples

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yunusemrearslan.kotlinandroidserviceexamples.databinding.ActivityMapsBinding
import com.yunusemrearslan.kotlinandroidserviceexamples.db.LocationDao
import com.yunusemrearslan.kotlinandroidserviceexamples.db.LocationNoteDBHelper
import com.yunusemrearslan.kotlinandroidserviceexamples.model.LocationModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback ,GoogleMap.OnMapLongClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    private val mDisposable = CompositeDisposable()
    var selectedLatitude : Double? =null
    var selectedLongitude : Double? = null
    var locationFromMain: LocationModel?=null
    private lateinit var db : LocationNoteDBHelper
    private lateinit var locationDao:LocationDao
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var locations : ArrayList<LocationModel>
    var reLocation:Boolean?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()
        selectedLatitude = 0.0
        selectedLongitude= 0.0

        sharedPreferences =getSharedPreferences("com.yunusemrearslan.kotlinandroidserviceexamples",
            MODE_PRIVATE)
        reLocation=false

         db = Room.databaseBuilder(applicationContext,LocationNoteDBHelper::class.java,"Locations").build()
        locationDao = db.locationDao()

        val locationDao = db.locationDao()
        mDisposable.add(locationDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::getLocationMark))

    }
    private fun getLocationMark(locationList:List<LocationModel>){
        locationList.forEach{
            val location =LatLng(it.latitude,it.longitude)
            mMap.addMarker(MarkerOptions().position(location!!).title(it.title))

        }
    }

    override fun onMapLongClick(latLng: LatLng?) {
        val context = this
        val builder = MaterialAlertDialogBuilder(context)

        // dialog title
        builder.setTitle("Annotate location .")

        // dialog message view
        val constraintLayout = getEditTextLayout(context)
        builder.setView(constraintLayout)

        val textInputLayout = constraintLayout.
        findViewWithTag<TextInputLayout>("textInputLayoutTag")
        val textInputEditText = constraintLayout.
        findViewWithTag<TextInputEditText>("textInputEditTextTag")

        // alert dialog positive button
        builder.setPositiveButton("Submit"){dialog,which->
            val note=textInputEditText.text

            mMap.addMarker(MarkerOptions().position(latLng!!).title(note.toString()))
            var location = LocationModel(note.toString(),latitude = latLng.latitude,longitude = latLng.longitude)
            mDisposable.add(locationDao.insert(location).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponse))

        }

        builder.setNeutralButton("Cancel",null)

        // set dialog non cancelable
        builder.setCancelable(false)

        // finally, create the alert dialog and show it
        val dialog = builder.create()

        dialog.show()

        // initially disable the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int,
                                           p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int,
                                       p2: Int, p3: Int) {
                if (p0.isNullOrBlank()){
                    textInputLayout.error = "Note is required."
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .isEnabled = false
                }else{
                    textInputLayout.error = ""
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .isEnabled = true
                }
            }
        })
    }
    private fun handleResponse() {
        Toast.makeText(applicationContext,"Success",Toast.LENGTH_LONG).show()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {

                val userLocation =LatLng(location.latitude,location.longitude)
                if(!reLocation!!){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                    reLocation=true
                }
            }
        }
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root,"Permission needed for location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",){
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()

            }else{
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
            mMap.isMyLocationEnabled=true


        }
    }
    private fun registerLauncher(){
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if(result){
                // permission granted
                if(ContextCompat.checkSelfPermission(this@MapsActivity,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    mMap.isMyLocationEnabled=true
                }
            }else{
                //Permission denied
                Toast.makeText(this@MapsActivity,"Permission needed!",Toast.LENGTH_LONG).show()
            }
        }
    }
    fun getEditTextLayout(context:Context):ConstraintLayout{
        val constraintLayout = ConstraintLayout(context)
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        constraintLayout.layoutParams = layoutParams
        constraintLayout.id = View.generateViewId()

        val textInputLayout = TextInputLayout(context)
        textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        layoutParams.setMargins(
            32.toDp(context),
            8.toDp(context),
            32.toDp(context),
            8.toDp(context)
        )
        textInputLayout.layoutParams = layoutParams
        textInputLayout.hint = "Add note"
        textInputLayout.id = View.generateViewId()
        textInputLayout.tag = "textInputLayoutTag"


        val textInputEditText = TextInputEditText(context)
        textInputEditText.id = View.generateViewId()
        textInputEditText.tag = "textInputEditTextTag"

        textInputLayout.addView(textInputEditText)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintLayout.addView(textInputLayout)
        return constraintLayout
    }


    fun Int.toDp(context: Context):Int =TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
    ).toInt()
}
