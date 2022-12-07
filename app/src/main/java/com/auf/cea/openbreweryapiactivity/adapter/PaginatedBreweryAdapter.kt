package com.auf.cea.openbreweryapiactivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.openbreweryapiactivity.databinding.ContentRecyclerViewBinding
import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDBItem
import com.auf.cea.openbreweryapiactivity.realm.config.RealmConfig
import com.auf.cea.openbreweryapiactivity.realm.operations.BreweryOperations
import com.auf.cea.openbreweryapiactivity.services.helper.HelperClass
import com.auf.cea.openbreweryapiactivity.services.helper.HelperClass.Companion.checkNull
import com.auf.cea.openbreweryapiactivity.services.helper.HelperClass.Companion.concatLocation
import kotlinx.coroutines.*

class PaginatedBreweryAdapter(private var breweryList: ArrayList<OpenBreweryDBItem>, private var context: Context, private var breweryAdapterCallback: PaginatedBreweryAdapterInterface): RecyclerView.Adapter<PaginatedBreweryAdapter.PaginatedViewHolder>() {
    interface PaginatedBreweryAdapterInterface {
        fun removeFavorite(id:String)
        fun addToFav(id:String, name:String, type:String, location:String, website:String, phone:String, position:Int)
    }
    inner class PaginatedViewHolder(private var binding: ContentRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (itemData:OpenBreweryDBItem){
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
                txtPhone.text = String.format("Phone: %s", phoneData )
                txtWebsite.text = String.format("Website: %s", websiteData)

//                Triggers the insert function during the fetch function below
//                toggleFavorite.setOnCheckedChangeListener { compoundButton, isChecked ->
//                    if (isChecked) {
//                        breweryAdapterCallback.addToFav(itemData.id,itemData.name,itemData.brewery_type,addressLine,websiteData,phoneData,adapterPosition)
//                    } else {
//                        breweryAdapterCallback.removeFavorite(itemData.id)
//                    }
//                }

                toggleFavorite.setOnClickListener {
                    val getState = binding.toggleFavorite.isChecked
                    if (getState) {
                        breweryAdapterCallback.addToFav(itemData.id,itemData.name,itemData.brewery_type,addressLine,websiteData,phoneData,adapterPosition)
                        Toast.makeText(context,"Added to Favorites",Toast.LENGTH_SHORT).show()
                    } else {
                        breweryAdapterCallback.removeFavorite(itemData.id)
                        Toast.makeText(context,"Removed from favorites",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Check if favorite
            val realmConfig = RealmConfig.getConfiguration()
            val getDbOperations = BreweryOperations(realmConfig)

            val coroutineContext = Job() + Dispatchers.IO
            val scope = CoroutineScope(coroutineContext + CoroutineName("SearchIdFromDB"))
            scope.launch (Dispatchers.IO){
                val result = getDbOperations.filterBrewery(itemData.id)
                if (result.size != 0) {
                    binding.toggleFavorite.isChecked = true
                    scope.coroutineContext.cancel()  // Nag-ooverwork, need to be cancelled
                }
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

//    Hindi na ata need
//    fun updateView(position: Int) {
//        notifyItemChanged(position)
//        notifyDataSetChanged()
//    }

    fun resetView(){
        this.breweryList.clear()
        this.breweryList = arrayListOf()
        this.notifyDataSetChanged()
    }
}