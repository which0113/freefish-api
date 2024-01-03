package com.which.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.which.apicommon.model.entity.InterfaceInfo;

/**
 * @author which
 * @description 针对表【interface_info(接口信息表)】的数据库操作Service
 * @createDate 2023-12-26 20:34:05
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验
     *
     * @param add           是否为创建校验
     * @param interfaceInfo 接口信息
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 更新总调用数
     *
     * @param interfaceId 接口id
     * @return boolean
     */
    boolean updateTotalInvokes(long interfaceId);

}
