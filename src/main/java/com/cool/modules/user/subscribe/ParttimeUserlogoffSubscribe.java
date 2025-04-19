package com.cool.modules.user.subscribe;

import com.cool.core.annotation.RedisSubscribe;

import java.util.function.Consumer;

@RedisSubscribe(channel = "user-logoff" , clazz = Long.class)
public class ParttimeUserlogoffSubscribe implements Consumer<Long> {
    
    @Override
    public void accept(Long userId) {
//        TODO: 用户注销后，清理用户数据
    }
}
