package com.example.medimate.recommendation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medimate.R
import com.example.medimate.recommendation.api.Product
import com.example.medimate.recommendation.api.RetrofitClient
import kotlinx.coroutines.launch

class RecommendActivity : AppCompatActivity() {
    private val apiKey = "5vdpC0fiXEBDtS8A/bOV5Ql5cWmDmsIKEcpv4bryubBdLpyXAnET8rszjBUPgqHL3uCOgQhz2GDc/aI3x1CHQg==" //디코딩 서비스 키
   
    //페이징(Paging)기능 관리를 위한 변수들
    private var currentPage = 1
    private var totalCount = 0
    private var isLoading = false
    private val productList = mutableListOf<Product>()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recommend)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //스크롤해도 위에 있던 데이터가 삭제되지 않는 목록
        setupRecyclerView()
        loadProducts(currentPage)
    }

    //RecyclerView를 설정하고 스크롤 이벤트 감지하는 함수
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter() //어댑터 객체 생성
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = productAdapter

        //스크롤 리스너 추가 (스크롤이 맨 아래에 닿으면 다음 페이지 로드)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                //로딩중이 아니고 스크롤이 맨 아래에 도달했다면
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    //불러올 데이터가 남아있다면
                    if (productList.size < totalCount) {
                        //다음 페이지를 로드하기 위해 페이지번호 증가시키고 데이터 로딩 함수 호출
                        currentPage++
                        loadProducts(currentPage)
                    }
                }
            }
        })
    }

    //API를 통해 제품 데이터 로드하고 필러팅하는 함수
    private fun loadProducts(pageNo: Int) {
        if (isLoading) return
        isLoading = true

        //UI 멈춤 방지를 위해 백그라운드에서 네트워크 작업 수행
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getSupplements(apiKey, pageNo = pageNo, numOfRows = 100, type = "xml") //API 호출(100개씩)
                val fetchedProducts = response.body?.items?.productList ?: emptyList()
                totalCount = response.body?.totalCount ?: 0

                val symptoms = intent.getStringArrayListExtra("health_status") ?: arrayListOf() //HealthInputActivity에서 전달받은 키워드 리스트 가져옴
                val recommendedProducts = filterProducts(fetchedProducts, symptoms) //키워드 필터링

                if (recommendedProducts.isNotEmpty()) {
                    productAdapter.addProducts(recommendedProducts) //어댑터에 제품 추가
                }

                if (productList.isEmpty() && recommendedProducts.isEmpty() && productList.size < totalCount) {
                    //첫 페이지에 결과가 없으면 다음 페이지 자동 로드
                    currentPage++
                    loadProducts(currentPage)
                } else if (pageNo == 1 && productList.isEmpty() && recommendedProducts.isEmpty()) {
                    Toast.makeText(this@RecommendActivity, "관련 제품을 찾지 못했습니다.", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("RecommendActivity", "API 호출 실패", e)
                Toast.makeText(this@RecommendActivity, "데이터 로딩 실패", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    //제품 리스트를 키워드로 필터링하는 함수
    private fun filterProducts(products: List<Product>, symptoms: List<String>): List<Product> {
        if (symptoms.isEmpty()) return products // 증상 선택이 없으면 모든 제품 반환 (디버깅용)
        return products.filter { product ->
            symptoms.any { symptom ->
                product.mainFunction?.contains(symptom) == true
            }
        }
    }
}