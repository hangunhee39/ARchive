package site.timecapsulearchive.core.domain.capsule.group_capsule.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import site.timecapsulearchive.core.domain.capsule.generic_capsule.data.dto.CapsuleSummaryDto;
import site.timecapsulearchive.core.domain.capsule.group_capsule.data.dto.GroupCapsuleSummaryDto;
import site.timecapsulearchive.core.domain.group.data.response.GroupMemberSummaryResponse;
import site.timecapsulearchive.core.global.common.response.ResponseMappingConstant;

@Schema(description = "캡슐 요약 정보")
@Builder
public record GroupCapsuleSummaryResponse(

    @Schema(description = "그룹원 요약 정보")
    List<GroupMemberSummaryResponse> members,

    @Schema(description = "생성자 닉네임")
    String nickname,

    @Schema(description = "생성자 프로필 url")
    String profileUrl,

    @Schema(description = "캡슐 스킨 url")
    String skinUrl,

    @Schema(description = "제목")
    String title,

    @Schema(description = "개봉일")
    ZonedDateTime dueDate,

    @Schema(description = "캡슐 생성 주소")
    String address,

    @Schema(description = "캡슐 생성 도로 이름")
    String roadName,

    @Schema(description = "개봉 여부")
    Boolean isOpened,

    @Schema(description = "캡슐 생성 일")
    ZonedDateTime createdAt
) {

    public GroupCapsuleSummaryResponse {
        if (dueDate != null) {
            dueDate = dueDate.withZoneSameInstant(ResponseMappingConstant.ZONE_ID);
        }

        createdAt = createdAt.withZoneSameInstant(ResponseMappingConstant.ZONE_ID);
    }

    public static GroupCapsuleSummaryResponse createOf(
        final GroupCapsuleSummaryDto summaryDto,
        final Function<String, String> preSignUrlFunction
    ) {
        CapsuleSummaryDto capsuleSummaryDto = summaryDto.capsuleSummaryDto();

        return new GroupCapsuleSummaryResponse(
            summaryDto.toGroupMemberSummaryResponse(),
            capsuleSummaryDto.nickname(),
            capsuleSummaryDto.profileUrl(),
            preSignUrlFunction.apply(capsuleSummaryDto.skinUrl()),
            capsuleSummaryDto.title(),
            capsuleSummaryDto.dueDate(),
            capsuleSummaryDto.address(),
            capsuleSummaryDto.roadName(),
            capsuleSummaryDto.isOpened(),
            capsuleSummaryDto.createdAt()
        );
    }
}
