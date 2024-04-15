package site.timecapsulearchive.core.domain.capsule.mapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.timecapsulearchive.core.domain.capsule.generic_capsule.data.dto.CapsuleDetailDto;
import site.timecapsulearchive.core.domain.capsule.generic_capsule.data.dto.CapsuleSummaryDto;
import site.timecapsulearchive.core.domain.capsule.generic_capsule.data.response.CapsuleDetailResponse;
import site.timecapsulearchive.core.domain.capsule.generic_capsule.data.response.CapsuleSummaryResponse;
import site.timecapsulearchive.core.domain.capsule.secret_capsule.data.dto.MySecreteCapsuleDto;
import site.timecapsulearchive.core.domain.capsule.secret_capsule.data.response.MySecretCapsuleSliceResponse;
import site.timecapsulearchive.core.domain.capsule.secret_capsule.data.response.MySecreteCapsuleResponse;
import site.timecapsulearchive.core.infra.s3.data.dto.S3PreSignedUrlDto;
import site.timecapsulearchive.core.infra.s3.data.request.S3PreSignedUrlRequestDto;
import site.timecapsulearchive.core.infra.s3.manager.S3PreSignedUrlManager;

@Component
@RequiredArgsConstructor
public class CapsuleMapper {

    private static final String DELIMITER = ",";
    private static final ZoneId ASIA_SEOUL = ZoneId.of("Asia/Seoul");
    private final S3PreSignedUrlManager s3PreSignedUrlManager;

    private ZonedDateTime checkNullable(final ZonedDateTime zonedDateTime) {
        if (zonedDateTime != null) {
            return zonedDateTime.withZoneSameInstant(ASIA_SEOUL);
        }
        return null;
    }

    public CapsuleSummaryResponse capsuleSummaryDtoToResponse(
        final CapsuleSummaryDto dto) {
        return CapsuleSummaryResponse.builder()
            .nickname(dto.nickname())
            .profileUrl(dto.profileUrl())
            .skinUrl(s3PreSignedUrlManager.getS3PreSignedUrlForGet(dto.skinUrl()))
            .title(dto.title())
            .dueDate(checkNullable(dto.dueDate()))
            .address(dto.address())
            .roadName(dto.roadName())
            .isOpened(dto.isOpened())
            .createdAt(dto.createdAt().withZoneSameInstant(ASIA_SEOUL))
            .build();
    }

    public CapsuleDetailResponse capsuleDetailDtoToResponse(
        final CapsuleDetailDto detailDto
    ) {
        final S3PreSignedUrlDto preSignedUrls = s3PreSignedUrlManager.getS3PreSignedUrlsForGet(
            S3PreSignedUrlRequestDto.forGet(
                splitFileNames(detailDto.images()),
                splitFileNames(detailDto.videos())
            )
        );

        return CapsuleDetailResponse.builder()
            .capsuleSkinUrl(
                s3PreSignedUrlManager.getS3PreSignedUrlForGet(detailDto.capsuleSkinUrl()))
            .dueDate(checkNullable(detailDto.dueDate()))
            .nickname(detailDto.nickname())
            .profileUrl(detailDto.profileUrl())
            .createdDate(detailDto.createdAt().withZoneSameInstant(ASIA_SEOUL))
            .address(detailDto.address())
            .roadName(detailDto.roadName())
            .title(detailDto.title())
            .content(detailDto.content())
            .imageUrls(preSignedUrls.preSignedImageUrls())
            .videoUrls(preSignedUrls.preSignedVideoUrls())
            .isOpened(detailDto.isOpened())
            .capsuleType(detailDto.capsuleType())
            .build();
    }

    private List<String> splitFileNames(String fileNames) {
        if (fileNames == null) {
            return Collections.emptyList();
        }

        return List.of(fileNames.split(DELIMITER));
    }

    public CapsuleDetailResponse notOpenedCapsuleDetailDtoToResponse(
        final CapsuleDetailDto detailDto
    ) {
        return CapsuleDetailResponse.builder()
            .capsuleSkinUrl(
                s3PreSignedUrlManager.getS3PreSignedUrlForGet(detailDto.capsuleSkinUrl()))
            .dueDate(checkNullable(detailDto.dueDate()))
            .nickname(detailDto.nickname())
            .profileUrl(detailDto.profileUrl())
            .createdDate(detailDto.createdAt().withZoneSameInstant(ASIA_SEOUL))
            .address(detailDto.address())
            .roadName(detailDto.roadName())
            .title("")
            .content("")
            .imageUrls(Collections.emptyList())
            .videoUrls(Collections.emptyList())
            .isOpened(detailDto.isOpened())
            .capsuleType(detailDto.capsuleType())
            .build();
    }

    public MySecretCapsuleSliceResponse mySecretCapsuleDetailSliceToResponse(
        final List<MySecreteCapsuleDto> content,
        final boolean hasNext
    ) {
        List<MySecreteCapsuleResponse> responses = content.stream()
            .map(this::mySecreteCapsuleDtoToResponse)
            .toList();

        return new MySecretCapsuleSliceResponse(
            responses,
            hasNext
        );
    }

    private MySecreteCapsuleResponse mySecreteCapsuleDtoToResponse(final MySecreteCapsuleDto dto) {
        return MySecreteCapsuleResponse.builder()
            .capsuleId(dto.capsuleId())
            .SkinUrl(s3PreSignedUrlManager.getS3PreSignedUrlForGet(dto.skinUrl()))
            .dueDate(dto.dueDate())
            .createdAt(dto.createdAt())
            .title(dto.title())
            .isOpened(dto.isOpened())
            .type(dto.type())
            .build();
    }

}
