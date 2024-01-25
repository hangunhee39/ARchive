package site.timecapsulearchive.core.domain.capsule.api.secret_c;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.timecapsulearchive.core.domain.capsule.dto.secret_c.mapper.CapsuleMapper;
import site.timecapsulearchive.core.domain.capsule.dto.secret_c.reqeust.SecretCapsuleCreateRequest;
import site.timecapsulearchive.core.domain.capsule.dto.secret_c.reqeust.SecretCapsuleUpdateRequest;
import site.timecapsulearchive.core.domain.capsule.dto.secret_c.response.SecretCapsuleDetailResponse;
import site.timecapsulearchive.core.domain.capsule.dto.secret_c.response.SecretCapsulePageResponse;
import site.timecapsulearchive.core.domain.capsule.dto.secret_c.response.SecretCapsuleSummaryResponse;
import site.timecapsulearchive.core.domain.capsule.service.SecreteCapsuleService;
import site.timecapsulearchive.core.global.common.response.ApiSpec;
import site.timecapsulearchive.core.global.common.response.SuccessCode;

@RestController
@RequestMapping("/secrete")
@RequiredArgsConstructor
public class SecretCapsuleApiController implements SecretCapsuleApi {

    private final SecreteCapsuleService secreteCapsuleService;
    private final CapsuleMapper mapper;

    @Override
    public ResponseEntity<ApiSpec<Void>> createSecretCapsule(
        @AuthenticationPrincipal Long memberId,
        @RequestBody SecretCapsuleCreateRequest request
    ) {
        secreteCapsuleService.createCapsule(
            memberId,
            mapper.secretCapsuleCreateRequestToDto(request)
        );

        return ResponseEntity.ok(
            ApiSpec.empty(
                SuccessCode.SUCCESS
            )
        );
    }

    @Override
    public ResponseEntity<SecretCapsuleDetailResponse> findSecretCapsuleById(Long capsuleId) {
        return null;
    }

    @Override
    public ResponseEntity<SecretCapsulePageResponse> getSecretCapsules(Long size, Long capsuleId) {
        return null;
    }

    @Override
    public ResponseEntity<SecretCapsuleSummaryResponse> updateSecretCapsule(Long capsuleId,
        SecretCapsuleUpdateRequest request) {
        return null;
    }
}