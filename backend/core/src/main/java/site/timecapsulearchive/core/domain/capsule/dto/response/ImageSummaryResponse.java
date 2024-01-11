package site.timecapsulearchive.core.domain.capsule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

@Schema(description = "이미지 요약 정보")
@Validated
public record ImageSummaryResponse(

    @Schema(description = "이미지 아이디")
    Long id,

    @Schema(description = "이미지 url")
    String imageUrl
) {

}
