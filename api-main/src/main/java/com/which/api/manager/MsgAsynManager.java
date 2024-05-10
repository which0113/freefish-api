package com.which.api.manager;

import cn.hutool.json.JSONUtil;
import com.which.api.model.entity.Chart;
import com.which.api.model.enums.ChartStatusEnum;
import com.which.api.service.ChartService;
import com.which.api.ws.WebSocketNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 消息异步处理
 *
 * @author which
 * @date 2024/05/10
 */
@Component
@Slf4j
public class MsgAsynManager {

    private static String userIdStr;
    @Resource
    private ChartService chartService;
    @Resource
    private AiManager aiManager;
    @Resource
    private WebSocketNotificationService notificationService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    public void handleMessage(Long chartId) {
        // 提交消息处理任务给 ThreadPoolExecutor 异步执行
        threadPoolExecutor.execute(() -> {
            try {
                processMessage(chartId);
            } catch (Exception e) {
                log.error("消息处理失败：", e);
            }
        });
    }

    private void processMessage(Long chartId) {
        // 更新 chartStatus 为 running
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setChartStatus(ChartStatusEnum.RUNNING.getValue());
        boolean update = chartService.updateById(updateChart);
        if (!update) {
            handleUpdateChartError(chartId, "图表状态更新失败");
            return;
        }

        // 调用AI
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            handleUpdateChartError(chartId, "图表为空");
            return;
        }
        userIdStr = chart.getUserId().toString();
        String chartResult;
        try {
            chartResult = aiManager.doChatByGpt(buildUserInput(chart));
        } catch (Exception e) {
            handleUpdateChartError(chartId, "AI 生成错误");
            return;
        }
        String[] splits = chartResult.split("【【【【【");
        if (splits.length < 3) {
            handleUpdateChartError(chartId, "AI 生成错误");
            return;
        }
        // 去掉换行、空格和 json 纠错等
        // 使用正则表达式匹配并替换单引号
        String genChart = splits[1].trim().replaceAll("(?<!\\\\)'", "\"");
        try {
            JSONUtil.parse(genChart);
        } catch (Exception e) {
            handleUpdateChartError(chartId, "AI 生成 json 错误");
            return;
        }
        String genResult = splits[2].trim();
        // 更新 chartStatus 为 succeed
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setChartStatus(ChartStatusEnum.SUCCEED.getValue());
        boolean update2 = chartService.updateById(updateChartResult);
        if (!update2) {
            handleUpdateChartError(chartId, "图表更新失败");
            return;
        }
        notificationService.sendNotification(userIdStr, "处理成功");
    }

    private String buildUserInput(Chart chart) {
        String userGoal = chart.getGoal();
        String csvData = chart.getChartData();
        return "分析需求：" + "\n" +
                // 拼接分析目标
                userGoal + "\n" +
                "原始数据：" + "\n" +
                // 压缩后的数据
                csvData + "\n";
    }

    private void handleUpdateChartError(Long chartId, String execMessage) {
        // 更新图表状态为失败
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setChartStatus(ChartStatusEnum.FAILED.getValue());
        updateChart.setExecMessage(execMessage);
        boolean update = chartService.updateById(updateChart);
        if (!update) {
            log.error("数据库异常");
        }
        notificationService.sendNotification(userIdStr, "处理失败");
    }
}
