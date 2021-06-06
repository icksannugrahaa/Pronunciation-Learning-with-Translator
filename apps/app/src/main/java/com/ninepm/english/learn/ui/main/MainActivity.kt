package com.ninepm.english.learn.ui.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityMainBinding
import com.ninepm.english.learn.ui.home.HomeFragment
import com.ninepm.english.learn.ui.home.HomeViewModel
import com.ninepm.english.learn.ui.home.ViewModelFactory
import com.ninepm.english.learn.utils.MyUtils.Companion.loadGIFDrawable
import com.ninepm.english.learn.utils.MyUtils.Companion.loadImage

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        filterMenu()
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeFragment())
                .commit()
            supportActionBar?.title = getString(R.string.app_name)
        }
    }

    override fun onResume() {
        super.onResume()
        filterMenu()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun filterMenu() {
        val menu = binding.navView.menu
        val header = binding.navView.getHeaderView(0)
        Log.d("firebase_menu", "isRunning")
        viewModel.getUser().observe(this, { response ->
            if (response.uid != null && response.isVerified!!) {
                header.isVisible = true
                header.findViewById<TextView>(R.id.nav_drawer_tv_email).text = response.email
                header.findViewById<TextView>(R.id.nav_drawer_tv_username).text = response.username
                if(response.profilePath != null) {
                    header.findViewById<ImageView>(R.id.nav_drawer_img_avatar).loadImage(response.profilePath)
                } else {
                    header.findViewById<ImageView>(R.id.nav_drawer_img_avatar).loadGIFDrawable(R.drawable.user_default, true)
                }
                menu.findItem(R.id.nav_drawer_profile).isVisible = true
                menu.findItem(R.id.nav_drawer_histories).isVisible = true
                menu.findItem(R.id.nav_drawer_change_password).isVisible = true

            } else {
                header.isVisible = false
                menu.findItem(R.id.nav_drawer_profile).isVisible = false
                menu.findItem(R.id.nav_drawer_histories).isVisible = false
                menu.findItem(R.id.nav_drawer_change_password).isVisible = false
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        var title = getString(R.string.app_name)
        when (item.itemId) {
            R.id.nav_drawer_home -> {
                fragment = HomeFragment()
                title = getString(R.string.app_name)
            }
//            R.id.nav_favorite -> {
//                fragment = FavoriteFragment()
//                title = getString(R.string.menu_favorite)
//            }
//            R.id.nav_map -> {
//                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
//            }
        }
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit()
        }
        supportActionBar?.title = title

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}