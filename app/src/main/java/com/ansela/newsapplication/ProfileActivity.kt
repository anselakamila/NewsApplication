package com.ansela.newsapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ansela.newsapplication.databinding.ActivityProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.jetbrains.anko.email

class ProfileActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var profileBinding:ActivityProfileBinding
    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)
        supportActionBar?.hide()
        profileBinding.apply {
            tvLogout.setOnClickListener(this@ProfileActivity)
            ivBackProfile.setOnClickListener(this@ProfileActivity)
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        refUsers!!.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val name = snapshot.child("fullname").value.toString()
                    val emai = snapshot.child("email").value.toString()
                    val photo = snapshot.child("photo").value.toString()
                    profileBinding.apply {
                        tvNameProfile.text = name
                        tvEmaiProfile.text = emai
                        Glide.with(this@ProfileActivity).load(photo).into(profileBinding.ivProfile)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    companion object{
        fun getLauchService(from : Context) = Intent(from, ProfileActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.tv_logout->logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}