package com.which.api.service.inner.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.which.api.service.InterfaceInfoService;
import com.which.apicommon.model.entity.InterfaceInfo;
import com.which.apicommon.service.inner.InnerInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author which
 */
@DubboService
@Slf4j
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        // 如果带参数，去除第一个？和之后后的参数
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        if (path.startsWith("http://")) {
            path = path.substring(7);
        }
        if (path.startsWith("https://")) {
            path = path.substring(8);
        }
        log.info("【查询地址】：" + path);
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceInfo::getMethod, method);
        lambdaQueryWrapper.like(InterfaceInfo::getUrl, path);
        return interfaceInfoService.getOne(lambdaQueryWrapper);
    }

}
