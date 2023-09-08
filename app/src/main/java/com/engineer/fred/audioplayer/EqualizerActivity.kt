package com.engineer.fred.audioplayer

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bullhead.equalizer.EqualizerFragment
import com.bullhead.equalizer.Settings
import com.engineer.fred.audioplayer.databinding.ActivityEqualizerBinding

class EqualizerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEqualizerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme( R.style.EqActivity )
        binding = ActivityEqualizerBinding.inflate( layoutInflater )
        setContentView( binding.root )

        val audioSession = intent.getIntExtra("audioSession", 0 )
        Settings.isEditing = false
        val equalizerFragment = EqualizerFragment.newBuilder()
            .setAccentColor(Color.parseColor("#4caf50"))
            .setAudioSessionId( audioSession )
            .build()

        supportFragmentManager.beginTransaction()
            .replace(R.id.eqFrame , equalizerFragment)
            .commit()
    }

}