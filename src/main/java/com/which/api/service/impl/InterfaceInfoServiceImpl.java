package com.which.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.which.api.common.ErrorCode;
import com.which.api.exception.BusinessException;
import com.which.api.mapper.InterfaceInfoMapper;
import com.which.api.model.entity.InterfaceInfo;
import com.which.api.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author which
 * @description 针对表【interface_info(接口信息表)】的数据库操作Service实现
 * @createDate 2023-12-26 20:34:05
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();

        String description = interfaceInfo.getDescription();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, url, method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(method)) {
            interfaceInfo.setMethod(method.trim().toUpperCase());
        }
        if (StringUtils.isNotBlank(url)) {
            interfaceInfo.setUrl(url.trim());
        }
        if (StringUtils.isNotBlank(name) && name.length() > 60) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称过长");
        }

        if (StringUtils.isNotBlank(description) && description.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口描述过长");
        }
    }

}




