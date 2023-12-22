package com.example.location_basedphotodiary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(private val imageList: ArrayList<ImageInfo>, private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    data class ImageInfo(val imageUrl: String, val location: String)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageInfo = imageList[position]

        // Loading the image
        Glide.with(holder.itemView.context).load(imageInfo.imageUrl).into(holder.imageView)

        // Displaying the location
        holder.locationTextView.text = imageInfo.location
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.item)
        var locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
    }
}
