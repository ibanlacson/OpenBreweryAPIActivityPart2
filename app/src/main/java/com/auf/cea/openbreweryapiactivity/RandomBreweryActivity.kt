package com.auf.cea.openbreweryapiactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.auf.cea.openbreweryapiactivity.adapter.BreweryAdapter
import com.auf.cea.openbreweryapiactivity.databinding.ActivityRandomBreweryScreenBinding
import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDBItem
import com.auf.cea.openbreweryapiactivity.realm.config.RealmConfig
import com.auf.cea.openbreweryapiactivity.realm.operations.BreweryOperations
import com.auf.cea.openbreweryapiactivity.services.helper.Retrofit
import com.auf.cea.openbreweryapiactivity.services.repository.OpenBreweryAPI
import io.realm.RealmConfiguration
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class RandomBreweryActivity : AppCompatActivity(), View.OnClickListener, BreweryAdapter.BreweryAdapterInterface {
    private lateinit var binding : ActivityRandomBreweryScreenBinding
    private lateinit var adapter : BreweryAdapter
    private lateinit var breweryData : ArrayList<OpenBreweryDBItem>
    private lateinit var realmConfig: RealmConfiguration
    private lateinit var getDbOperations: BreweryOperations
    private lateinit var coroutineContext: CoroutineContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomBreweryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Title
        supportActionBar?.title = "Random Breweries"

        // Initialization of Variables
        breweryData = arrayListOf()

        // Recycler View configuration
        adapter = BreweryAdapter(breweryData, this, this)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.rvBrewery.layoutManager = layoutManager
        binding.rvBrewery.adapter = adapter

        // Button Configuration
        binding.btnRandomize.setOnClickListener(this)

        // init
        realmConfig = RealmConfig.getConfiguration()
        getDbOperations = BreweryOperations(realmConfig)
        coroutineContext = Job() + Dispatchers.IO
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

    private fun showLoading(){
        binding.rvBrewery.visibility = View.GONE
        binding.animationLoading.visibility = View.VISIBLE

        object : CountDownTimer(3000,1000){
            override fun onTick(p0: Long) {
            }
            override fun onFinish() {
                binding.rvBrewery.visibility = View.VISIBLE
                binding.animationLoading.visibility = View.GONE
            }
        }.start()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            (R.id.btn_randomize) -> {
                getRandomBreweries()
                showLoading()
            }
        }
    }

    override fun removeFavorite(id: String) {
        val scope = CoroutineScope(coroutineContext + CoroutineName("RemoveDBEntry"))
        scope.launch (Dispatchers.IO) {
            getDbOperations.removeBrewery(id)
        }
    }

    override fun addToFav(id: String, name: String, type: String, location: String, website: String, phone: String, position: Int) {
        val scope = CoroutineScope(coroutineContext + CoroutineName("AddToDatabase"))
        scope.launch(Dispatchers.IO) {
            getDbOperations.insertBrewery(id,name, type, location, phone, website)
        }
    }
}