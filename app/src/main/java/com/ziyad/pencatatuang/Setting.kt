package com.ziyad.pencatatuang

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.ziyad.pencatatuang.databinding.ActivitySettingBinding

class Setting : AppCompatActivity() {
    private lateinit var user: FirebaseUser
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance().currentUser!!
        binding.etEmail.setText(user.email)
        binding.btnLogout.setOnClickListener {
            AuthUI.getInstance().signOut(this)
                .addOnCompleteListener {
                    startActivity(
                        Intent(this, MainActivity::class.java)

                    )
                    // ...
                }
        }
        binding.btnSave.setOnClickListener {
            var pwt = true
            if (binding.etPassword.text.toString() != "" || binding.etRePassword.text.toString() != "") {
                if (binding.etPassword.text.toString() == binding.etRePassword.text.toString()) {
                    user.updatePassword(binding.etPassword.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User password updated.")
                                Toast.makeText(this, "User password updated.", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "User password not updated.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }

                } else {
                    Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT)
                        .show()
                    pwt = false
                }
            }
            if (binding.etEmail.text.toString() != "" && pwt) {
                user.updateEmail(binding.etEmail.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User email address updated.")
                            Toast.makeText(this, "Email updated.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
            Toast.makeText(this, "Perubahan telah disimpan", Toast.LENGTH_SHORT)
                .show()


        }
    }
}