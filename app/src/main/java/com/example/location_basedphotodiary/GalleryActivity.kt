package com.example.location_basedphotodiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.location_basedphotodiary.databinding.ActivityGalleryBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class GalleryActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    //private lateinit var binding: ActivityGalleryBinding
    private val imagelist: ArrayList<String> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        //binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_gallery)
        storage = FirebaseStorage.getInstance()

        recyclerView = findViewById(R.id.recyclerview)
        adapter = ImageAdapter(imagelist, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        progressBar = findViewById(R.id.progress)
        progressBar.visibility = View.VISIBLE
        val listRef: StorageReference = FirebaseStorage.getInstance().reference.child("images")
        listRef.listAll().addOnSuccessListener { listResult ->
            for (file: StorageReference in listResult.items) {
                file.downloadUrl.addOnSuccessListener { uri ->
                    imagelist.add(uri.toString())
                    Log.e("Itemvalue", uri.toString())
                }.addOnCompleteListener {
                    recyclerView.adapter = adapter
                    progressBar.visibility = View.GONE
                }
            }
        }

    }


    /*private fun loadAndDisplayImages() {
        // Reference to the "images" folder in Firebase Storage
        val storageRef = storage.reference.child("images")

        // List the items in the "images" folder
        storageRef.listAll().addOnSuccessListener { result ->
            for (imageRef in result.items) {
                // Get the download URL for each image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    // Use Glide to load and display the image

                    Glide.with(this)
                        .load(downloadUrl)
                        .error(R.drawable.img) // Set an error image if loading fails
                        .into(binding.imageContainer)
                }
            }
        }.addOnFailureListener { exception ->
            // Handle failures while retrieving the list of images
            Log.e("PictureActivity", "Error loading images: ${exception.message}", exception)
        }
    }*/
}