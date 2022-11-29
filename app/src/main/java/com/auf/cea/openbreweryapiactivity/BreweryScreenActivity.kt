package com.auf.cea.openbreweryapiactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.openbreweryapiactivity.adapter.PaginatedBreweryAdapter
import com.auf.cea.openbreweryapiactivity.databinding.ActivityBreweryScreenBinding
import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDBItem
import com.auf.cea.openbreweryapiactivity.services.helper.Retrofit
import com.auf.cea.openbreweryapiactivity.services.repository.OpenBreweryAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BreweryScreenActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityBreweryScreenBinding
    private lateinit var breweryData: ArrayList<OpenBreweryDBItem>
    private lateinit var adapter : PaginatedBreweryAdapter
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
        adapter = PaginatedBreweryAdapter(breweryData,this)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvPaginated.adapter = adapter
        binding.rvPaginated.layoutManager = layoutManager

        // Button Config
        binding.btnRefresh.setOnClickListener(this)

        // Spinner Configuration
        val spinnerAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, filterList)
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
    }

    private fun getBreweries(filterValue:String){
        val breweryAPI = Retrofit.getInstance().create(OpenBreweryAPI::class.java)
        Log.d(BreweryScreenActivity::class.java.simpleName,"the filter is: ${filterValue.lowercase()}")
        GlobalScope.launch(Dispatchers.IO) {
            val result = breweryAPI.getBreweryData(filterValue.lowercase(),pageCounter, 5)
            val breweryDataResult = result.body()
            if (breweryDataResult != null){
                when(isChanged) {
                    (false) -> {
                        breweryData.addAll(breweryDataResult)
                        withContext(Dispatchers.Main){
                            adapter.updateData(breweryDataResult)
                        }
                    }
                    (true) -> {

                        breweryData.clear()
                        breweryData.addAll(breweryDataResult)
                        withContext(Dispatchers.Main){
                            adapter.resetRecyclerView(breweryDataResult)
                        }

                        // Reset the flag and counter
                        isChanged = false
                        pageCounter = 1
                    }
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
            Log.d(BreweryScreenActivity::class.java.simpleName,filterValue)
            isChanged = true
        } else {
            isChanged = false
        }
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}