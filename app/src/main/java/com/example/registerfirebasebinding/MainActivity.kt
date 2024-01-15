package com.example.registerfirebasebinding

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.registerfirebasebinding.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        @Suppress("DEPRECATION") // чтобы не было уведомления зачеркнутости на коде
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { //для того чтобы скрыть полосу статуса (новая версия)
//            val windowInsetsController =
//                ViewCompat.getWindowInsetsController(window.decorView) ?: return
//            // Configure the behavior of the hidden system bars
//            windowInsetsController.systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            // Hide both the status bar and the navigation bar
//            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
//        }else {
//            window.setFlags( //старая версия
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        } // для того чтобы убрать полосу сверху

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)

            val email: String = binding.mail.text.toString().trim()
            val password: String = binding.password.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {


                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val toast = Toast.makeText(
                                this@MainActivity,
                                "успешно",
                                Toast.LENGTH_LONG
                            ) // toast - для вывода сообщения, можно писать через snackbar, но для него нужна отдельная функция
                            if (toast.view != null) {
                                toast.view!!.backgroundTintList =
                                    ColorStateList.valueOf(Color.rgb(0, 206, 209)) //цвет
                                toast.show() // чтоб показывалось
                            }
                            startActivity(Intent(this@MainActivity, MainActivity2::class.java))
                        } else {
                            Toast.makeText( // когда допусти введен плохой пароль
                                this@MainActivity,
                                "neуспешно" + task.exception!!.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }.addOnFailureListener { //когда нет доступа к базе данных
                        Toast.makeText(
                            this@MainActivity,
                            "neуспешно" + it!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
        binding.vhod.setOnClickListener {  // переход на регистрацию
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }

    }

}