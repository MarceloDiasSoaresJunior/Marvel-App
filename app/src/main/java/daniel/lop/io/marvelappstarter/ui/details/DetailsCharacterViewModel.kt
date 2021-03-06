package daniel.lop.io.marvelappstarter.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.lop.io.marvelappstarter.data.model.comics.ComicDataResponse
import daniel.lop.io.marvelappstarter.repository.MarvelRepository
import daniel.lop.io.marvelappstarter.ui.state.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailsCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository

): ViewModel() {
    private val _details = MutableStateFlow<ResourceState<ComicDataResponse>>(ResourceState.Loading())
    val details: StateFlow<ResourceState<ComicDataResponse>> = _details

    fun  fetch(characterId: Int) = viewModelScope.launch{
        safeFetch(characterId)
    }

    private suspend fun safeFetch(characterId: Int) {
        _details.value = ResourceState.Loading()
        try {
            val response = repository.getComics(characterId)
            _details.value = handleResponse(response)
        }catch (t: Throwable){
            when(t){
                is IOException -> _details.value = ResourceState.Erros("Erro na rede")
                else -> _details.value = ResourceState.Erros("Erro na conversão de dados")
            }
        }
    }

    private fun handleResponse(response: Response<ComicDataResponse>): ResourceState<ComicDataResponse> {
        if(response.isSuccessful){
            response.body()?.let { values ->
                return ResourceState.Success(values)
            }
        }
        return ResourceState.Erros(response.message())
    }
}