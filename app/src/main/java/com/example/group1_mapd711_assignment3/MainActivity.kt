package com.example.group1_mapd711_assignment3

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.group1_mapd711_assignment3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val cities_saved_list = arrayOf("Toronto Downtown, ON", "Scarborough, ON", "Mississauga, ON", "North York, ON", "Oakville, ON", "Markham, ON", "Brampton, ON", "Ajax, ON",)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.citiesListView.setOnItemClickListener { parent, view, position, id ->
            val selectedCity = cities_saved_list[position]

            val intent = Intent(this,MarkersMapActivity::class.java)
            intent.putExtra("selected_city",selectedCity)

            val geocoder = Geocoder(this)
            val results = geocoder.getFromLocationName(selectedCity, 1)
            if (results != null) {
                if (results.isNotEmpty()) {
                    val location = results[0]

                    val latitude = location.latitude
                    intent.putExtra("latitude",latitude)

                    val longitude = location.longitude
                    intent.putExtra("longitude",longitude)

                    startActivity(intent)
                } else {

                }
            }
        }
    }
}