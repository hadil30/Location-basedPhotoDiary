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

class LoginActivity : AppCompatActivity() {
    lateinit var email: EditText;
    lateinit var password: EditText;
    lateinit var btn: Button;
    lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        email=findViewById(R.id.email)
        password=findViewById(R.id.password)
        btn=findViewById(R.id.login)
        btn.setOnClickListener {
            Login()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            intent = Intent(this, PictureActivity::class.java)
            startActivity(intent)
        }
    }

   fun extractUsername(email: String): String {
       val atIndex = email.indexOf('@')
       return if (atIndex != -1) {
           email.substring(0, atIndex)
       } else {

           email
       }
   }
    fun Login() {
        mAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user : FirebaseUser? =mAuth.currentUser
                    Toast.makeText(this,"Authentication Success", Toast.LENGTH_SHORT).show()
                    intent = Intent(this, PictureActivity::class.java)
                    val name =extractUsername(email.text.toString())
                    intent.putExtra("name",name)
                    startActivity(intent)
                } else {
                    Log.d("","signInWithEmailAndPassword:failure",task.getException());
                    Toast.makeText(this,"Authentication Failed", Toast.LENGTH_SHORT).show()

                }
            }
    }
}