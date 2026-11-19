package com.lumafeed.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lumafeed.entity.OpsTask;
import com.lumafeed.mapper.OpsTaskMapper;
import com.lumafeed.service.IOpsTaskService;
import org.springframework.stereotype.Service;

/**
 * Operations task persistence service.
 *
 * @author qzh
 */
@Service
public class OpsTaskServiceImpl extends ServiceImpl<OpsTaskMapper, OpsTask> implements IOpsTaskService {
}
