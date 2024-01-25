package com.droidblossom.archive.data.repository

import com.droidblossom.archive.data.dto.ResponseBody
import com.droidblossom.archive.data.dto.auth.response.HealthResponseDto
import com.droidblossom.archive.data.dto.member.request.MemberStatusRequestDto
import com.droidblossom.archive.data.dto.member.response.MemberDetailResponseDto
import com.droidblossom.archive.data.dto.member.response.MemberStatusResponseDto
import com.droidblossom.archive.data.source.remote.api.MemberService
import com.droidblossom.archive.domain.model.auth.Health
import com.droidblossom.archive.domain.model.member.MemberDetail
import com.droidblossom.archive.domain.model.member.MemberStatus
import com.droidblossom.archive.domain.repository.MemberRepository
import com.droidblossom.archive.util.RetrofitResult
import com.droidblossom.archive.util.apiHandler
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val api: MemberService
) : MemberRepository {
    override suspend fun getMe(): RetrofitResult<MemberDetail> {
        return apiHandler({ api.getMeApi() }) { response: ResponseBody<MemberDetailResponseDto> -> response.result.toModel() }
    }

    override suspend fun postMemberStatus(request: MemberStatusRequestDto): RetrofitResult<MemberStatus> {
        return apiHandler({ api.postMeStatusApi(request) }) { response : ResponseBody<MemberStatusResponseDto> -> response.result.toModel() }
    }

    override suspend fun getText(): RetrofitResult<Health> {
        return apiHandler({ api.getTextApi() }) { response: ResponseBody<HealthResponseDto> -> response.result.toModel() }
    }
}