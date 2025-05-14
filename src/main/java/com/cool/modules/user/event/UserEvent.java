package com.cool.modules.user.event;


import com.cool.core.event.Subscribe;
import com.cool.modules.user.entity.UserInfoEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEvent {

    @Subscribe(value = "user.delete", eventType = Long.class)
    public void handleUserDelete(Long id) {
        System.out.println("用户注销: " + id);
    }
    
    @Subscribe(value = "user.login", eventType = UserInfoEntity.class)
    public void handleOrderLogin( UserInfoEntity entity ) {
        System.out.println("用户登陆: " + entity);
    }
    
    @Subscribe(value = "user.register", eventType = UserInfoEntity.class)
    public void handleUserRegister(UserInfoEntity entity) {
        System.out.println("用户注册: " + entity);
    }    
    @Subscribe(value = "user.update", eventType = UserInfoEntity.class)
    public void handleUserUpdate(UserInfoEntity entity) {
        System.out.println("修改用户资料: " + entity);
    }
}