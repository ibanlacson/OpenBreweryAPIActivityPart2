package com.auf.cea.openbreweryapiactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.auf.cea.openbreweryapiactivity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            btnBreweryList.setOnClickListener(this@MainActivity)
            btnRandomBrewery.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            (R.id.btn_brewery_list) -> {
                val intent = Intent(this,BreweryScreenActivity::class.java)
                startActivity(intent)
            }
            (R.id.btn_random_brewery) -> {
                val intent = Intent(this,RandomBreweryActivity::class.java)
                startActivity(intent)
            }
        }
    }
}