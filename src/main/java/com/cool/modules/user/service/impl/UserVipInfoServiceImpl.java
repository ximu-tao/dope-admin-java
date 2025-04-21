package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.user.entity.UserVipInfoEntity;
import com.cool.modules.user.mapper.UserVipInfoMapper;
import com.cool.modules.user.service.UserVipInfoService;
import org.springframework.stereotype.Service;

/**
 * 会员开通配置
 */
@Service
public class UserVipInfoServiceImpl extends BaseServiceImpl<UserVipInfoMapper, UserVipInfoEntity> implements UserVipInfoService {
}