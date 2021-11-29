package com.ansela.newsapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ansela.newsapplication.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUser: DatabaseReference
    private var firebaseUserId: String = ""
    private lateinit var activitySignupBinding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignupBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(activitySignupBinding.root)
        mAuth = FirebaseAuth.getInstance()
        activitySignupBinding.btnSignup.setOnClickListener(this)
    }

    companion object {
        fun getLaunchServive(from: Context) = Intent(from, SignupActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.btnSignup -> signupUser()
        }
    }

    private fun signupUser() {
        val fullName: String = activitySignupBinding.etNameSignup.text.toString()
        val email: String = activitySignupBinding.etEmailSignup.text.toString()
        val password: String = activitySignupBinding.etPasswordSignup.toString()
        val confirmPassword: String = activitySignupBinding.etConfirmPasswordSignup.toString()

        if (fullName == "") {
            Toast.makeText(this, "No Empty", Toast.LENGTH_SHORT).show()
        } else if (email == "") {
            Toast.makeText(this, "No Empty", Toast.LENGTH_SHORT).show()
        } else if (password == "") {
            Toast.makeText(this, "No Empty", Toast.LENGTH_SHORT).show()
        } else if ((confirmPassword == "").equals(password)) {
            Toast.makeText(this, "No Empty", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    firebaseUserId = mAuth.currentUser!!.uid
                    refUser =
                        FirebaseDatabase.getInstance().reference.child("User").child(firebaseUserId)
                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserId
                    userHashMap["fullName"] = fullName
                    userHashMap["email"] = email
                    userHashMap["linkedIn"] = ""
                    userHashMap["instagram"] = ""
                    userHashMap["medium"] = ""
                    userHashMap["photo"] = ""

                    refUser.updateChildren(userHashMap).addOnCompleteListener {
                        if (it.isSuccessful){
                        startActivity(Intent(MainActivity.getLauchService(this)))
                        finish()


                    }else{
                        Toast.makeText(
                            this,
                            "Register Faild" + it.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()

                    }
                    }
                }
            }

        }


    }
}