package com.example.location_basedphotodiary

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

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

        // Set an OnClickListener for the delete icon
        holder.deleteIcon.setOnClickListener {
            // Call a method to handle the deletion
            showDeleteConfirmationDialog(position)
        }
    }
    private fun showDeleteConfirmationDialog(position: Int) {
        val imageInfo = imageList[position]

        AlertDialog.Builder(context)
            .setTitle("Delete Image")
            .setMessage("Are you sure you want to delete this image?")
            .setPositiveButton("Delete") { _, _ ->
                // Call a method to delete the image from Firebase and the list
                deleteImage(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.item)
        var locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        var deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)


    }

    private fun deleteImage(position: Int) {
        val imageInfo = imageList[position]
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        // Ensure UID is not null and imageInfo has a valid URL
        if (uid != null && imageInfo.imageUrl.isNotBlank()) {
            val userImagesRef = FirebaseDatabase.getInstance().reference
                .child("user_images")
                .child(uid)

            // Find the image node with the corresponding URL and remove it
            userImagesRef.orderByChild("imageUrl").equalTo(imageInfo.imageUrl)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // The snapshot exists, iterate through the children
                            for (imageSnapshot in snapshot.children) {
                                val imageRef = imageSnapshot.ref
                                val imageUrl = imageInfo.imageUrl

                                // Delete the image from Cloud Storage
                                deleteImageFromStorage(imageUrl)

                                // Remove the image from the Realtime Database
                                imageRef.removeValue()

                                // Remove the image from the list
                                imageList.removeAt(position)

                                // Notify the adapter about the removal
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, itemCount)

                                // Show a toast for confirmation
                                Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
                                Log.d("ImageAdapter", "Image removed from Firebase")
                            }
                        } else {
                            Log.d("ImageAdapter", "Image not found in Firebase")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                        Log.e("ImageAdapter", "Failed to delete image from Firebase", error.toException())
                    }
                })
        }
    }

    private fun deleteImageFromStorage(imageUrl: String) {
        // Get a reference to the Firebase Storage instance
        val storage = FirebaseStorage.getInstance()

        // Create a reference to the image file
       // val storageRef = storage.reference.child("images/imageUrl")
        val storageRef = storage.getReferenceFromUrl(imageUrl)


        // Delete the file
        storageRef.delete()
            .addOnSuccessListener {
                Log.d("ImageAdapter", "Image deleted from Storage")
            }
            .addOnFailureListener {
                // Handle error
                Log.e("ImageAdapter", "Failed to delete image from Storage", it)
            }
    }








}
