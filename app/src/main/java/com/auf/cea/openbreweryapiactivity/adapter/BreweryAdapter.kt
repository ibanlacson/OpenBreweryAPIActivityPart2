package com.auf.cea.openbreweryapiactivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.openbreweryapiactivity.databinding.ContentRecyclerViewBinding
import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDBItem
import com.auf.cea.openbreweryapiactivity.services.helper.HelperClass
import com.auf.cea.openbreweryapiactivity.services.helper.HelperClass.Companion.checkNull
import com.auf.cea.openbreweryapiactivity.services.helper.HelperClass.Companion.concatLocation

class BreweryAdapter(private var breweryList: ArrayList<OpenBreweryDBItem>, private var context: Context, private var breweryAdapterCallback: BreweryAdapterInterface ) : RecyclerView.Adapter<BreweryAdapter.BreweryViewHolder>() {
    interface BreweryAdapterInterface {
        fun removeFavorite(id:String)
        fun addToFav(id:String, name:String, type:String, location:String, website:String, phone:String, position:Int)
    }
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
            val phoneData = checkNull(itemData.phone)
            val websiteData = checkNull(itemData.website_url)

            with(binding){
                txtName.text = itemData.name
                txtBreweryType.text = itemData.brewery_type
                txtLocation.text = String.format("Location: %s", addressLine )
                txtPhone.text = String.format("Phone: %s", checkNull(itemData.phone))
                txtWebsite.text = String.format("Website: %s", checkNull(itemData.website_url))

                toggleFavorite.setOnClickListener {
                    val getState = binding.toggleFavorite.isChecked
                    if (getState) {
                        breweryAdapterCallback.addToFav(itemData.id,itemData.name,itemData.brewery_type,addressLine,websiteData,phoneData,adapterPosition)
                        Toast.makeText(context,"Added to Favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        breweryAdapterCallback.removeFavorite(itemData.id)
                        Toast.makeText(context,"Removed from favorites", Toast.LENGTH_SHORT).show()
                    }
                }
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