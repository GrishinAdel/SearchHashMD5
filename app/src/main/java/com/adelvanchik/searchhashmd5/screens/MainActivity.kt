package com.adelvanchik.searchhashmd5.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adelvanchik.searchhashmd5.R
import com.adelvanchik.searchhashmd5.databinding.ActivityMainBinding
import layout.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm by lazy {ViewModelProvider(this)[MainViewModel::class.java]}
    private var newStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickListeners()
        textChangeListener()
        observers()
    }

    private fun observers() {
        vm.incorrectHashFormat.observe(this) {
            showToast(resources.getString(R.string.incorrect_format_hash))
        }

        vm.serviceNotWork.observe(this) {
            showToast(resources.getString(R.string.error_working_service))
        }

        vm.hashAdded.observe(this) {
            val str = if (it) resources.getString(R.string.ok)
            else resources.getString(R.string.hash_added)
            showToast(str)
        }

        vm.inputText.observe(this) {
            if (newStart) binding.hashInput.setText(it)
            newStart = false
        }
    }

    private fun textChangeListener() {
        binding.hashInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                vm.updateInputText(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun clickListeners() {
        binding.importExportButton.setOnClickListener {
            startActivity(ExportImportActivity.newIntent(this))
        }

        binding.searchButton.setOnClickListener {
            vm.searchHash()
        }
    }

    private fun showToast(text: String) {
        if (!newStart) {
            Toast.makeText(
                this@MainActivity,
                text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

