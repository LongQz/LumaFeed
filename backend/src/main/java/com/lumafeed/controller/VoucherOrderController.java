package com.lumafeed.controller;


import com.lumafeed.dto.Result;
import com.lumafeed.dto.UserDTO;
import com.lumafeed.service.IVoucherOrderService;
import com.lumafeed.service.RedisRateLimitService;
import com.lumafeed.utils.UserHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qzh
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {
    @Resource
    private IVoucherOrderService voucherOrderService;
    @Resource
    private RedisRateLimitService rateLimitService;

    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId, HttpServletRequest request) {
        UserDTO user = UserHolder.getUser();
        String subject = user == null ? request.getRemoteAddr() : user.getId().toString();
        if (!rateLimitService.tryAcquire("seckill:" + subject, 30, 60)) {
            return Result.fail("秒杀请求过于频繁，请稍后重试");
        }
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
