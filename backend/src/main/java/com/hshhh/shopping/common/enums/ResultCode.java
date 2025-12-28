package com.hshhh.shopping.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILURE(400, "业务异常"),
    UNAUTHORIZED(401, "未登录或Token过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    
    USER_HAS_EXISTED(2001, "用户已存在"),
    USER_NOT_EXIST(2002, "用户不存在"),
    PASSWORD_ERROR(2003, "密码错误"),
    
    PRODUCT_NOT_EXIST(3001, "商品不存在"),
    STOCK_NOT_ENOUGH(3002, "库存不足");

    private final int code;
    private final String message;
}
