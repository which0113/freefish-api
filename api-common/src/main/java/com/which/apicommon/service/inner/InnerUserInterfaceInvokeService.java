package com.which.apicommon.service.inner;

/**
 * @author which
 */
public interface InnerUserInterfaceInvokeService {

    /**
     * 援引
     * 接口调用
     *
     * @param interfaceInfoId 接口信息id
     * @param userId          用户id
     * @param reduceScore     降低分数
     * @return boolean
     */
    boolean invoke(Long interfaceInfoId, Long userId, Integer reduceScore);
}
