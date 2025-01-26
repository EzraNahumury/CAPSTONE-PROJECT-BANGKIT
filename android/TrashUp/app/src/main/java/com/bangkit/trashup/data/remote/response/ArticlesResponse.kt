package com.bangkit.trashup.data.remote.response

import android.os.Parcelable
import com.bangkit.trashup.data.local.entity.ArticlesFavEntity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticlesResponse(

    @field:SerializedName("payload")
    val payload: Payload? = null
): Parcelable

@Parcelize
data class DatasItem(

    @field:SerializedName("pitcURL")
    val pitcURL: String? = null,

    @field:SerializedName("totalView")
    val totalView: Int? = null,

    @field:SerializedName("wasteType")
    val wasteType: String? = null,

    @field:SerializedName("wasteGroup")
    val wasteGroup: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("tools")
    val tools: String? = null,

    @field:SerializedName("steps")
    val steps: String? = null,

    @field:SerializedName("desc")
    val desc: String? = null
): Parcelable {

    fun toArticlesFavEntity(): ArticlesFavEntity? {
        return if (id != null && title != null && pitcURL != null && wasteType != null && wasteGroup != null &&
            tools != null && steps != null && desc != null && totalView != null
        ) {
            ArticlesFavEntity(
                id = id,
                title = title,
                pitcURL = pitcURL,
                wasteType = wasteType,
                wasteGroup = wasteGroup,
                tools = tools,
                steps = steps,
                desc = desc,
                totalView = totalView
            )
        } else {
            null
        }
    }
}

@Parcelize
data class Payload(

    @field:SerializedName("status_code")
    val statusCode: Int? = null,

    @field:SerializedName("datas")
    val datas: List<DatasItem?>? = null,

    @field:SerializedName("message")
    val message: String? = null
): Parcelable
