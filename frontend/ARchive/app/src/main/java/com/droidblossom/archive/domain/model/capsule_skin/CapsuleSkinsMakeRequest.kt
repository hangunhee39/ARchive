package com.droidblossom.archive.domain.model.capsule_skin

import com.droidblossom.archive.data.dto.capsule_skin.request.CapsuleSkinsMakeRequestDto
import com.droidblossom.archive.util.CapsuleSkinUtils
import com.droidblossom.archive.util.Motion
import com.droidblossom.archive.util.Retarget
import java.io.File

data class CapsuleSkinsMakeRequest(
    val skinName : String,
    val imageUrl : String,
    val directory : String,
    val motionName : Motion,
    val retarget : Retarget
){
    fun toDto()= CapsuleSkinsMakeRequestDto(
        skinName = this.skinName,
        imageUrl = this.imageUrl,
        directory = this.directory,
        motionName = this.motionName.toString(),
        retarget = this.retarget.toString()
    )
}