package com.which.apicommon.service.inner;

import com.which.apicommon.model.entity.InterfaceInfo;

/**
 * @author which
 */
public interface InnerInterfaceInfoService {
    /**
     * 获取接口信息
     *
     * @param path   路径
     * @param method 方法
     * @return {@link InterfaceInfo}
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
