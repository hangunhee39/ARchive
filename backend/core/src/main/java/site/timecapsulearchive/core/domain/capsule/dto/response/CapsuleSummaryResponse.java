package site.timecapsulearchive.core.domain.capsule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import org.springframework.validation.annotation.Validated;

@Schema(description = "캡슐 요약 정보")
@Validated
public record CapsuleSummaryResponse(

    @Schema(description = "캡슐 아이디")
    Long id,

    @Schema(description = "생성자 닉네임")
    String nickname,

    @Schema(description = "캡슐 스킨 url")
    String capsuleSkinUrl,

    @Schema(description = "제목")
    String title,

    @Schema(description = "개봉일")
    ZonedDateTime dueDate,

    @Schema(description = "캡슐 생성 주소")
    String address
) {

}