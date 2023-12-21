package com.example.location_basedphotodiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth;
    lateinit var email:EditText
    lateinit var password:EditText
    lateinit var btncreate: Button
    lateinit var btnlogin: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth= FirebaseAuth.getInstance()
        email=findViewById(R.id.email)
        password=findViewById(R.id.password)
        btncreate=findViewById(R.id.create)
        btnlogin=findViewById(R.id.login)
        btncreate.setOnClickListener{
            signUp()
        }
         btnlogin.setOnClickListener {
                intent = Intent(this ,LoginActivity::class.java)
                startActivity(intent)
            }


    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = mAuth.currentUser

        if (currentUser != null) {
            Toast.makeText(this,"Authentification failed",Toast.LENGTH_LONG).show()
        }
    }

    fun signUp() {
        mAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    Toast.makeText(this, "You created a new account", Toast.LENGTH_LONG).show()
                } else {
                    Log.d("","createUserWithEmail:failure",task.getException());

                    //Toast.makeText(this, "Authentication fail", Toast.LENGTH_LONG).show()
                }
            }
    }
}