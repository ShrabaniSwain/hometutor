package com.flyngener.tutorhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.ActivitySearchServiceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchServiceBinding
    private lateinit var searchList: List<SearchKeyword>
    private var type = 2
    private lateinit var adapter: SearchServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSearchKeyword()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString()


                val filteredList = searchList.filter { marketDetail ->
                    marketDetail.service_profile_name.contains(searchText, ignoreCase = true)
                }

                if (filteredList.isEmpty()){
                    type = 0
                }else{
                    type = 1
                }
                if (searchText.length >= 3) {
                    val searchKeyword = SearchKeyResult(Constant.INSERT_ID, searchText, type)
                    saveKeywordToApi(searchKeyword)
                    Log.i("TAG", "onTextChanged: $searchKeyword")
                }

                if (binding.etSearch.text.toString().isBlank()){
                    binding.rvService.visibility = View.GONE
                }else{
                    binding.rvService.visibility = View.VISIBLE
                }

                adapter = SearchServiceAdapter(searchList, applicationContext)
                binding.rvService.adapter = adapter
                binding.rvService.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                adapter.updateList(filteredList)

            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }


    private fun getSearchKeyword() {
        val call = RetrofitClient.api.getSearchKeyword()

        call.enqueue(object : Callback<SearchKeywordResponse> {
            override fun onResponse(call: Call<SearchKeywordResponse>, response: Response<SearchKeywordResponse>) {
                if (response.isSuccessful) {

                    searchList = response.body()?.result ?: emptyList()

                    Log.i("TAG", "response: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<SearchKeywordResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }

    private fun saveKeywordToApi(keyword: SearchKeyResult) {
        val call = RetrofitClient.api.searchKeyByGuardian(keyword)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(call: Call<HomeTutorModel>, response: Response<HomeTutorModel>) {
                if (response.isSuccessful) {
                    if (response.isSuccessful) {
                        Log.i("TAG", "Keyword saved successfully: ${response.body()}")
                    }
                } else {
                    Log.e("TAG", "Error saving keyword: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Log.e("TAG", "Failed to save keyword: ${t.message}")
            }
        })
    }
}