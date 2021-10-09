package com.ulia.ulia_id

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.health.SystemHealthManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ulia.ulia_id.databinding.ActivityCategoryAddBinding

class CategoryAddActivity : AppCompatActivity() {

    //view binding
    private  lateinit var binding: ActivityCategoryAddBinding
    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Harap tunggu...")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle click, go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, begin upload category
        binding.submitBtn.setOnClickListener {
            validateData()
        }

    }

    private var category = ""

    private fun validateData() {
        //get data
        category = binding.categoryEt.text.toString().trim()
        //validate data
        if (category.isEmpty()){
            Toast.makeText(this, "Masukan Kategori...", Toast.LENGTH_SHORT).show()
        }
        else{
            addCategoryFirebase()
        }

    }

    private fun addCategoryFirebase() {
        //show progress
        progressDialog.show()
        //get timestamp
        val timestamp = System.currentTimeMillis()
        //setup data to add in firebase db
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        //add to firebase db : db root >categories>categoryId>category info
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                //added successfully
                progressDialog.dismiss()
                Toast.makeText(this, "Berhasil ditambahkan...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                //failed to add
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add due to${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}