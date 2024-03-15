package site.timecapsulearchive.core.domain.friend.data.reqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "전화번호 리스트로 친구 찾기 요청")
public record SearchFriendsRequest(

    @Schema(description = "전화번호 리스트")
    List<String> phones
) {

}