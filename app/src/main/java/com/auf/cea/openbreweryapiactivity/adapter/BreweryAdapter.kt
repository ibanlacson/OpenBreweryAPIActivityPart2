package com.auf.cea.openbreweryapiactivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.openbreweryapiactivity.databinding.ContentRecyclerViewBinding
import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDBItem

class BreweryAdapter(private var breweryList: ArrayList<OpenBreweryDBItem>, private var context: Context) : RecyclerView.Adapter<BreweryAdapter.BreweryViewHolder>() {
    inner class BreweryViewHolder(private val binding: ContentRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (itemData: OpenBreweryDBItem){

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
}