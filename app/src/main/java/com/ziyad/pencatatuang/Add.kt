package com.ziyad.pencatatuang

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ziyad.pencatatuang.databinding.ActivityAddBinding
import com.ziyad.pencatatuang.model.Notes
import java.util.*

class Add : AppCompatActivity(),AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityAddBinding
    val tipe= mutableListOf<String>("+","-")
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var noteId: String
    private lateinit var kategori: String
    private lateinit var uid:String

    val today = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //tipe
        binding.tipe.setOnItemSelectedListener(this)
        val aa= ArrayAdapter(this, android.R.layout.simple_spinner_item,tipe)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tipe.adapter=aa
        //add
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        uid = auth.currentUser?.uid.toString()
        val uuid = UUID.randomUUID()
        noteId = uuid.toString()
        val notes = intent.getParcelableExtra<Notes>(notes)
        Log.d("ISI NOTES", "${notes?.nama}")
        binding.apply {

            var hari="${today.get(Calendar.DAY_OF_MONTH)}"
            var bulan="${today.get(Calendar.MONTH)}"
            var tahun="${today.get(Calendar.YEAR)}"
            if(notes!=null){
                if(notes.tahun==""||notes.bulan==""||notes.hari==""){
                    datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                        today.get(Calendar.DAY_OF_MONTH)
                    ) { view, year, month, day ->
                        val month = month + 1
                        hari="$day"
                        bulan="$month"
                        tahun="$year"
                        val msg = "You Selected: $day/$month/$year"
                        Log.d("tess",msg)
                    }
                }else{
                    datePicker.init(notes.tahun.toInt(),notes.bulan.toInt(),notes.hari.toInt()){ view, year, month, day ->
                        val month = month + 1
                        hari="$day"
                        bulan="$month"
                        tahun="$year"
                        val msg = "You Selected: $day/$month/$year"
                        Log.d("tess",msg)
                }


            }
        }else{
            datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            ) { view, year, month, day ->
                val month = month + 1
                hari="$day"
                bulan="$month"
                tahun="$year"
                val msg = "You Selected: $day/$month/$year"
                Log.d("tess",msg)
            }
        }
            if (notes != null) {
                etNama.setText(notes.nama)
                var kategori=0
                if(notes.kategori=="+"){
                    kategori=0
                }else{
                    kategori=1
                }
                tipe.setSelection(kategori)
                etNominal.setText(notes.nominal)
            }
            btnHapus.setOnClickListener {
                if (notes != null) {
                    database.collection("notes").document(notes.noteId).delete()

                }
                startActivity(Intent(this@Add, Home::class.java))
                finish()
            }
            btnCancel.setOnClickListener {
                startActivity(Intent(this@Add, Home::class.java))
                finish()
            }
            btnSimpan.setOnClickListener {
                var nama=etNama.text.toString()
                var nominal = etNominal.text.toString()

                if(notes!=null){
                    editNote(notes.noteId,nama,hari,bulan,tahun,kategori,nominal)
                }else{
                    addNote(nama,hari,bulan,tahun,kategori,nominal)
                }
                startActivity(Intent(this@Add, Home::class.java))
                finish()
            }
        }

    }
    private fun addNote(nama: String, hari: String, bulan: String, tahun: String, kategori: String, nominal: String) {
        val notes=Notes(noteId,uid,nama,hari,bulan,tahun,kategori,nominal)
        database.collection("notes").document(noteId).set(notes)
            .addOnSuccessListener {
                startActivity(Intent(this@Add, Home::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Add note failed!", Toast.LENGTH_SHORT)
                Log.e("NOTES", "$e")
            }
    }
    private fun editNote(noteId: String, nama: String, hari: String, bulan: String, tahun: String, kategori: String, nominal: String) {
        database.collection("notes").document(noteId)
            .update("nama", nama, "hari", hari, "bulan", bulan, "tahun", tahun, "kategori", kategori, "nominal", nominal)
            .addOnSuccessListener {
                startActivity(Intent(this@Add, Home::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Add note failed!", Toast.LENGTH_SHORT)
                Log.e("NOTES", "$e")
            }
    }
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        // use position to know the selected item
        kategori=tipe[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
        kategori=tipe[0]
    }
    companion object {
        const val notes: String ="NOTES"
    }
}