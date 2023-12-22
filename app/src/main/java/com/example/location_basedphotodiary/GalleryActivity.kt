package com.example.location_basedphotodiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GalleryActivity : AppCompatActivity() {
    private val imageInfoList: ArrayList<ImageAdapter.ImageInfo> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        recyclerView = findViewById(R.id.recyclerview)
        adapter = ImageAdapter(imageInfoList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        progressBar = findViewById(R.id.progress)
        progressBar.visibility = View.VISIBLE

        // Get the current user's UID
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            // Reference to the "user_images" node in Firebase Database under the user's UID
            val userImagesRef = FirebaseDatabase.getInstance().reference
                .child("user_images")
                .child(uid)

            // Retrieve image URLs and location from the "user_images" node
            userImagesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    imageInfoList.clear()
                    for (imageSnapshot in snapshot.children) {
                        val imageUrl = imageSnapshot.child("imageUrl").getValue(String::class.java)
                        val location = imageSnapshot.child("location").getValue(String::class.java)
                        if (imageUrl != null && location != null) {
                            imageInfoList.add(ImageAdapter.ImageInfo(imageUrl, location))
                            Log.e("Itemvalue", imageUrl)
                        }
                    }

                    // Update the RecyclerView adapter with the new image list
                    recyclerView.adapter = adapter
                    progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("GalleryActivity", "Failed to read value.", error.toException())
                }
            })
        }
    }
}
