package com.example.registerfirebasebinding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.registerfirebasebinding.databinding.ActivityMenuBinding
import com.example.registerfirebasebinding.databinding.NavHeaderMainBinding
import com.example.registerfirebasebinding.fragments.ProfileFragment
import com.example.registerfirebasebinding.models.users
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

const val appid = "appid"

interface applistner {
    fun appselected(id: Int)
}

class MenuActivity : AppCompatActivity(), applistner {
    
    private lateinit var appBarConfiguration: AppBarConfiguration // для кнопки меню
    
    lateinit var binding: ActivityMenuBinding
    
    lateinit var toggle: ActionBarDrawerToggle
    
    //   lateinit var toolbar: MaterialToolbar
    lateinit var navController: NavController


    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBar.toolbar)
        Main = this
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val toolbar = binding.appBar.toolbar

        val drawerMenu = binding.navView2 //отображение фотки в меню
        val header = drawerMenu.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(header)
        val imageUser = headerBinding.image


        FirebaseFirestore.getInstance().collection("users")
            .document(ProfileFragment().getCurrntUserId()) //вызов функции получения айди юзенра
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(users::class.java)
                if ( user != null) {
                    val imageHeader = user!!.img
                    headerBinding.drawerEmail.text = user.email
                    GlideLoader(this).loadUserProfile(imageHeader, imageUser)
                }
            }

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.ChatFragment, R.id.ProfileNav, R.id.FirstFragment, R.id.NewsFragment),findViewById(R.id.drawer_layout)
        )
setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view2)?.setupWithNavController(navController)

//            binding.navView2.setNavigationItemSelectedListener {
//                when (it.itemId) {
//                    R.id.nav_profile -> {
//                        change(ProfileFragment())
//                    }
//                    R.id.chat -> {
//                        change(ChatFragment())
//                    }
//                }
//                true
//            }


        //


//        val navHostFragment2 =
//            supportFragmentManager.findFragmentById(R.id.fragment_list) as NavHostFragment
//        val navController2 = navHostFragment2.navController
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.FirstFragment, R.id.SecondFragment,
//            ), findViewById(R.id.drawer_layout)
//        )
//
//
//        setupActionBarWithNavController(navController, appBarConfiguration)

//        findViewById<NavigationView>(R.id.nav_view2)?.setupWithNavController(navController2)
    
    }

    fun change (fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit()

            binding.drawerLayout.closeDrawers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true

        return super.onOptionsItemSelected(item)
    }
    
    override fun appselected(id: Int) {
        val intent = Intent(this@MenuActivity, DetailsActivity::class.java)
        intent.putExtra(appid, id) // отправка айди элемента из списка
        startActivity(intent)
        
    }
}

//private fun setUpActionBar(){
//    setSupportActionBar(binding.toolbarMenu)
//
//    supportActionBar?.setDisplayHomeAsUpEnabled(true)
//    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
//    binding.toolbarMenu.setNavigationOnClickListener {
//        onBackPressed()
//    }


