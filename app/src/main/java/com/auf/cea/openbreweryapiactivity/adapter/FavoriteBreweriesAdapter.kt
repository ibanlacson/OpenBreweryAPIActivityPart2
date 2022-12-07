package com.auf.cea.openbreweryapiactivity.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.openbreweryapiactivity.databinding.ContentRecyclerViewBinding
import com.auf.cea.openbreweryapiactivity.models.BreweryDBItems
import com.auf.cea.openbreweryapiactivity.realm.config.RealmConfig
import com.auf.cea.openbreweryapiactivity.realm.operations.BreweryOperations
import kotlinx.coroutines.*

class FavoriteBreweriesAdapter(private var favoriteList:ArrayList<BreweryDBItems>, private var context: Context, private var favoriteAdapterCallback: FavoriteBreweriesAdapterInterface): RecyclerView.Adapter<FavoriteBreweriesAdapter.FavoritesViewHolder>() {
    interface FavoriteBreweriesAdapterInterface {
        fun removeFavorite(id:String)
    }

    inner class FavoritesViewHolder(private val binding: ContentRecyclerViewBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(itemData: BreweryDBItems){

            with(binding){
                txtName.text = itemData.name
                txtBreweryType.text = itemData.type
                txtLocation.text = String.format("Location: %s", itemData.location )
                txtPhone.text = String.format("Phone: %s", itemData.phone )
                txtWebsite.text = String.format("Website: %s", itemData.website)
                toggleFavorite.isChecked = true


                toggleFavorite.setOnClickListener {
                    val getState = binding.toggleFavorite.isChecked
                    if (getState) {
//                        favoriteAdapterCallback.addToFav(itemData.id,itemData.name,itemData.type,itemData.location,itemData.website,itemData.phone,adapterPosition)
//                        Toast.makeText(context,"Added to Favorites",Toast.LENGTH_SHORT).show()
                    } else {
                        favoriteAdapterCallback.removeFavorite(itemData.id)
                        Toast.makeText(context,"Removed from favorites",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = ContentRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val favoritesData = favoriteList[position]
        return holder.bind(favoritesData)
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    fun updateBreweryList(favoriteList: ArrayList<BreweryDBItems>){
        this.favoriteList = arrayListOf()
        notifyDataSetChanged()
        this.favoriteList.addAll(favoriteList)
        this.notifyItemInserted(this.favoriteList.size)
    }
}