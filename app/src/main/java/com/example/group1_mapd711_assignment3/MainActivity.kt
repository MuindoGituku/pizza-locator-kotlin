/**
 * @Group 1
 * @author Muindo Gituku (301372521)
 * @author Emre Deniz (301371047)
 * @date Nov 3, 2023
 * @description: Android Assignment 3 - Pizza Locator App
 */

package com.example.group1_mapd711_assignment3

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.group1_mapd711_assignment3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // List of cities
    val cities_saved_list = arrayOf("Toronto Downtown, ON", "Scarborough, ON", "Mississauga, ON", "North York, ON", "Oakville, ON", "Markham, ON", "Brampton, ON", "Ajax, ON")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ListView of Cities
        binding.citiesListView.setOnItemClickListener { parent, view, position, id ->
            val selectedCity = cities_saved_list[position]

            // Define intent
            val intent = Intent(this,MarkersMapActivity::class.java)

            // Pass selected city to intent
            intent.putExtra("selected_city",selectedCity)

            // Get longtitude and latitude values of selected city
            val geocoder = Geocoder(this)
            val results = geocoder.getFromLocationName(selectedCity, 1)
            if (results != null) {
                if (results.isNotEmpty()) {
                    val location = results[0]

                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Pass latitude and longitude to intent
                    intent.putExtra("latitude",latitude)
                    intent.putExtra("longitude",longitude)

                    startActivity(intent)
                }
            }
        }
    }
}
