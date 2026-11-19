package com.lumafeed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lumafeed.dto.LoginFormDTO;
import com.lumafeed.dto.Result;
import com.lumafeed.entity.User;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qzh
 * @since 2021-12-22
 */
public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result sign();

    Result signCount();
}
