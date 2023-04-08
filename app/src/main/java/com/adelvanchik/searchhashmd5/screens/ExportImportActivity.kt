package com.adelvanchik.searchhashmd5.screens

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adelvanchik.searchhashmd5.R
import com.adelvanchik.searchhashmd5.data.HashEntity
import com.adelvanchik.searchhashmd5.databinding.ActivityExportImportBinding
import com.adelvanchik.searchhashmd5.recycleview.HashListAdapter
import java.io.*

class ExportImportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportImportBinding
    private val vm by lazy {ViewModelProvider(this)[ExportImportViewModel::class.java]}
    private lateinit var hashListAdapter: HashListAdapter

    private var fileList = mutableListOf<String>()
    private lateinit var hashList: List<HashEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportImportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickListeners()
        setupRecycleView()

        vm.hashListLiveData.observe(this) {
            binding.listHashTv.visibility = if (it.isEmpty()) View.INVISIBLE else View.VISIBLE
            hashList = it
            hashListAdapter.submitList(it)
        }
        updateFileList()
    }

    private fun clickListeners() {
        binding.exportButton.setOnClickListener {
            showExportDialog()
        }

        binding.importButton.setOnClickListener {
            showImportDialog()
        }
    }

    fun setupRecycleView() {
        hashListAdapter = HashListAdapter()
        with(binding.hashRv) {
            adapter = hashListAdapter
        }
    }

    private fun showExportDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.export_file))

        val input = View.inflate(this, R.layout.dialog_input, null)
        builder.setView(input)
        builder.setPositiveButton(resources.getString(R.string.export)) { dialog, _ ->
            val fileName = input.findViewById<EditText>(R.id.edit_text).text.toString().trim()

            if (fileName.isNotEmpty()) {
                exportToFile(fileName)
            } else {
                Toast.makeText(this, resources.getString(R.string.filename_cannot_be_empty),
                    Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.close)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun exportToFile(fileName: String) {
        try {
            val file = File(filesDir, "$fileName.txt")
            val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file)))

            hashList.forEach {
                writer.write(it.hash)
                writer.newLine()
            }
            vm.clearList()
            writer.close()

            Toast.makeText(this, "${resources.getString(
                R.string.file_saved_as)} $fileName.txt", Toast.LENGTH_SHORT).show()
            updateFileList()
        } catch (e: IOException) {
            Toast.makeText(this, resources.getString(R.string.error_writing_file),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImportDialog() {
        if (fileList.isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.no_files_available_for_import),
                Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.import_file))
        builder.setAdapter(ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
            fileList)) { _, position ->
            importFromFile(fileList[position])
        }

        builder.show()
    }

    private fun importFromFile(fileName: String) {
        try {
            val file = File(filesDir, fileName)
            val inputStream = FileInputStream(file)
            val reader = BufferedReader(InputStreamReader(inputStream))

            val list = mutableListOf<HashEntity>()

            var line = reader.readLine()
            while (line != null) {
                list.add(HashEntity(line))
                line = reader.readLine()
            }

            reader.close()
            inputStream.close()
            vm.addHashList(list)

            Toast.makeText(this, resources.getString(R.string.file_imported_successfully),
                Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, resources.getString(R.string.error_reading_file),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFileList() {
        fileList.clear()
        filesDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension == "txt") {
                fileList.add(file.name)
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ExportImportActivity::class.java)
    }
}