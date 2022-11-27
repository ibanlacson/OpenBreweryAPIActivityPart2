package com.auf.cea.openbreweryapiactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.auf.cea.openbreweryapiactivity.adapter.BreweryAdapter
import com.auf.cea.openbreweryapiactivity.databinding.ActivityRandomBreweryScreenBinding
import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDBItem
import com.auf.cea.openbreweryapiactivity.services.helper.Retrofit
import com.auf.cea.openbreweryapiactivity.services.repository.OpenBreweryAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class RandomBreweryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityRandomBreweryScreenBinding
    private lateinit var adapter : BreweryAdapter
    private lateinit var breweryData : ArrayList<OpenBreweryDBItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomBreweryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Title
        supportActionBar?.title = "Random Breweries"

        // Initialization of Variables
        breweryData = arrayListOf()

        // Recycler View configuration
        adapter = BreweryAdapter(breweryData, this)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.rvBrewery.layoutManager = layoutManager
        binding.rvBrewery.adapter = adapter

        // Button Configuration
        binding.btnRandomize.setOnClickListener(this)
    }

    private fun getRandomBreweries(){
        val breweryAPI = Retrofit.getInstance().create(OpenBreweryAPI::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val result = breweryAPI.getRandomBreweryData(Random.nextInt(9,18))
            val breweryDataResult = result.body()

            if (breweryDataResult != null){
                breweryData.clear()
                breweryData.addAll(breweryDataResult)

                withContext(Dispatchers.Main){
                    adapter.updateData(breweryDataResult)
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            (R.id.btn_randomize) -> {
                getRandomBreweries()
                //Show Animation
            }
        }
    }
}