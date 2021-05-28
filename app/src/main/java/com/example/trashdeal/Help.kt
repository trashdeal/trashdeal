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
import com.example.trashdeal.databinding.ActivityMainBinding

class Help : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Help"
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
        val txtcol1 = findViewById<TextView>(R.id.txtcol1)
        val txtcol2 = findViewById<TextView>(R.id.txtcol2)
        val txtcol3 = findViewById<TextView>(R.id.txtcol3)
        val txtcol4 = findViewById<TextView>(R.id.txtcol4)
        val txtcol6 = findViewById<TextView>(R.id.txtcol6)
        val color: Int = getResources().getColor(R.color.colorPrimaryDark)
        val text1 = "Welcome to TrashDeal, a unique system developed for the management of waste systematically. We're dedicated to providing you the very best of services in an easy and convenient manner, with an emphasis on technical comfort, privacy of data and the best user experience."
        val text2 = "We the support staff of TrashDeal are here to guide you and answer your questions."
        val text3 = "We hope you enjoy our services as much as we enjoy offering them to you. If you have any questions or comments, please don't hesitate to contact us."
        val text4 = "You can email us your enquiries here at:"
        val text6 = "Or give us a call on:"
        val s1 = SpannableString(text1)
        val s2 = SpannableString(text2)
        val s3 = SpannableString(text3)
        val s4 = SpannableString(text4)
        val s6 = SpannableString(text6)
        val ssgreen = ForegroundColorSpan(color)
        s1.setSpan(ssgreen, 204, 269, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        s2.setSpan(ssgreen, 7, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        s3.setSpan(ssgreen, 137, 148, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        s4.setSpan(ssgreen, 8, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        s6.setSpan(ssgreen, 13, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtcol1.text = s1
        txtcol2.text = s2
        txtcol3.text = s3
        txtcol4.text = s4
        txtcol6.text = s6

        val gmailLink: TextView = findViewById<View>(R.id.txtcol5) as TextView
        gmailLink.setMovementMethod(LinkMovementMethod.getInstance())

        val phoneLink: TextView = findViewById<View>(R.id.txtcol7) as TextView
        phoneLink.setMovementMethod(LinkMovementMethod.getInstance())

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
