package site.timecapsulearchive.core.domain.member.data.reqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import site.timecapsulearchive.core.domain.member.entity.SocialType;

@Schema(description = "소셜 프로바이더의 인증 아이디로 회원 인증 상태 체크")
public record CheckStatusRequest(

    @Schema(description = "소셜 프로바이더 인증 아이디")
    @NotBlank(message = "인증 아이디는 필수 입니다.")
    String authId,

    @Schema(description = "소셜 프로바이더 타입")
    @NotNull(message = "소셜 프로바이더 타입은 필수입니다.")
    SocialType socialType
) {

}
