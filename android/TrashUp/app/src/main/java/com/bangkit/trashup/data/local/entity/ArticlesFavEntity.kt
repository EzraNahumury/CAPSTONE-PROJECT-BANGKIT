package com.bangkit.trashup.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bangkit.trashup.data.remote.response.DatasItem
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class ArticlesFavEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val pitcURL: String,
    val wasteType: String,
    val wasteGroup: String,
    val tools: String,
    val steps: String,
    val desc: String,
    val totalView: Int
) : Parcelable {

    fun toDatasItem(): DatasItem {
        return DatasItem(
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
    }
}
