package com.ansela.newsapplication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ansela.newsapplication.adapter.NewsAdapter
import com.ansela.newsapplication.adapter.OnItemClickCallback
import com.ansela.newsapplication.databinding.ActivityMainBinding
import com.ansela.newsapplication.model.ArticlesItem
import com.ansela.newsapplication.model.ResponseNews
import com.ansela.newsapplication.service.RetrofitConfig
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    val date = getCurreDate()
    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    lateinit var mainBinding: ActivityMainBinding

    private fun getCurreDate(): Date {
        return Calendar.getInstance().time

    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formater = SimpleDateFormat(format, locale)
        return formater.format(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        supportActionBar?.hide()
        mainBinding.apply {
            ibProfile.setOnClickListener(this@MainActivity)
            tvDateMain.text = date.toString("dd/MM/yyy")
        }
//        mainBinding.ibProfile.setOnClickListener(this)
//        mainBinding.tvDateMain.text = date.toString("dd/MM/yyy")
        getNews()

    }

    companion object {
        fun getLauchService(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.ibProfile -> startActivity(Intent(ProfileActivity.getLauchService(this)))
        }
    }

    private fun getNews() {
        val country = "id"
        val apiKey = "9d97a9bc85b54dc8804f9ef68e5d3c5c"

        val loading = ProgressDialog.show(this, "Request Data", "Loading ..")
        RetrofitConfig.getInstance().getNewsHeadLines(country, apiKey).enqueue(
            object : Callback<ResponseNews> {
                override fun onResponse(
                    call: Call<ResponseNews>,
                    response: Response<ResponseNews>
                ) {
                    Log.d("Response", "Success" + response.body()?.articles)
                    loading.dismiss()
                    if (response.isSuccessful) {
                        val status = response.body()?.status
                        if (status.equals("ok")) {
                            Toast.makeText(this@MainActivity, "Data Success", Toast.LENGTH_SHORT)
                                .show()
                            val newsData = response.body()?.articles
                            val newsAdapter = NewsAdapter(this@MainActivity, newsData)
                            newsAdapter.setOnItemClickCallback(object : OnItemClickCallback {
                                override fun onItemClicked(news: ArticlesItem) {
                                    val intent =
                                        Intent(this@MainActivity, DetailActivity::class.java)
                                    intent.putExtra(DetailActivity.ESTRA_NEWS, news)
                                    startActivity(intent)
                                }
                            })
                            mainBinding.rvMain.apply {
                                adapter = newsAdapter
                                layoutManager = LinearLayoutManager(this@MainActivity)
                                val dataHighlight = response.body()

                                Glide.with(this@MainActivity)
                                    .load(dataHighlight?.articles?.component4()?.url)
                                    .centerCrop().into(mainBinding.ivHighlight)

                                mainBinding.apply {
                                    tvTitleHighlight.text =
                                        dataHighlight?.articles?.component4()?.title
                                    tvDateHighlight.text =
                                        dataHighlight?.articles?.component4()?.publishedAt
                                    tvAuthorNameHighlight.text =
                                        dataHighlight?.articles?.component4()?.author
                                }
                            }


                        } else {
                            Toast.makeText(this@MainActivity, "Data Faild", Toast.LENGTH_SHORT)
                        }
                    }

                    override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                        Log.d("Response", "Failed :" + t.localizedMessage)
                        loading.dismiss()
                    }
                }
            })
    }
}



