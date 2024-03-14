package site.timecapsulearchive.core.domain.member.dto;

import site.timecapsulearchive.core.domain.member.entity.SocialType;

public record SignUpRequestDto(
    String authId,
    String email,
    String profileUrl,
    SocialType socialType
) {

}