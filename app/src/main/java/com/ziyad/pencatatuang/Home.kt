package com.ziyad.pencatatuang

import android.R
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.Observer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.whiteelephant.monthpicker.MonthPickerDialog
import com.ziyad.pencatatuang.databinding.ActivityHomeBinding
import com.ziyad.pencatatuang.model.Notes
import java.text.NumberFormat
import java.util.*

class Home : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val viewModel: totalViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var email: String
    private lateinit var adapter: NoteAdapter
    private lateinit var uid: String

    var month: String = ""
    var year: String = ""
    var kategori: String = ""
    var plus_bulan_ini: Int = 0
    var minus_bulan_ini: Int = 0
    var total_bulan_ini: Int = 0
    var plus_all: Int = 0
    var minus_all: Int = 0
    var total_all: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = Firebase.firestore
        uid = auth.currentUser?.uid.toString()
        adapter = NoteAdapter(this)
        getNotesData()
        val today = Calendar.getInstance()
        binding.apply {
            rvHome.layoutManager = LinearLayoutManager(this@Home)
            button.setOnClickListener {

                val builder = MonthPickerDialog.Builder(
                    this@Home,
                    { selectedMonth, selectedYear ->
                        Log.d(TAG, "selectedMonth : $selectedMonth selectedYear : $selectedYear")
//                        Toast.makeText(
//                            this@Home,
//                            "Date set with month$selectedMonth year $selectedYear",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }, today[Calendar.YEAR], today[Calendar.MONTH]
                )
                builder.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1990)
                    .setActivatedYear(2017)
                    .setMaxYear(2030)
                    .setMinMonth(Calendar.JANUARY)
                    .setTitle("Select trading month")
                    .setMonthRange(
                        Calendar.JANUARY,
                        Calendar.DECEMBER
                    ) // .setMaxMonth(Calendar.OCTOBER)
                    // .setYearRange(1890, 1890)
                    // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                    //.showMonthOnly()
                    // .showYearOnly()
                    .setOnMonthChangedListener { selectedMonth ->
                        Log.d("SIP", "Selected month : $selectedMonth")
                        month = "${selectedMonth + 1}"
//                         Toast.makeText(this, " Selected month : " + selectedMonth, Toast.LENGTH_SHORT).show();
                    }
                    .setOnYearChangedListener { selectedYear ->
                        Log.d(TAG, "Selected year : $selectedYear")
                        year = "$selectedYear"
                        binding.button.text = "$month/$year"
                        // Toast.makeText(MainActivity.this, " Selected year : " + selectedYear, Toast.LENGTH_SHORT).show();
                    }
                    .build()
                    .show()

            }
            btnTambah.setOnClickListener {
                startActivity(Intent(this@Home, Add::class.java))
            }
            btnSetting.setOnClickListener {
                startActivity(Intent(this@Home, Setting::class.java))
            }
            btnApply.setOnClickListener {
                getNotesData(month, year, kategori)
            }
            btnReset.setOnClickListener {
                getNotesData()
            }
        }


        binding.tipe.setOnItemSelectedListener(this)
        val tipe = mutableListOf<String>("all", "+", "-")
        val aa = ArrayAdapter(this, R.layout.simple_spinner_item, tipe)
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.tipe.adapter = aa

        viewModel.totalBulanIni.observe(this, Observer {
            binding.tvTotalBulanIni.text="= ${toRp(it.toString())}"
        })
        viewModel.totalAll.observe(this, Observer {
            binding.tvTotalAll.text="= ${toRp(it.toString())}"
        })

        database.collection("notes")
            .whereEqualTo("uid", uid)
            .whereEqualTo("kategori", "+")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    plus_all+=document.getString("nominal").toString().toInt()
                }
                viewModel.totalAll.value= viewModel.totalAll.value?.plus(plus_all)
                binding.tvPlusAll.text="+ ${toRp(plus_all.toString())}"
            }
        database.collection("notes")
            .whereEqualTo("uid", uid)
            .whereEqualTo("kategori", "-")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    minus_all+=document.getString("nominal").toString().toInt()
                }

                viewModel.totalAll.value= viewModel.totalAll.value?.minus(minus_all)
                binding.tvMinusAll.text="- ${toRp(minus_all.toString())}"
//                binding.tvTotalAll.text=toRp((plus_all-minus_all).toString())
            }
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        // use position to know the selected item
        when(position){
            0 -> kategori = "all"
            1 -> kategori = "+"
            2 -> kategori = "-"
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
        kategori="all"
    }

    private fun getNotesData() {
        adapter.dataset.clear()
        adapter.notifyDataSetChanged()
        binding.button.text = "Bln/Thn"
        database.collection("notes")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    adapter.dataset.add(
                        Notes(
                            noteId = document.getString("noteId").toString(),
                            uid = document.getString("uid").toString(),
                            nama = document.getString("nama").toString(),
                            hari = document.getString("hari").toString(),
                            bulan = document.getString("bulan").toString(),
                            tahun = document.getString("tahun").toString(),
                            kategori = document.getString("kategori").toString(),
                            nominal = document.getString("nominal").toString()
                        )
                    )
                    binding.rvHome.adapter = adapter
                }
            }
    }

    private fun getNotesData(bulan: String, tahun: String, kategori: String) {
        adapter.dataset.clear()
        adapter.notifyDataSetChanged()
        plus_bulan_ini=0
        minus_bulan_ini=0
        total_bulan_ini=0
        viewModel.totalBulanIni.value=0
        database.collection("notes")
            .whereEqualTo("uid", uid)
            .whereEqualTo("bulan", bulan)
            .whereEqualTo("tahun", tahun)
            .whereEqualTo("kategori", "+")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    plus_bulan_ini+=document.getString("nominal").toString().toInt()
                }
                viewModel.totalBulanIni.value= viewModel.totalBulanIni.value?.plus(plus_bulan_ini)
                binding.tvPlusBulanIni.text="+ ${toRp(plus_bulan_ini.toString())}"
            }
        database.collection("notes")
            .whereEqualTo("uid", uid)
            .whereEqualTo("bulan", bulan)
            .whereEqualTo("tahun", tahun)
            .whereEqualTo("kategori", "-")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    minus_bulan_ini+=document.getString("nominal").toString().toInt()
                }
                viewModel.totalBulanIni.value= viewModel.totalBulanIni.value?.minus(minus_bulan_ini)
                binding.tvMinusBulanIni.text="- ${toRp(minus_bulan_ini.toString())}"
            }

        if(kategori=="all"){
            database.collection("notes")
                .whereEqualTo("uid", uid)
                .whereEqualTo("bulan", bulan)
                .whereEqualTo("tahun", tahun)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        adapter.dataset.add(
                            Notes(
                                noteId = document.getString("noteId").toString(),
                                uid = document.getString("uid").toString(),
                                nama = document.getString("nama").toString(),
                                hari = document.getString("hari").toString(),
                                bulan = document.getString("bulan").toString(),
                                tahun = document.getString("tahun").toString(),
                                kategori = document.getString("kategori").toString(),
                                nominal = document.getString("nominal").toString()
                            )
                        )
                        binding.rvHome.adapter = adapter
                    }
                }
        }else {
            database.collection("notes")
                .whereEqualTo("uid", uid)
                .whereEqualTo("bulan", bulan)
                .whereEqualTo("tahun", tahun)
                .whereEqualTo("kategori", kategori)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        adapter.dataset.add(
                            Notes(
                                noteId = document.getString("noteId").toString(),
                                uid = document.getString("uid").toString(),
                                nama = document.getString("nama").toString(),
                                hari = document.getString("hari").toString(),
                                bulan = document.getString("bulan").toString(),
                                tahun = document.getString("tahun").toString(),
                                kategori = document.getString("kategori").toString(),
                                nominal = document.getString("nominal").toString()
                            )
                        )
                        binding.rvHome.adapter = adapter
                    }
                }
        }
    }

    fun toRp(str: String): String {
        val x= (String.format("%,d", str.toInt())).replace(',', '.')
        return "Rp $x"
    }
}