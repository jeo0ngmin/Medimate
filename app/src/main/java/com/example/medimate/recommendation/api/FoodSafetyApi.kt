package com.example.medimate.recommendation.api

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import retrofit2.http.GET
import retrofit2.http.Query

//--------데이터 클래스 (XML 데이터 구조를 그대로 본뜬 '그릇')---------//

// 1. 최상위 <response> 태그
@Root(name = "response", strict = false)
data class FoodResponse(
    @field:Element(name = "body", required = false)
    var body: Body? = null
)

// 2. <body> 태그
@Root(name = "body", strict = false)
data class Body(
    @field:Element(name = "totalCount", required = false)
    var totalCount: Int = 0,

    @field:Element(name = "items", required = false)
    var items: Items? = null
)

// 3. <items> 태그
@Root(name = "items", strict = false)
data class Items(
    @field:ElementList(inline = true, name = "item", required = false)
    var productList: List<Product>? = null
)

// 4. 실제 제품 정보가 담긴 <item> 태그
@Root(name = "item", strict = false)
data class Product(
    @field:Element(name = "PRDUCT", required = false)
    var productName: String? = null,

    @field:Element(name = "ENTRPS", required = false)
    var company: String? = null,

    @field:Element(name = "MAIN_FNCTN", required = false)
    var mainFunction: String? = null
)

// API 요청 인터페이스
interface FoodSafetyApi {
    //HTTP GET 방식으로 요청할 API임을 명시
    @GET("getHtfsItem01")
    suspend fun getSupplements(
        @Query("ServiceKey") apiKey: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 100,
        @Query("type") type: String = "xml"
    ): FoodResponse
}