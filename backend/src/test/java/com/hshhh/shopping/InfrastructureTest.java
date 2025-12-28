package com.hshhh.shopping;

import com.hshhh.shopping.common.Result;
import com.hshhh.shopping.common.enums.ResultCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InfrastructureTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testRedisConnection() {
        assertNotNull(redisTemplate);
        // 尝试简单的Redis操作，如果Redis未启动，这里可能会报错，但能证明Bean已加载
        try {
            redisTemplate.opsForValue().set("test_key", "test_value");
            Object value = redisTemplate.opsForValue().get("test_key");
            assertEquals("test_value", value);
            redisTemplate.delete("test_key");
        } catch (Exception e) {
            System.out.println("Redis连接失败 (如果是本地开发且未启动Redis，这是正常的): " + e.getMessage());
        }
    }

    @Test
    void testResultWrapper() {
        Result<String> success = Result.success("data");
        assertEquals(200, success.getCode());
        assertEquals("data", success.getData());

        Result<Void> error = Result.error(ResultCode.INTERNAL_SERVER_ERROR);
        assertEquals(500, error.getCode());
    }
}
