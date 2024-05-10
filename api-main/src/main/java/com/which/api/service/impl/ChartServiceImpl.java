package com.which.api.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.which.api.exception.ThrowUtils;
import com.which.api.manager.MsgAsynManager;
import com.which.api.manager.RedissonManager;
import com.which.api.mapper.ChartMapper;
import com.which.api.model.dto.chart.GenChartByAiRequest;
import com.which.api.model.entity.Chart;
import com.which.api.model.entity.User;
import com.which.api.model.enums.ChartStatusEnum;
import com.which.api.model.vo.BiVO;
import com.which.api.service.ChartService;
import com.which.api.service.UserService;
import com.which.api.utils.ExcelUtils;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import com.which.apicommon.model.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.which.api.constant.CommonConstant.*;
import static com.which.api.constant.RedisConstant.GEN_CHART_BY_AI;
import static com.which.api.constant.RedisConstant.RATE_LIMIT_KEY;

/**
 * @author which
 * @description 针对表【chart(图表信息表)】的数据库操作Service实现
 * @createDate 2023-11-27 20:12:12
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService {

    @Resource
    private RedissonManager redissonManager;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private MsgAsynManager msgAsynManager;

    @Override
    public BiVO genChartByAi(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();

        if (loginUser.getBalance() <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
        }

        // 限流
        redissonManager.doRateLimit(RATE_LIMIT_KEY + "genChartByAi:" + userId);
        // 校验参数 genChartByAiRequest
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 200,
                ErrorCode.PARAMS_ERROR, "名称过长");
        ThrowUtils.throwIf(StringUtils.isBlank(chartType), ErrorCode.PARAMS_ERROR, "类型为空");
        // 校验文件 multipartFile
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        ThrowUtils.throwIf(!VALID_SUFFIX.contains(suffix), ErrorCode.PARAMS_ERROR, "非法文件后缀");
        long fileSize = multipartFile.getSize();
        ThrowUtils.throwIf(fileSize > TEN_MB, ErrorCode.PARAMS_ERROR, "文件最大10MB");

        // 扣除积分
        String redissonLock = (GEN_CHART_BY_AI + "genChartByAi:" + loginUser.getUserAccount()).intern();
        redissonManager.redissonDistributedLocks(redissonLock, () -> {
            User user = userService.getById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "账号不存在");
            }
            if (user.getBalance() < NEED_BALANCE) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
            }
            boolean update = userService.reduceWalletBalance(userId, NEED_BALANCE);
            if (!update) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI分析失败");
            }
            return null;
        }, "AI分析失败");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        // 压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        ThrowUtils.throwIf(csvData.length() > TREE_KB, ErrorCode.PARAMS_ERROR, "文件数据超出限制");

        // 保存数据到数据库
        Chart chart = new Chart();
        chart.setGoal(userGoal);
        chart.setChartData(csvData);
        chart.setName(name);
        chart.setChartType(chartType);
        // 更新 chartStatus 为 wait
        chart.setChartStatus(ChartStatusEnum.WAIT.getValue());
        chart.setUserId(userId);
        boolean saveResult = this.save(chart);
        if (!saveResult) {
            log.error("数据库异常");
        }

        // AI分析
        Long chartId = chart.getId();
        // 异步处理
        msgAsynManager.handleMessage(chartId);

        BiVO biVO = new BiVO();
        biVO.setChartId(chartId);
        return biVO;
    }

}




