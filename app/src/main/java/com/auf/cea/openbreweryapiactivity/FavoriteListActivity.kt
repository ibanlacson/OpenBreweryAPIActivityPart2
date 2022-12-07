package com.auf.cea.openbreweryapiactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.openbreweryapiactivity.adapter.FavoriteBreweriesAdapter
import com.auf.cea.openbreweryapiactivity.databinding.ActivityFavoriteListBinding
import com.auf.cea.openbreweryapiactivity.models.BreweryDBItems
import com.auf.cea.openbreweryapiactivity.realm.config.RealmConfig
import com.auf.cea.openbreweryapiactivity.realm.operations.BreweryOperations
import io.realm.RealmConfiguration
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class FavoriteListActivity : AppCompatActivity(), FavoriteBreweriesAdapter.FavoriteBreweriesAdapterInterface {
    private lateinit var binding : ActivityFavoriteListBinding
    private lateinit var breweryData: ArrayList<BreweryDBItems>
    private lateinit var adapter : FavoriteBreweriesAdapter
    private lateinit var realmConfig: RealmConfiguration
    private lateinit var getDbOperations: BreweryOperations
    private lateinit var coroutineContext: CoroutineContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Arraylist
        breweryData = arrayListOf()

        // Recycler View configuration
        adapter = FavoriteBreweriesAdapter(breweryData,this, this)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.layoutManager = layoutManager

        // init
        realmConfig = RealmConfig.getConfiguration()
        getDbOperations = BreweryOperations(realmConfig)
        coroutineContext = Job() + Dispatchers.IO

    }

    override fun onResume() {
        super.onResume()
        getFavoriteList()
    }

    private fun getFavoriteList(){
        val scope = CoroutineScope(coroutineContext + CoroutineName("FetchDataFromDB"))
        scope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                val result = getDbOperations.retrieveBrewery()
                withContext(Dispatchers.Main){
                    adapter.updateBreweryList(result)
                }
            }
        }
    }

    override fun removeFavorite(id: String) {
        val scope = CoroutineScope(coroutineContext + CoroutineName("RemoveDBEntry"))
        scope.launch (Dispatchers.IO) {
            getDbOperations.removeBrewery(id)
            withContext(Dispatchers.Main){
                getFavoriteList()
            }
        }
    }
}