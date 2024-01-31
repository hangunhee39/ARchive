package com.droidblossom.archive.domain.usecase.kakao

import com.droidblossom.archive.data.dto.kakao.response.AddressDto
import com.droidblossom.archive.domain.repository.KakaoRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddressUseCase @Inject constructor(
    private val repository: KakaoRepository
) {
    suspend operator fun invoke(x: String, y:String) =
        flow<AddressDto?> {
            emit(repository.getAddress(x,y))
        }
}