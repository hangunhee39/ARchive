package site.timecapsulearchive.core.domain.auth.data.dto;

public record MemberInfo(Long memberId) {

    public static MemberInfo from(Long memberId) {
        return new MemberInfo(memberId);
    }
}
