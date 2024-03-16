package site.timecapsulearchive.core.infra.notification.data.dto.request;

import lombok.Builder;

@Builder
public record FriendReqNotificationRequest(
    Long friendId,
    String status,
    String title,
    String text
) {

}
