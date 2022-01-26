package daniel.lop.io.marvelappstarter.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.lop.io.marvelappstarter.data.model.character.CharacterDataResponse
import daniel.lop.io.marvelappstarter.repository.MarvelRepository
import daniel.lop.io.marvelappstarter.ui.state.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel

class SearchCharacterViewModel @Inject constructor(
   private val repository: MarvelRepository
): ViewModel(  ) {

    private val _searchCharacter = MutableStateFlow<ResourceState<CharacterDataResponse>>(ResourceState.Empty())
    val searchCharacter: StateFlow<ResourceState<CharacterDataResponse>> = _searchCharacter

    fun fetch(nameStartsWith: String ) = viewModelScope.launch {
        safeFetch(nameStartsWith)
    }

    private suspend fun safeFetch(nameStartsWith: String) {
        _searchCharacter.value = ResourceState.Loading()
        try{
            val response = repository.list(nameStartsWith)
            _searchCharacter.value = handleResponse(response)
        }catch (t: Throwable){
            when(t){
                is IOException -> _searchCharacter.value = ResourceState.Erros("Erro na rede")
                else -> _searchCharacter.value = ResourceState.Erros("Erro na conversão de dados")
            }
        }
    }

    private fun handleResponse(response: Response<CharacterDataResponse>): ResourceState<CharacterDataResponse> {
        if(response.isSuccessful){
            response.body()?.let { values ->
                return ResourceState.Success(values)
            }
        }
        return ResourceState.Erros(response.message())
    }
}