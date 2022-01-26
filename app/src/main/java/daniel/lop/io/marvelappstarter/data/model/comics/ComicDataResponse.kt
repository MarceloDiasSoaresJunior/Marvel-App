package daniel.lop.io.marvelappstarter.data.model.comics

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ComicDataResponse(
    @SerializedName("data")
    val data: ComicModelData
):Serializable
