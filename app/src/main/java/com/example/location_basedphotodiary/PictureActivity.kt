package com.example.location_basedphotodiary
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.location_basedphotodiary.databinding.ActivityPictureBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class PictureActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var storage: FirebaseStorage
    lateinit var binding:ActivityPictureBinding
    lateinit var mAuth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth= FirebaseAuth.getInstance()

        storage= FirebaseStorage.getInstance()
        val namef=intent.getStringExtra("name")
        binding.textView9.text=namef
        binding.btnPic.setOnClickListener {
            openCamera()
        }
        binding.btnAllpic.setOnClickListener {
            intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
        binding.btnlogout.setOnClickListener {
            mAuth.signOut()
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun openCamera() {
        if (checkPermissionsCamera()) {
            if (isCameraPermissionEnabled()) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                } catch (e: ActivityNotFoundException) {
                    // display error state to the user
                }}
        }
        else{
            requestCameraPermission()
        }
    }
    private fun isCameraPermissionEnabled(): Boolean {
        val permission =  Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }
    private val permissionId = 123
    private fun requestCameraPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            permissionId
        )
    }
    private fun checkPermissionsCamera(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    //Now we write onActivityResult() Function to show the captured Picture and show it on Image View



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("images").child(fileName)

            // Convert the Bitmap to a byte array
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()

            // Upload the image to Firebase Storage
            val uploadTask = storageRef.putBytes(imageData)
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        Log.d("PictureActivity", "Download URL: $downloadUrl")

                        // Save the image URL in Firebase Realtime Database
                        saveImageUrlToDatabase(downloadUrl)

                        // Use the downloadUrl with Glide to load and display the image
                        Glide.with(this)
                            .load(downloadUrl)
                            .error(R.drawable.img) // Set an error image if loading fails
                            .into(binding.image)

                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                        // You can save the downloadUrl or use it to display the image later
                    }
                } else {
                    // Image upload failed
                    val exception = task.exception
                    // Handle the exception
                }
            }
        }}


    private fun saveImageUrlToDatabase(imageUrl: String) {
        // Get the current user's UID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            // Reference to the 'user_images' node in the Firebase Realtime Database
            val userImagesReference = FirebaseDatabase.getInstance().reference.child("user_images")

            // Save the image URL under the user's UID
            userImagesReference.child(uid).push().setValue(imageUrl)
        }
    }



    }