package com.example.sqlitedatamigrator

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity(), ContactAdapter.OnItemClickListener {

    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var adapter: ContactAdapter
    private var contactList = ArrayList<ContactModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var btnImport: Button
    private lateinit var btnExport: Button

    // Export launcher
    private val createDocLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/comma-separated-values")) { uri ->
            uri?.let { exportToCSV(it) }
        }

    // Import launcher
    private val openDocLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { importFromCSV(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = MyDatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        fabAdd = findViewById(R.id.fabAdd)
        btnImport = findViewById(R.id.btnImport)
        btnExport = findViewById(R.id.btnExport)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(this, contactList, this)
        recyclerView.adapter = adapter

        refreshData()

        fabAdd.setOnClickListener {
            showBottomSheet(false, null)
        }

        btnImport.setOnClickListener {
            openDocLauncher.launch("text/*")
        }

        btnExport.setOnClickListener {
            createDocLauncher.launch("contacts_backup.csv")
        }
    }

    private fun refreshData() {
        contactList = dbHelper.getAllContacts()

        if (contactList.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
            tvEmptyState.text = "No data found!\nAdd or import new data."
        } else {
            recyclerView.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
            adapter.updateList(contactList)
        }
    }

    private fun showBottomSheet(isEdit: Boolean, contact: ContactModel?) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_input, null)
        bottomSheetDialog.setContentView(view)

        val etName = view.findViewById<EditText>(R.id.etName)
        val etPhone = view.findViewById<EditText>(R.id.etPhone)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val tvTitle = view.findViewById<TextView>(R.id.tvSheetTitle)

        if (isEdit && contact != null) {
            tvTitle.text = "Edit Contact"
            etName.setText(contact.name)
            etPhone.setText(contact.phone)
            etEmail.setText(contact.email)
            btnSave.text = "Update"
        } else {
            tvTitle.text = "Add Contact"
            btnSave.text = "Save"
        }

        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()) {
                if (isEdit && contact != null) {
                    dbHelper.updateContact(contact.id, name, phone, email)
                    Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.addContact(name, phone, email)
                    Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                }
                refreshData()
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetDialog.show()
    }

    override fun onEditClick(contact: ContactModel) {
        showBottomSheet(true, contact)
    }

    override fun onDeleteClick(contact: ContactModel) {
        dbHelper.deleteContact(contact.id)
        Toast.makeText(this, "Data deleted successfully", Toast.LENGTH_SHORT).show()
        refreshData()
    }

    private fun exportToCSV(uri: Uri) {
        try {
            val outputStream = contentResolver.openOutputStream(uri)
            val writer = outputStream?.bufferedWriter()

            writer?.write("Name,Phone,Email")
            writer?.newLine()

            val list = dbHelper.getAllContacts()
            for (contact in list) {
                writer?.write("${contact.name},${contact.phone},${contact.email}")
                writer?.newLine()
            }

            writer?.flush()
            writer?.close()

            Toast.makeText(this, "Data exported successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importFromCSV(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.readLine() // Skip header

            var line: String?
            var count = 0

            while (reader.readLine().also { line = it } != null) {
                val tokens = line?.split(",")
                if (tokens != null && tokens.size >= 3) {
                    dbHelper.addContact(
                        tokens[0].trim(),
                        tokens[1].trim(),
                        tokens[2].trim()
                    )
                    count++
                }
            }

            reader.close()
            refreshData()

            Toast.makeText(this, "$count contacts imported successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
