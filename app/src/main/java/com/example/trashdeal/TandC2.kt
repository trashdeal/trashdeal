package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityHelpBinding
import com.example.trashdeal.databinding.ActivityTandC2Binding

class TandC2 : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityTandC2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTandC2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Terms and Conditions"
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView
            .setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.miItem1 -> startActivity(Intent(this, MainActivity::class.java));
                    R.id.miItem2 -> startActivity(Intent(this, MyProfile::class.java));
                    R.id.miItem4 -> startActivity(Intent(this, PointsDetails::class.java));
                    R.id.miItem6 -> startActivity(Intent(this, TandC2::class.java));
                    R.id.miItem3 -> startActivity(Intent(this, Help::class.java));
                    R.id.miItem8 -> startActivity(Intent(this, MoreInformation::class.java));
                    R.id.miItem7 -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Logout")
                        builder.setIcon(R.drawable.logout_icon)
                        builder.setMessage("Are you sure you want to Logout?")
                        builder.setPositiveButton("YES") { dialog, which ->
                            startActivity(Intent(this, MobnoRegister::class.java))
                        }
                        builder.setNegativeButton(
                            "NO"
                        ) { dialog, which -> dialog.dismiss() }
                        builder.show()
                    }
                }
                true
            }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
