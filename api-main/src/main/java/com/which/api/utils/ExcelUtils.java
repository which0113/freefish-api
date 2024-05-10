package com.which.api.utils;

import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel工具类
 *
 * @author which
 */
@Slf4j
public class ExcelUtils {
    public static String excelToCsv(MultipartFile multipartFile) {
        // 1. load excel data
        List<Map<Integer, String>> list = null;
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(getEnumByValue(suffix))
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("excel to csv error !", e);
        }
        // 2. excel to csv
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return list.stream().map(
                x -> x.values()
                        .stream().filter(ObjectUtils::isNotEmpty)
                        .collect(Collectors.joining(","))
        ).collect(Collectors.joining("\n"));
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 值
     * @return excel类型枚举
     */
    private static ExcelTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isNotEmpty(value)) {
            for (ExcelTypeEnum anEnum : ExcelTypeEnum.values()) {
                if (anEnum.getValue().equals(value)) {
                    return anEnum;
                }
            }
        }
        return null;
    }

}
