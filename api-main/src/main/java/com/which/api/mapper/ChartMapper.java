package com.which.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.which.apicommon.model.entity.Chart;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author which
 * @description 针对表【chart(图表信息表)】的数据库操作Mapper
 * @createDate 2023-11-27 20:12:12
 * @Entity com.which.apicommon.model.entity.Chart
 */
public interface ChartMapper extends BaseMapper<Chart> {

    /**
     * 查询图表数据
     *
     * @param querySql
     * @return
     */
    @MapKey("")
    List<Map<String, Object>> queryChartData(@Param("querySql") String querySql);

}




