package com.app.gestordeperfumaria.activities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.gestordeperfumaria.R
import com.app.gestordeperfumaria.databinding.ActivityRulesBinding

class RulesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRulesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

    }
}