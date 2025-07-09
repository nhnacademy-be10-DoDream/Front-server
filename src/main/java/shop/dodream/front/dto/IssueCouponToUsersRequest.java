package shop.dodream.front.dto;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IssueCouponToUsersRequest {

    @NotNull
    private Long couponId;

    private List<String> userIds;

    private UserSearchCondition condition;
}
