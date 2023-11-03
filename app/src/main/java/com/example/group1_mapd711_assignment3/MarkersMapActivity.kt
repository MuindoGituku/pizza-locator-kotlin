/**
 * @Group 1
 * @author Muindo Gituku (301372521)
 * @author Emre Deniz (301371047)
 * @date Nov 3, 2023
 * @description: Android Assignment 3 - Pizza Locator App
 */

package com.example.group1_mapd711_assignment3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.group1_mapd711_assignment3.databinding.ActivityMarkersMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Data classes for API response
data class PlaceResult(val results: List<Place>)
data class Place(
    val name: String,
    val vicinity: String,
    val formatted_address: String,
    val formatted_phone_number: String,
    val international_phone_number: String,
    val opening_hours: OpenNow,
    val rating: Double,
    val user_ratings_total: Double,
    val website: String,
    val geometry: Geometry,
    val photos: List<Photo>,
)
data class Geometry(val location: Location)

data class Photo(
    val height: Double,
    val photo_reference : String,
    val width: Double,
)

data class OpenNow(val open_now: Boolean)
data class Location(val lat: Double, val lng: Double)

// Retrofit service interface
interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("name") name: String,
        @Query("key") apiKey: String
    ): PlaceResult
}

class MarkersMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMarkersMapBinding

    // Define place longitude and latitude values
    private var townLatitude = 0.0
    private var townLongitude = 0.0
    private var userSelectedCity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkersMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        userSelectedCity = "Pizza Restaurants in "+intent.getStringExtra("selected_city")
        townLatitude = intent.getDoubleExtra("latitude", 0.00)
        townLongitude = intent.getDoubleExtra("longitude", 0.00)

        binding.selectedCityText.text = userSelectedCity

        // Obtain the SupportMapFragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Manipulate the map once available
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker to selected town
        val selectedTownPin = LatLng(townLatitude, townLongitude)
        mMap.addMarker(MarkerOptions().position(selectedTownPin).title(intent.getStringExtra("selected_city")))

        // Set default zoom on map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedTownPin, 13f))

        // Set default map type as normal
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Call fetch Nearby Pizza Restaurants and draw function
        fetchNearbyPizzaRestaurants()

        // Action for Normal Map radio button press
        binding.radioButtonNormal.setOnCheckedChangeListener { buttonView, isChecked ->
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }

        // Action for Satellite Map radio button press
        binding.radioButtonSatellite.setOnCheckedChangeListener { buttonView, isChecked ->
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    // Define function to fetch Nearby Pizza Restaurants and draw
    private fun fetchNearbyPizzaRestaurants(){
        // Google Places API key
        val apiKey = "AIzaSyDBQOf0tvMNiecFD8A5UQ-0JmHW8BL6JAA"

        // Coordinates of the location
        val latitude = townLatitude
        val longitude = townLongitude
        val location = "$latitude,$longitude"

        // Radius in meters (2 kilometer)
        val radius = 2000

        // Name of places to filter
        val name = "pizza"

        // Retrofit setup
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placesApiService = retrofit.create(PlacesApiService::class.java)

        lifecycleScope.launch {
            try {
                // Make API call to get nearby restaurants
                val response = placesApiService.getNearbyPlaces(
                    location, radius, name, apiKey
                )
                val places = response.results

                // Add markers for each nearby restaurants
                for (place in places) {
                    val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
                    val markerOptions = MarkerOptions()
                        .position(latLng)
                        .title(place.name)
                        .snippet(place.vicinity)
                        .infoWindowAnchor(1.0F, 2.0F)
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pizza_icon_small))

                    mMap.addMarker(markerOptions)
                    Log.d("places",place.toString())
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

//val restaurant_image = view.findViewById<ImageView>(R.id.restaurant_image)
//val restaurant_name = view.findViewById<TextView>(R.id.restaurant_name)
//val address_vicinity = view.findViewById<TextView>(R.id.address_vicinity)
//val restaurant_rating_number = view.findViewById<TextView>(R.id.restaurant_rating_number)
//val restaurant_rating_bar = view.findViewById<RatingBar>(R.id.restaurant_rating_bar)
//val restaurant_rating_count = view.findViewById<TextView>(R.id.restaurant_rating_count)
//val open_hours = view.findViewById<TextView>(R.id.open_hours)


//restaurant_name.text = place.name
//address_vicinity.text = place.vicinity
//restaurant_rating_number.text = place.rating.toString()
//restaurant_rating_bar.rating = place.rating.toFloat()
//restaurant_rating_count.text = place.user_ratings_total.toString()
//open_hours.text = if (place.opening_hours.open_now) "OPEN" else "CLOSED"

