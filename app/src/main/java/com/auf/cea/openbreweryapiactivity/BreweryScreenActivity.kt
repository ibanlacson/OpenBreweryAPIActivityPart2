package com.auf.cea.openbreweryapiactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.openbreweryapiactivity.adapter.PaginatedBreweryAdapter
import com.auf.cea.openbreweryapiactivity.databinding.ActivityBreweryScreenBinding
import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDBItem
import com.auf.cea.openbreweryapiactivity.realm.config.RealmConfig
import com.auf.cea.openbreweryapiactivity.realm.operations.BreweryOperations
import com.auf.cea.openbreweryapiactivity.services.helper.Retrofit
import com.auf.cea.openbreweryapiactivity.services.repository.OpenBreweryAPI
import io.realm.RealmConfiguration
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BreweryScreenActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener, PaginatedBreweryAdapter.PaginatedBreweryAdapterInterface {
    private lateinit var binding: ActivityBreweryScreenBinding
    private lateinit var breweryData: ArrayList<OpenBreweryDBItem>
    private lateinit var adapter : PaginatedBreweryAdapter
    private lateinit var realmConfig: RealmConfiguration
    private lateinit var getDbOperations: BreweryOperations
    private lateinit var coroutineContext: CoroutineContext
    private var filterList = arrayOf("Nano","Micro","Brewpub","Regional")
    private var pageCounter = 0
    private var isLoading: Boolean = false
    private var isChanged: Boolean = false
    private var filterValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreweryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Arraylist
        breweryData = arrayListOf()

        // Recycler View configuration
        adapter = PaginatedBreweryAdapter(breweryData,this,this)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvPaginated.adapter = adapter
        binding.rvPaginated.layoutManager = layoutManager

        // Button Config
        binding.btnRefresh.setOnClickListener(this)

        // Spinner Configuration
        val spinnerAdapter = ArrayAdapter(this,R.layout.spinner_content, filterList)
        binding.spinnerFilter.adapter = spinnerAdapter
        binding.spinnerFilter.onItemSelectedListener = this


        // OnScroll Listener
        binding.llLoading.visibility = View.GONE
        binding.rvPaginated.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && (newState == RecyclerView.SCROLL_STATE_IDLE)) {
                    // Reset the RecyclerView if the filter was changed
                    if(!isLoading){
                        isLoading = true
                        whileLoading()
                        binding.llLoading.visibility = View.VISIBLE
                    }
                }
            }
        })

        // init
        realmConfig = RealmConfig.getConfiguration()
        getDbOperations = BreweryOperations(realmConfig)
        coroutineContext = Job() + Dispatchers.IO
    }

    private fun getBreweries(filterValue:String){
        val breweryAPI = Retrofit.getInstance().create(OpenBreweryAPI::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            val result = breweryAPI.getBreweryData(filterValue.lowercase(),pageCounter, 5)
            val breweryDataResult = result.body()
            if (breweryDataResult != null){
                breweryData.addAll(breweryDataResult)
                withContext(Dispatchers.Main){
                    adapter.updateData(breweryDataResult)
                }
                if (isChanged){
                    isChanged = false
                    pageCounter = 1
                }
            }
        }
    }

    private fun whileLoading(){
        object : CountDownTimer(2000,1000){
            override fun onTick(p0: Long) {
            }
            override fun onFinish() {
                isLoading = false
                pageCounter++
                getBreweries(filterValue)
                binding.llLoading.visibility = View.GONE
            }
        }.start()
    }
    
    private fun showLoading(){
        binding.animationLoading.visibility = View.VISIBLE
        binding.animationLoading.playAnimation()
        binding.rvPaginated.visibility = View.GONE
        object : CountDownTimer(3000,1000) {
            override fun onTick(p0: Long) {
            }
            override fun onFinish() {
                binding.animationLoading.visibility = View.GONE
                binding.rvPaginated.visibility = View.VISIBLE
                binding.animationLoading.cancelAnimation()
            }
        }.start()
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            (R.id.btn_refresh) -> {
                getBreweries(filterValue)
                showLoading()
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // Check whether to reset the recyclerview or not
        if (filterValue != filterList[p2]){
            filterValue = filterList[p2]
            isChanged = true

            // Reset View
            GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main){
                    breweryData.clear()
                    adapter.resetView()
                }
            }
        } else {
            isChanged = false
        }
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {
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

//            Hindi ito need -> causing dispatcher error (overload?)
//            withContext(Dispatchers.Main){
//                adapter.updateView(position) // No need to update the view
//            }
        }
    }

}