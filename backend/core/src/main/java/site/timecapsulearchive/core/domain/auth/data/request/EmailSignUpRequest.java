package site.timecapsulearchive.core.domain.auth.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 회원가입 요청")
public record EmailSignUpRequest(

    @Schema(description = "사용할 이메일")
    @NotBlank
    @Email
    String email,

    @Schema(description = "사용할 비밀번호")
    @NotBlank
    String password
) {

}
