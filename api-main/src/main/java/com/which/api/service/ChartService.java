package com.which.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.which.api.model.dto.chart.GenChartByAiRequest;
import com.which.api.model.entity.Chart;
import com.which.api.model.vo.BiVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author which
 * @description 针对表【chart(图表信息表)】的数据库操作Service
 * @createDate 2023-11-27 20:12:12
 */
public interface ChartService extends IService<Chart> {

    /**
     * 生成AI分析后数据
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    BiVO genChartByAi(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request);

}
