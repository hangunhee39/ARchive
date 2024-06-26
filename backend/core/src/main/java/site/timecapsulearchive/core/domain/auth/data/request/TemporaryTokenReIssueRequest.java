package site.timecapsulearchive.core.domain.auth.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import site.timecapsulearchive.core.domain.member.entity.SocialType;

@Schema(description = "소셜 프로바이더의 인증 아이디로 임시 인증 토큰 재발급 요청")
public record TemporaryTokenReIssueRequest(

    @Schema(description = "소셜 프로바이더 인증 아이디")
    @NotBlank(message = "인증 아이디는 필수입니다.")
    String authId,

    @Schema(description = "소셜 프로바이더 타입")
    @NotNull(message = "소셜 프로바이더 타입은 필수입니다.")
    SocialType socialType
) {

}
