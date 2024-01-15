package com.example.registerfirebasebinding

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.ui.AppBarConfiguration
import com.example.registerfirebasebinding.databinding.ActivityMain2Binding
import com.example.registerfirebasebinding.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val email: String = binding.mail.text.toString().trim()
            val password: String = binding.password.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val toast = Toast.makeText(
                                this,
                                "успешно",
                                Toast.LENGTH_LONG
                            )
                            if (toast.view != null) {
                                toast.view!!.backgroundTintList =
                                    ColorStateList.valueOf(Color.rgb(0, 206, 209)) //цвет
                                toast.show() // чтоб показывалось
                            }
                            val intent = Intent(this@MainActivity2, MenuActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@MainActivity2,
                                "neуспешно" + task.exception!!.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@MainActivity2,
                            "neуспешно" + it!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
        binding.registr.setOnClickListener {  // переход
            val intent = Intent(this@MainActivity2, MainActivity::class.java)
            startActivity(intent)
        }
    }
}