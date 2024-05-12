package com.which.api.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.which.api.annotation.AuthCheck;
import com.which.api.exception.ThrowUtils;
import com.which.api.manager.RedissonManager;
import com.which.api.service.InterfaceInfoService;
import com.which.api.service.UserService;
import com.which.apicommon.common.*;
import com.which.apicommon.model.dto.interfaceinfo.*;
import com.which.apicommon.model.emums.InterfaceStatusEnum;
import com.which.apicommon.model.entity.InterfaceInfo;
import com.which.apicommon.model.vo.InterfaceInfoVO;
import com.which.apicommon.model.vo.UserVO;
import com.which.apisdk.client.ApiClient;
import com.which.apisdk.model.request.CurrencyRequest;
import com.which.apisdk.model.response.ResultResponse;
import com.which.apisdk.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.which.apicommon.constant.CommonConstant.SORT_ORDER_ASC;
import static com.which.apicommon.constant.RedisConstant.RATE_LIMIT_KEY;
import static com.which.apicommon.constant.UserConstant.ADMIN_ROLE;
import static com.which.apicommon.constant.UserConstant.DEMO_ROLE;

/**
 * api信息接口
 *
 * @author which
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private Gson gson;

    @Resource
    private ApiService apiService;

    @Resource
    private RedissonManager redissonManager;

    /**
     * 添加接口信息
     * 创建
     *
     * @param interfaceInfoAddRequest 接口信息添加请求
     * @param request                 请求
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String name = interfaceInfoAddRequest.getName();
        String url = interfaceInfoAddRequest.getUrl();
        String method = interfaceInfoAddRequest.getMethod();
        Integer reduceScore = interfaceInfoAddRequest.getReduceScore();
        if (StringUtils.isAnyBlank(name, url, method) || reduceScore <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        InterfaceInfo interfaceInfo = new InterfaceInfo();
        if (CollectionUtils.isNotEmpty(interfaceInfoAddRequest.getRequestParams())) {
            List<RequestParamsField> requestParamsFields = interfaceInfoAddRequest.getRequestParams().stream()
                    .filter(field -> StringUtils.isNotBlank(field.getFieldName()))
                    .collect(Collectors.toList());
            String requestParams = JSONUtil.toJsonStr(requestParamsFields);
            interfaceInfo.setRequestParams(requestParams);
        }
        if (CollectionUtils.isNotEmpty(interfaceInfoAddRequest.getResponseParams())) {
            List<ResponseParamsField> responseParamsFields = interfaceInfoAddRequest.getResponseParams().stream()
                    .filter(field -> StringUtils.isNotBlank(field.getFieldName()))
                    .collect(Collectors.toList());
            String responseParams = JSONUtil.toJsonStr(responseParamsFields);
            interfaceInfo.setResponseParams(responseParams);
        }
        BeanUtil.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        UserVO loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除接口信息
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(deleteRequest, deleteRequest.getId()) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新接口信息
     *
     * @param interfaceInfoUpdateRequest 接口信息更新请求
     * @param request                    请求
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        String name = interfaceInfoUpdateRequest.getName();
        String url = interfaceInfoUpdateRequest.getUrl();
        String method = interfaceInfoUpdateRequest.getMethod();
        Integer reduceScore = interfaceInfoUpdateRequest.getReduceScore();
        if (StringUtils.isAnyBlank(name, url, method) || reduceScore <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        InterfaceInfo interfaceInfo = new InterfaceInfo();
        if (CollectionUtils.isNotEmpty(interfaceInfoUpdateRequest.getRequestParams())) {
            List<RequestParamsField> requestParamsFields = interfaceInfoUpdateRequest.getRequestParams().stream().filter(field -> StringUtils.isNotBlank(field.getFieldName())).collect(Collectors.toList());
            String requestParams = JSONUtil.toJsonStr(requestParamsFields);
            interfaceInfo.setRequestParams(requestParams);
        }
        if (CollectionUtils.isNotEmpty(interfaceInfoUpdateRequest.getResponseParams())) {
            List<ResponseParamsField> responseParamsFields = interfaceInfoUpdateRequest.getResponseParams().stream().filter(field -> StringUtils.isNotBlank(field.getFieldName())).collect(Collectors.toList());
            String responseParams = JSONUtil.toJsonStr(responseParamsFields);
            interfaceInfo.setResponseParams(responseParams);
        }
        BeanUtil.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        UserVO user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!userService.isAdmin(request) && !oldInterfaceInfo.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 通过id获取接口信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfoVO> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        BeanUtil.copyProperties(interfaceInfo, interfaceInfoVO);
        return ResultUtils.success(interfaceInfoVO);
    }

    /**
     * 分页获取数据
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(anyRole = {ADMIN_ROLE, DEMO_ROLE})
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        interfaceInfoQueryRequest.setSortField("totalInvokes");
        return this.getPageBaseResponse(interfaceInfoQueryRequest, true);
    }

    /**
     * 分页获取当前用户的数据
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/search/list/page")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoBySearchPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 限制爬虫
        ThrowUtils.throwIf(interfaceInfoQueryRequest.getPageSize() > 20, ErrorCode.PARAMS_ERROR);
        interfaceInfoQueryRequest.setStatus(InterfaceStatusEnum.ONLINE.getValue());
        interfaceInfoQueryRequest.setSortField("totalInvokes");
        return this.getPageBaseResponse(interfaceInfoQueryRequest, false);
    }

    /**
     * 获取 PageVO BaseResponse
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @NotNull
    private BaseResponse<Page<InterfaceInfoVO>> getPageBaseResponse(InterfaceInfoQueryRequest interfaceInfoQueryRequest, boolean isAdminPage) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                this.getQueryWrapper(interfaceInfoQueryRequest, isAdminPage));
        Page<InterfaceInfoVO> interfaceInfoVoPage = new PageDTO<>(interfaceInfoPage.getCurrent(), interfaceInfoPage.getSize(), interfaceInfoPage.getTotal());
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoPage.getRecords().stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtil.copyProperties(interfaceInfo, interfaceInfoVO);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        interfaceInfoVoPage.setRecords(interfaceInfoVOList);
        return ResultUtils.success(interfaceInfoVoPage);
    }

    /**
     * 获取查询包装类
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    private QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest, boolean isAdminPage) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceInfoQueryRequest == null) {
            return queryWrapper;
        }

        String name = interfaceInfoQueryRequest.getName();
        String returnFormat = interfaceInfoQueryRequest.getReturnFormat();
        String url = interfaceInfoQueryRequest.getUrl();
        Long userId = interfaceInfoQueryRequest.getUserId();
        Integer reduceScore = interfaceInfoQueryRequest.getReduceScore();
        String method = interfaceInfoQueryRequest.getMethod();
        String description = interfaceInfoQueryRequest.getDescription();
        Integer status = interfaceInfoQueryRequest.getStatus();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        // 拼接查询条件
        boolean nameNotBlank = StringUtils.isNotBlank(name);
        boolean descriptionNotBlank = StringUtils.isNotBlank(description);
        if (isAdminPage) {
            // 管理界面查询使用 and 操作
            queryWrapper.like(nameNotBlank, "name", name)
                    .like(descriptionNotBlank, "description", description);
        } else {
            queryWrapper.and(nameNotBlank || descriptionNotBlank, qw -> qw
                    .like(nameNotBlank, "name", name)
                    .or().like(descriptionNotBlank, "description", description));
        }
        queryWrapper.like(StringUtils.isNotBlank(url), "url", url)
                .eq(StringUtils.isNotBlank(returnFormat), "returnFormat", returnFormat)
                .eq(StringUtils.isNotBlank(method), "method", method)
                .eq(ObjectUtils.isNotEmpty(status), "status", status)
                .eq(ObjectUtils.isNotEmpty(reduceScore), "reduceScore", reduceScore)
                .eq(ObjectUtils.isNotEmpty(userId), "userId", userId)
                .orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(SORT_ORDER_ASC), sortField)
                .orderBy(true, false, "updateTime")
                .select("id",
                        "name",
                        "description",
                        "status",
                        "totalInvokes",
                        "avatarUrl",
                        "method",
                        "url",
                        "reduceScore",
                        "userId",
                        "updateTime"
                );
        return queryWrapper;
    }

    /**
     * 下线
     *
     * @param idRequest id请求
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        interfaceInfo.setStatus(InterfaceStatusEnum.OFFLINE.getValue());
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }

    /**
     * 上线
     *
     * @param idRequest id请求
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        interfaceInfo.setStatus(InterfaceStatusEnum.ONLINE.getValue());
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }

    /**
     * 调用接口
     *
     * @param invokeRequest id请求
     * @param request       请求
     * @return
     */
    @PostMapping("/invoke")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Object> invokeInterface(@RequestBody InvokeRequest invokeRequest, HttpServletRequest request) {
        if (invokeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long interfaceInfoId = invokeRequest.getId();
        if (interfaceInfoId == null || interfaceInfoId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 限流
        UserVO loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        redissonManager.doRateLimit(RATE_LIMIT_KEY + "invokeInterface:" + userId);
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceInfoId);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (interfaceInfo.getStatus() != InterfaceStatusEnum.ONLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口未开启");
        }
        // 构建请求参数
        List<InvokeRequest.Field> fieldList = invokeRequest.getRequestParams();
        String requestParams = "{}";
        if (fieldList != null && fieldList.size() > 0) {
            JsonObject jsonObject = new JsonObject();
            for (InvokeRequest.Field field : fieldList) {
                jsonObject.addProperty(field.getFieldName(), field.getValue());
            }
            requestParams = gson.toJson(jsonObject);
        }
        Map<String, Object> params = new Gson().fromJson(requestParams, new TypeToken<Map<String, Object>>() {
        }.getType());
        // 调用接口
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        try {
            ApiClient apiClient = new ApiClient(accessKey, secretKey);
            CurrencyRequest currencyRequest = new CurrencyRequest();
            currencyRequest.setMethod(interfaceInfo.getMethod());
            currencyRequest.setPath(interfaceInfo.getUrl());
            currencyRequest.setRequestParams(params);
            ResultResponse response = apiService.request(apiClient, currencyRequest);
            return ResultUtils.success(response.getData());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * 更新接口图片
     *
     * @param interfaceInfoUpdateAvatarRequest
     * @return
     */
    @PostMapping("/updateInterfaceInfoAvatar")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfoAvatarUrl(@RequestBody InterfaceInfoUpdateAvatarRequest interfaceInfoUpdateAvatarRequest) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "开发中");
    }

}
