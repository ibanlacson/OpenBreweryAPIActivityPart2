package com.auf.cea.openbreweryapiactivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.openbreweryapiactivity.databinding.ContentRecyclerViewBinding
import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDBItem
import com.auf.cea.openbreweryapiactivity.services.helper.HelperClass

class PaginatedBreweryAdapter(private var breweryList: ArrayList<OpenBreweryDBItem>, context: Context): RecyclerView.Adapter<PaginatedBreweryAdapter.PaginatedViewHolder>() {

    inner class PaginatedViewHolder(private var binding: ContentRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (itemData:OpenBreweryDBItem){
            val addressLine = HelperClass.concatLocation(
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
                txtPhone.text = String.format("Phone: %s", HelperClass.checkNull(itemData.phone))
                txtWebsite.text = String.format("Website: %s",
                    HelperClass.checkNull(itemData.website_url)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaginatedViewHolder {
        val binding = ContentRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PaginatedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaginatedViewHolder, position: Int) {
        val breweryData = breweryList[position]
        return holder.bind(breweryData)
    }

    override fun getItemCount(): Int {
        return breweryList.size
    }

    fun updateData(breweryDataList: ArrayList<OpenBreweryDBItem>){
        this.breweryList.addAll(breweryDataList)
        this.notifyItemInserted(breweryList.size)
    }
}