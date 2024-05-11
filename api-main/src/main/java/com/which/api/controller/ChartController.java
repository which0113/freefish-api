package com.which.api.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.which.api.annotation.AuthCheck;
import com.which.api.common.DeleteRequest;
import com.which.api.exception.ThrowUtils;
import com.which.api.model.dto.chart.ChartAddRequest;
import com.which.api.model.dto.chart.ChartQueryRequest;
import com.which.api.model.dto.chart.ChartUpdateRequest;
import com.which.api.model.dto.chart.GenChartByAiRequest;
import com.which.api.model.entity.Chart;
import com.which.api.model.enums.ChartStatusEnum;
import com.which.api.model.vo.BiVO;
import com.which.api.model.vo.ChartVO;
import com.which.api.service.ChartService;
import com.which.api.service.UserService;
import com.which.api.utils.SqlUtils;
import com.which.apicommon.common.BaseResponse;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import com.which.apicommon.common.ResultUtils;
import com.which.apicommon.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.which.api.constant.CommonConstant.SORT_ORDER_ASC;
import static com.which.api.constant.UserConstant.ADMIN_ROLE;
import static com.which.api.constant.UserConstant.DEMO_ROLE;

/**
 * 图表接口
 *
 * @author which
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtil.copyProperties(chartAddRequest, chart);
        UserVO loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        String chartStatus = oldChart.getChartStatus();
        if (ChartStatusEnum.WAIT.getValue().equals(chartStatus) ||
                ChartStatusEnum.RUNNING.getValue().equals(chartStatus)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请稍等");
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest, HttpServletRequest request) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtil.copyProperties(chartUpdateRequest, chart);
        UserVO loginUser = userService.getLoginUser(request);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<ChartVO> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        ChartVO chartVO = new ChartVO();
        BeanUtil.copyProperties(chart, chartVO);
        return ResultUtils.success(chartVO);
    }

    /**
     * 分页获取数据
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(anyRole = {ADMIN_ROLE, DEMO_ROLE})
    public BaseResponse<Page<ChartVO>> listChartByPage(ChartQueryRequest chartQueryRequest, HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return this.getPageBaseResponse(chartQueryRequest, true);
    }

    /**
     * 分页获取当前用户的数据
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list/page")
    public BaseResponse<Page<ChartVO>> listChartByMyPage(ChartQueryRequest chartQueryRequest, HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 限制爬虫
        ThrowUtils.throwIf(chartQueryRequest.getPageSize() > 20, ErrorCode.PARAMS_ERROR);
        UserVO loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        return this.getPageBaseResponse(chartQueryRequest, false);
    }

    /**
     * 获取 PageVO BaseResponse
     *
     * @param chartQueryRequest
     * @return
     */
    @NotNull
    private BaseResponse<Page<ChartVO>> getPageBaseResponse(ChartQueryRequest chartQueryRequest, boolean isAdminPage) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                this.getQueryWrapper(chartQueryRequest, isAdminPage));
        Page<ChartVO> chartVoPage = new PageDTO<>(chartPage.getCurrent(), chartPage.getSize(), chartPage.getTotal());
        List<ChartVO> chartVOList = chartPage.getRecords().stream().map(chart -> {
            ChartVO chartVO = new ChartVO();
            BeanUtil.copyProperties(chart, chartVO);
            return chartVO;
        }).collect(Collectors.toList());
        chartVoPage.setRecords(chartVOList);
        return ResultUtils.success(chartVoPage);
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest, boolean isAdminPage) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        String goal = chartQueryRequest.getGoal();
        String name = chartQueryRequest.getName();
        String chartType = chartQueryRequest.getChartType();
        String genResult = chartQueryRequest.getGenResult();
        String chartStatus = chartQueryRequest.getChartStatus();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();
        Long userId = chartQueryRequest.getUserId();
        // 拼接查询条件
        boolean goalNotBlank = StringUtils.isNotBlank(goal);
        boolean nameNotBlank = StringUtils.isNotBlank(name);
        boolean genResultNotEmpty = ObjectUtils.isNotEmpty(genResult);
        boolean chartTypeNotEmpty = ObjectUtils.isNotEmpty(chartType);
        if (isAdminPage) {
            // 管理界面查询使用 and 操作
            queryWrapper.like(goalNotBlank, "goal", goal)
                    .like(nameNotBlank, "name", name)
                    .like(genResultNotEmpty, "genResult", genResult)
                    .like(chartTypeNotEmpty, "chartType", chartType);
        } else {
            queryWrapper.and(goalNotBlank || nameNotBlank || genResultNotEmpty || chartTypeNotEmpty, qw -> qw
                    .like(goalNotBlank, "goal", goal)
                    .or().like(nameNotBlank, "name", name)
                    .or().like(genResultNotEmpty, "genResult", genResult)
                    .or().like(chartTypeNotEmpty, "chartType", chartType));
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(chartStatus), "chartStatus", chartStatus)
                .eq(ObjectUtils.isNotEmpty(userId), "userId", userId)
                .orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(SORT_ORDER_ASC), sortField)
                .orderBy(true, false, "updateTime")
                .select("id",
                        "goal",
                        "name",
                        "chartType",
                        "genChart",
                        "genResult",
                        "chartStatus",
                        "execMessage",
                        "userId",
                        "updateTime"
                );
        return queryWrapper;
    }

    /**
     * 智能分析（异步）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async")
    public BaseResponse<BiVO> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                           GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        // 是否登陆
        UserVO loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // AI分析
        BiVO biVO = chartService.genChartByAi(multipartFile, genChartByAiRequest, request);
        ThrowUtils.throwIf(biVO == null, ErrorCode.SYSTEM_ERROR, "AI生成错误");
        return ResultUtils.success(biVO);
    }

}
