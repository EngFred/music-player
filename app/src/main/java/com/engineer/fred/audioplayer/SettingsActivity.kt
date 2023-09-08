package com.engineer.fred.audioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.engineer.fred.audioplayer.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class SettingsActivity : AppCompatActivity() {

    private lateinit var  binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme( MainActivity.currentTheme[ MainActivity.themeIndex ] )
        binding = ActivitySettingsBinding.inflate(  layoutInflater )
        setContentView(  binding.root   )

        when( MainActivity.themeIndex ) {
            0 -> {
                binding.root.setBackgroundColor( ContextCompat.getColor( this, R.color.copperfield ) )
                binding.chooseColorTv.setTextColor( ContextCompat.getColor(  this, R.color.white) )
            }
            1 ->  {
                binding.root.setBackgroundColor( ContextCompat.getColor( this, R.color.green) )
                binding.chooseColorTv.setTextColor( ContextCompat.getColor(  this, R.color.white) )
            }
            2 -> {
                binding.root.setBackgroundColor( ContextCompat.getColor(  this, R.color.tamarind )  )
                binding.chooseColorTv.setTextColor( ContextCompat.getColor(  this, R.color.white) )
            }
            3-> {
                binding.root.setBackgroundColor( ContextCompat.getColor(  this, R.color.orange )  )
                binding.chooseColorTv.setTextColor( ContextCompat.getColor(  this, R.color.white) )
            }
            4 -> {
                binding.root.setBackgroundColor( ContextCompat.getColor(  this, R.color.pink )  )
                binding.chooseColorTv.setTextColor( ContextCompat.getColor(  this, R.color.white) )
            }
            5 -> {
                binding.root.setBackgroundColor( ContextCompat.getColor(  this, R.color.dark)  )
                binding.chooseColorTv.setTextColor( ContextCompat.getColor(  this, R.color.white) )
            }
        }

        binding.defaultTheme.setOnClickListener {  saveTheme( 0 ) }
        binding.greenTheme.setOnClickListener {  saveTheme( 1 ) }
        binding.darkTheme.setOnClickListener {  saveTheme( 2  ) }
        binding.orangeTheme.setOnClickListener {  saveTheme( 3  ) }
        binding.pinkTheme.setOnClickListener {  saveTheme( 4  ) }
        binding.dark2Theme.setOnClickListener { saveTheme( 5 ) }

    }

    private fun saveTheme(  index: Int ) {
        if ( MainActivity.themeIndex != index ) {
            getSharedPreferences("THEMES", MODE_PRIVATE).edit()
                .putInt("themeIndex", index )
                .apply()
            MaterialAlertDialogBuilder( this )
                .setTitle( "Apply theme!" )
                .setMessage("Restart app to apply theme?")
                .setPositiveButton("YES"){_,_ ->
                     exitProcess( 1 )
                }
                .setNegativeButton("NO"){ d,_ -> d.dismiss() }
                .show()
        }
    }

}