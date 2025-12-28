package com.hshhh.shopping.modules.auth.vo;

import com.hshhh.shopping.modules.user.vo.UserVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录结果")
public class LoginResultVO {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "过期时间(秒)")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserVO user;
}
