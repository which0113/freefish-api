package com.which.api.bizmq;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.which.api.manager.AiManager;
import com.which.api.model.entity.Chart;
import com.which.api.model.enums.ChartStatusEnum;
import com.which.api.service.ChartService;
import com.which.api.ws.WebSocketNotificationService;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import javax.annotation.Resource;

import static com.which.api.constant.MqConstant.*;

/**
 * @author which
 * RabbitMQ 消费者
 */
// @Component
@Slf4j
public class BiMsgConsumer {

    private static String userIdStr;
    @Resource
    private ChartService chartService;
    @Resource
    private AiManager aiManager;
    @Resource
    private WebSocketNotificationService notificationService;

    /**
     * 调用AI分析图表
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = BI_QUEUE, durable = "true"),
            exchange = @Exchange(value = BI_DIRECT_EXCHANGE),
            key = BI_ROUTING_KEY
    ))
    @SneakyThrows
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isBlank(message)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "MQ消息为空");
        }

        // 更新 chartStatus 为 running
        Long chartId = Long.parseLong(message);
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setChartStatus(ChartStatusEnum.RUNNING.getValue());
        boolean update = chartService.updateById(updateChart);
        if (!update) {
            this.handleUpdateChartError(channel, deliveryTag, chartId, "图表状态更新失败");
            return;
        }

        // 调用AI
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            this.handleUpdateChartError(channel, deliveryTag, chartId, "图表为空");
            return;
        }
        userIdStr = chart.getUserId().toString();
        String chartResult;
        try {
            chartResult = aiManager.doChatByGpt(this.buildUserInput(chart));
        } catch (Exception e) {
            this.handleUpdateChartError(channel, deliveryTag, chartId, "AI 生成错误");
            return;
        }
        String[] splits = chartResult.split("【【【【【");
        if (splits.length < 3) {
            this.handleUpdateChartError(channel, deliveryTag, chartId, "AI 生成错误");
            return;
        }
        // 去掉换行、空格和 json 纠错等
        // 使用正则表达式匹配并替换单引号
        String genChart = splits[1].trim().replaceAll("(?<!\\\\)'", "\"");
        try {
            JSONUtil.parse(genChart);
        } catch (Exception e) {
            this.handleUpdateChartError(channel, deliveryTag, chartId, "AI 生成 json 错误");
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
            this.handleUpdateChartError(channel, deliveryTag, chartId, "图表更新失败");
            return;
        }
        // 消息处理成功
        channel.basicAck(deliveryTag, false);
        notificationService.sendNotification(userIdStr, "处理成功");
    }

    /**
     * 构建用户输入
     *
     * @param chart
     * @return
     */
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

    /**
     * 处理图表更新异常
     *
     * @param chartId
     * @param execMessage
     */
    @SneakyThrows
    private void handleUpdateChartError(Channel channel, Long deliveryTag, Long chartId, String execMessage) {
        channel.basicNack(deliveryTag, false, false);
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
