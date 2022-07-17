package com.ziyad.pencatatuang

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ziyad.pencatatuang.databinding.NotesCardBinding
import com.ziyad.pencatatuang.model.Notes
import java.text.DecimalFormat

class NoteAdapter(val activity: Home) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    var dataset = ArrayList<Notes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            NotesCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataset[position], dataset, position, activity)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    class ViewHolder(val binding: NotesCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Notes, dataset: ArrayList<Notes>, position: Int, activity: Home) {
            binding.apply {
                tvNama.text = data.nama
                tvTanggal.text = "${data.hari}/${data.bulan}/${data.tahun}"
                tvNominal.text = "${data.kategori} ${toRp(data.nominal)}"

                btnEdit.setOnClickListener {
                    val intent = Intent(itemView.context, Add::class.java)
                    intent.putExtra(Add.notes, data)
                    itemView.context.startActivity(intent)
                }

            }
        }

        fun toRp(str: String): String {
            val x= (String.format("%,d", str.toInt())).replace(',', '.')
            return "Rp $x"
        }
    }

}