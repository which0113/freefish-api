package com.which.apicommon.service.inner;

import com.which.apicommon.model.vo.UserVO;

/**
 * @author which
 */
public interface InnerUserService {

    /**
     * 通过访问密钥获取invoke用户
     *
     * @param accessKey 访问密钥
     * @return
     */
    UserVO getInvokeUserByAccessKey(String accessKey);

}
