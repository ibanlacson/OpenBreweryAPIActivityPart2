package com.auf.cea.openbreweryapiactivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.openbreweryapiactivity.databinding.ContentRecyclerViewBinding
import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDBItem
import com.auf.cea.openbreweryapiactivity.services.helper.HelperClass.Companion.checkNull
import com.auf.cea.openbreweryapiactivity.services.helper.HelperClass.Companion.concatLocation

class BreweryAdapter(private var breweryList: ArrayList<OpenBreweryDBItem>, private var context: Context) : RecyclerView.Adapter<BreweryAdapter.BreweryViewHolder>() {
    inner class BreweryViewHolder(private val binding: ContentRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (itemData: OpenBreweryDBItem){
            val addressLine = concatLocation(
                itemData.street,
                itemData.address_2,
                itemData.address_3,
                itemData.city,
                itemData.state,
                itemData.county_province,
                itemData.country
            )
            with(binding){
                txtName.text = itemData.name
                txtBreweryType.text = itemData.brewery_type
                txtLocation.text = String.format("Location: %s", addressLine )
                txtPhone.text = String.format("Phone: %s", checkNull(itemData.phone))
                txtWebsite.text = String.format("Website: %s", checkNull(itemData.website_url))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreweryViewHolder {
        val binding = ContentRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BreweryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BreweryViewHolder, position: Int) {
        val breweryData = breweryList[position]
        holder.bind(breweryData)
    }

    override fun getItemCount(): Int {
        return breweryList.size
    }

     fun updateData(breweryDataList: ArrayList<OpenBreweryDBItem>){
        this.breweryList = arrayListOf()
         notifyDataSetChanged()
         this.breweryList = breweryDataList
         this.notifyItemInserted(this.breweryList.size)
    }
}