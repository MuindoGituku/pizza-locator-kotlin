package com.example.group1_mapd711_assignment3


import android.os.Bundle
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
data class Place(val name: String, val vicinity: String, val geometry: Geometry)
data class Geometry(val location: Location)
data class Location(val lat: Double, val lng: Double)

// Retrofit service interface
interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ): PlaceResult
}

class MarkersMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMarkersMapBinding

    private var townLatitude = 0.0
    private var townLongitude = 0.0
    private var userSelectedCity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkersMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        userSelectedCity = "Restaurants in "+intent.getStringExtra("selected_city")
        townLatitude = intent.getDoubleExtra("latitude", 0.00)
        townLongitude = intent.getDoubleExtra("longitude", 0.00)

        binding.selectedCityText.text = userSelectedCity

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override  fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker to selected town and move the camera
        val selectedTownPin = LatLng(townLatitude, townLongitude)
        mMap.addMarker(MarkerOptions().position(selectedTownPin).title(userSelectedCity))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedTownPin, 15f))

        fetchNearbyRestaurants()
    }

    private  fun fetchNearbyRestaurants(){
        // Coordinates of the location (e.g., latitude: 40.7128, longitude: -74.0060 for New York)
        val latitude = townLatitude
        val longitude = townLongitude
        val location = "$latitude,$longitude"

        // Radius in meters (1 kilometer)
        val radius = 1000

        // Type of place you want to filter (e.g., "restaurant", "cafe")
        val type = "restaurant"

        // Your Google Places API key
        val apiKey = "AIzaSyDBQOf0tvMNiecFD8A5UQ-0JmHW8BL6JAA"

        // Retrofit setup
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placesApiService = retrofit.create(PlacesApiService::class.java)

        lifecycleScope.launch {
            try {
                // Make API call to get nearby places
                val response = placesApiService.getNearbyPlaces(
                    location, radius, type, apiKey
                )
                val places = response.results

                // Add markers for each nearby restaurant
                for (place in places) {
                    val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
                    val markerOptions = MarkerOptions()
                        .position(latLng)
                        .title(place.name)
                        .snippet(place.vicinity)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))// Additional information, if desired
                    mMap.addMarker(markerOptions)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error
            }
        }

    }
}

