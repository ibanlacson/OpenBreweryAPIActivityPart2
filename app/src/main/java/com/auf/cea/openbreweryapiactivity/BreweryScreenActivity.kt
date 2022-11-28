package com.auf.cea.openbreweryapiactivity

import android.app.ActivityManager.RecentTaskInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
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

class BreweryScreenActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityBreweryScreenBinding
    private lateinit var breweryData: ArrayList<OpenBreweryDBItem>
    private lateinit var adapter : PaginatedBreweryAdapter
    private var pageCounter = 0
    private var isLoading: Boolean = false
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
        binding.rbtnBrewpub.setOnClickListener(this)
        binding.rbtnMicro.setOnClickListener(this)
        binding.rbtnNano.setOnClickListener(this)
        binding.rbtnRegional.setOnClickListener(this)

        // OnScroll Listener
        binding.rvPaginated.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && (newState == RecyclerView.SCROLL_STATE_IDLE)) {
                    isLoading = true

                }
            }
        })
    }

    private fun getBreweries(filterValue:String){
        val breweryAPI = Retrofit.getInstance().create(OpenBreweryAPI::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            val result = breweryAPI.getBreweryData(filterValue,pageCounter, 5)
            val breweryDataResult = result.body()

            if (breweryDataResult != null){
                breweryData.addAll(breweryDataResult)

                withContext(Dispatchers.Main){
                    adapter.updateData(breweryDataResult)
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
            }
        }.start()
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){

            (R.id.rbtn_nano) -> {
                with(binding){
                    rbtnNano.isChecked = true
                    rbtnRegional.isChecked = false
                    rbtnBrewpub.isChecked = false
                    rbtnMicro.isChecked = false
                }
                filterValue = "nano"
            }
            (R.id.rbtn_regional) -> {
                with(binding){
                    rbtnRegional.isChecked = true
                    rbtnNano.isChecked = false
                    rbtnBrewpub.isChecked = false
                    rbtnMicro.isChecked = false
                }
                filterValue = "regional"
            }
            (R.id.rbtn_brewpub) -> {
                with(binding){
                    rbtnBrewpub.isChecked = true
                    rbtnNano.isChecked = false
                    rbtnRegional.isChecked = false
                    rbtnMicro.isChecked = false
                }
                filterValue = "brewpub"
            }
            (R.id.rbtn_micro) -> {
                with(binding){
                    rbtnMicro.isChecked = true
                    rbtnNano.isChecked = false
                    rbtnRegional.isChecked = false
                    rbtnBrewpub.isChecked = false
                }
                filterValue = "micro"
            }
            (R.id.btn_refresh) -> {
                getBreweries(filterValue)
                //Show animation
            }
        }
    }
}