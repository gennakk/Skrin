package com.rod.skrin


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.alejandrolora.mylibrary.ToolbarActivity
import com.google.firebase.auth.FirebaseAuth
import com.rod.skrin.activities.LoginActivity


import com.rod.skrin.adapters.PagerAdapter
import com.rod.skrin.extensions.gotoActivity
import com.rod.skrin.fragments.ChatFragment
import com.rod.skrin.fragments.InfoFragment
import com.rod.skrin.fragments.RatesFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : ToolbarActivity() {


    private var prevBottonSelected : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbarToLoad(toolbarView as Toolbar)

        setUpViewPager(getPagerAdapter())
        setUpBottomNavigationBar()
    }

    private fun getPagerAdapter():PagerAdapter{
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(InfoFragment())
        adapter.addFragment(RatesFragment())
        adapter.addFragment(ChatFragment())

        return adapter
    }

    private fun setUpViewPager(adapter: PagerAdapter){
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = adapter.count

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {


            }

            override fun onPageSelected(position: Int) {

                if(prevBottonSelected == null){
                    bottomNavigation.menu.getItem(0).isChecked = false
                }else{
                    prevBottonSelected!!.isChecked = false
                }

                bottomNavigation.menu.getItem(position).isChecked = true

                prevBottonSelected = bottomNavigation.menu.getItem(position)

            }

        })
    }

    private fun setUpBottomNavigationBar(){

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_nav_info ->{
                    viewPager.currentItem = 0
                    true
                }
                R.id.bottom_nav_rates -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.bottom_nav_chat -> {
                    viewPager.currentItem = 2
                    true
                }
                else -> false

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.general_options_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_log_out -> {
                FirebaseAuth.getInstance().signOut()

                gotoActivity<LoginActivity>{
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
