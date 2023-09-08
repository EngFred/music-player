package com.engineer.fred.audioplayer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.engineer.fred.audioplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme( MainActivity.currentTheme[ MainActivity.themeIndex ] )
        binding = ActivityAboutBinding.inflate( layoutInflater )
        setContentView( binding.root )
        supportActionBar?.title = "About app"

        if ( MainActivity.themeIndex == 5  ) {
            binding.root.setBackgroundColor( ContextCompat.getColor(  this, R.color.special) )
            binding.card.setBackgroundColor(  ContextCompat.getColor(  this, R.color.dark )  )
            binding.developerTV.setTextColor( ContextCompat.getColor(  this, R.color.white ))
            binding.infoTV.setTextColor( ContextCompat.getColor(  this, R.color.white ))
            binding.whatsappNoTV.setTextColor( ContextCompat.getColor(  this, R.color.white ))
            binding.emailTV.setTextColor( ContextCompat.getColor(  this, R.color.white ))
        }
    }
}