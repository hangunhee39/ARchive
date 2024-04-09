package site.timecapsulearchive.core.domain.capsule.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.timecapsulearchive.core.domain.capsule.exception.CapsuleNotFondException;
import site.timecapsulearchive.core.domain.capsule.generic_capsule.data.dto.CapsuleDetailDto;
import site.timecapsulearchive.core.domain.capsule.generic_capsule.data.dto.CapsuleSummaryDto;
import site.timecapsulearchive.core.domain.capsule.mapper.CapsuleMapper;
import site.timecapsulearchive.core.domain.capsule.public_capsule.data.response.PublicCapsuleSliceResponse;
import site.timecapsulearchive.core.domain.capsule.repository.PublicCapsuleQueryRepository;
import site.timecapsulearchive.core.domain.capsule.generic_capsule.data.response.CapsuleDetailResponse;
import site.timecapsulearchive.core.domain.capsule.generic_capsule.data.response.CapsuleSummaryResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicCapsuleService {

    private final PublicCapsuleQueryRepository publicCapsuleQueryRepository;
    private final CapsuleMapper capsuleMapper;

    public CapsuleDetailResponse findPublicCapsuleDetailByMemberIdAndCapsuleId(Long memberId,
        Long capsuleId) {
        CapsuleDetailDto detailDto = publicCapsuleQueryRepository.findPublicCapsuleDetailDtosByMemberIdAndCapsuleId(
                memberId, capsuleId)
            .orElseThrow(CapsuleNotFondException::new);

        if (capsuleNotOpened(detailDto)) {
            return capsuleMapper.notOpenedCapsuleDetailDtoToResponse(detailDto);
        }

        return capsuleMapper.capsuleDetailDtoToResponse(detailDto);
    }

    private boolean capsuleNotOpened(final CapsuleDetailDto detailDto) {
        if (detailDto.dueDate() == null) {
            return false;
        }

        return !detailDto.isOpened() || detailDto.dueDate()
            .isAfter(ZonedDateTime.now(ZoneOffset.UTC));
    }

    public CapsuleSummaryResponse findPublicCapsuleSummaryByMemberIdAndCapsuleId(
        final Long memberId,
        final Long capsuleId
    ) {
        CapsuleSummaryDto summaryDto = publicCapsuleQueryRepository.findPublicCapsuleSummaryDtosByMemberIdAndCapsuleId(
                memberId, capsuleId)
            .orElseThrow(CapsuleNotFondException::new);

        return capsuleMapper.capsuleSummaryDtoToResponse(summaryDto);
    }

    public PublicCapsuleSliceResponse findPublicCapsulesMadeByFriend(
        final Long memberId,
        final int size,
        final ZonedDateTime createdAt
    ) {
        final Slice<CapsuleDetailDto> publicCapsuleDetailSlice = publicCapsuleQueryRepository.findPublicCapsulesDtoMadeByFriend(
            memberId, size, createdAt);

        return capsuleMapper.publicCapsuleDetailSliceToResponse(
            publicCapsuleDetailSlice.getContent(), publicCapsuleDetailSlice.hasNext());
    }
}
