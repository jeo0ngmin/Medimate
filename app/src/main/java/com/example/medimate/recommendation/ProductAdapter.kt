package com.example.medimate.recommendation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medimate.R
import com.example.medimate.recommendation.api.Product

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    //어댑터가 관리할 제품 데이터 목록
    private val productList = mutableListOf<Product>()

    //리스트 한 칸에 포함된 UI 요소들을 보관
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.tvProductName)
        val company: TextView = view.findViewById(R.id.tvCompany)
        val function: TextView = view.findViewById(R.id.tvFunction)
    }

    //RecyclerView가 새로운 '칸'이 필요할 때 호출하는 함수(붕어빵 틀을 만듦)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    //RecyclerView가 특정 위치의 '칸'에 데이터를 표시해야 할 때 호출하는 함수(붕어빵 안에 팥을 넣음)
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.productName?.trim()
        holder.company.text = product.company?.trim()
        holder.function.text = product.mainFunction?.trim()
    }

    override fun getItemCount() = productList.size

    //페이징을 통해 새로운 데이터를 가져왔을 때 호출하는 함수
    fun addProducts(newProducts: List<Product>) {
        val startPosition = productList.size
        productList.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }
}