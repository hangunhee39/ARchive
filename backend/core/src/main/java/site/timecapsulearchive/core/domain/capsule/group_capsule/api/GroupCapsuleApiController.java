package site.timecapsulearchive.core.domain.capsule.group_capsule.api;

import jakarta.validation.Valid;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.timecapsulearchive.core.domain.capsule.group_capsule.data.dto.MyGroupCapsuleDto;
import site.timecapsulearchive.core.domain.capsule.group_capsule.data.reqeust.GroupCapsuleCreateRequest;
import site.timecapsulearchive.core.domain.capsule.group_capsule.data.reqeust.GroupCapsuleUpdateRequest;
import site.timecapsulearchive.core.domain.capsule.group_capsule.data.response.GroupCapsuleDetailResponse;
import site.timecapsulearchive.core.domain.capsule.group_capsule.data.response.GroupCapsulePageResponse;
import site.timecapsulearchive.core.domain.capsule.group_capsule.data.response.MyGroupCapsuleSliceResponse;
import site.timecapsulearchive.core.domain.capsule.group_capsule.data.response.GroupCapsuleSummaryResponse;
import site.timecapsulearchive.core.domain.capsule.group_capsule.facade.GroupCapsuleFacade;
import site.timecapsulearchive.core.domain.capsule.group_capsule.service.GroupCapsuleService;
import site.timecapsulearchive.core.global.common.response.ApiSpec;
import site.timecapsulearchive.core.global.common.response.SuccessCode;
import site.timecapsulearchive.core.infra.s3.manager.S3PreSignedUrlManager;

@RestController
@RequestMapping("/groups-capsules")
@RequiredArgsConstructor
public class GroupCapsuleApiController implements GroupCapsuleApi {

    private final GroupCapsuleFacade groupCapsuleFacade;
    private final GroupCapsuleService groupCapsuleService;
    private final S3PreSignedUrlManager s3PreSignedUrlManager;

    @PostMapping(
        value = "/{group_id}",
        consumes = {"application/json"}
    )
    @Override
    public ResponseEntity<ApiSpec<String>> createGroupCapsule(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("group_id") Long groupId,
        @Valid @RequestBody GroupCapsuleCreateRequest request
    ) {
        groupCapsuleFacade.saveGroupCapsule(request.toGroupCapsuleCreateRequestDto(),
            memberId, groupId);

        return ResponseEntity.ok(
            ApiSpec.empty(SuccessCode.SUCCESS)
        );
    }

    @Override
    public ResponseEntity<GroupCapsuleDetailResponse> findGroupCapsuleByIdAndGroupId(Long groupId,
        Long capsuleId) {
        return null;
    }

    @Override
    public ResponseEntity<GroupCapsulePageResponse> getGroupCapsules(Long groupId, Long size,
        Long capsuleId) {
        return null;
    }

    @GetMapping(value = "/my", produces = {"application/json"})
    @Override
    public ResponseEntity<ApiSpec<MyGroupCapsuleSliceResponse>> getMyGroupCapsules(
        @AuthenticationPrincipal Long memberId,
        @RequestParam(defaultValue = "20", value = "size") final int size,
        @RequestParam(value = "created_at") final ZonedDateTime createdAt
    ) {
        final Slice<MyGroupCapsuleDto> groupCapsules = groupCapsuleService.findMyGroupCapsuleSlice(
            memberId,
            size,
            createdAt
        );

        return ResponseEntity.ok(
            ApiSpec.success(
                SuccessCode.SUCCESS,
                MyGroupCapsuleSliceResponse.createOf(
                    groupCapsules.getContent(),
                    groupCapsules.hasNext(),
                    s3PreSignedUrlManager::getS3PreSignedUrlForGet
                )
            )
        );
    }

    @Override
    public ResponseEntity<GroupCapsuleSummaryResponse> updateGroupCapsuleByIdAndGroupId(
        Long groupId, Long capsuleId, GroupCapsuleUpdateRequest request) {
        return null;
    }
}
