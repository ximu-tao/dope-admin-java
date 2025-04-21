package com.cool.modules.user.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.enums.PayTerminalEnum;
import com.cool.core.enums.PayWayEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.modules.user.entity.UserOauthEntity;
import com.cool.modules.user.mapper.UserOauthMapper;
import com.cool.modules.user.proxy.WxProxy;
import com.cool.modules.user.service.UserOauthService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 第三方绑定信息
 */
@Service
@RequiredArgsConstructor
public class UserOauthServiceImpl extends BaseServiceImpl<UserOauthMapper, UserOauthEntity> implements UserOauthService {
    
    private final WxProxy wxProxy;
    
    @Override
    public UserOauthEntity loginByMini(String code, String encryptedData, String iv){
                // 获取 session
        WxMaJscode2SessionResult result = null;
        try {
            result = wxProxy.getSessionInfo(code);
            // 解密数据
            WxMaUserInfo wxMaUserInfo = wxProxy.getUserInfo(result.getSessionKey(), encryptedData, iv);

            if (ObjUtil.isNotEmpty(wxMaUserInfo)) {

                UserOauthEntity userOauthEntity = new UserOauthEntity();
                userOauthEntity.setProvider( PayWayEnum.WECHAT );
                userOauthEntity.setPlatform( PayTerminalEnum.MP_WECHAT );
                userOauthEntity.setOpenid( result.getOpenid() );
                userOauthEntity.setUnionid( result.getUnionid() );

                Map<String, Object> stringObjectMap = BeanUtil.beanToMap(wxMaUserInfo, true, true);
                
                userOauthEntity.setExtend( stringObjectMap );
                return getBySave( userOauthEntity );
            }
        } catch (WxErrorException e) {
            CoolPreconditions.alwaysThrow(e.getMessage(), e);
        }
        CoolPreconditions.alwaysThrow("获得小程序用户信息");
        return null;
    }
    
    
    
    public UserOauthEntity getBySave( UserOauthEntity entity ){
        System.out.println( entity );
        UserOauthEntity one = this.getOne(
                QueryWrapper.create()
                        .eq(UserOauthEntity::getOpenid, entity.getOpenid())
                        .eq(UserOauthEntity::getProvider, entity.getProvider())
                        .eq(UserOauthEntity::getPlatform, entity.getPlatform())
        );
        if ( ObjUtil.isEmpty(one) ){
            entity.setUserId(0L);
            super.save(entity);
            return entity;
        }
        return one;
    }
    
    
}