package com.which.api.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 通用常量
 *
 * @author which
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    /**
     * 爬虫的最大次数
     */
    Long CRAWLER_NUM = 50L;

    /**
     * oss 访问地址
     */
    String OSS_HOST = "https://img.freefish.love/";

    /**
     * oss bucket名称
     */
    String BUCKET_NAME = "freefish-api";

    /**
     * 1M
     */
    Long ONE_M = 1024 * 1024L;

    /**
     * 文件大小阈值（10MB）
     */
    long TEN_MB = 10 * 1024 * 1024L;

    /**
     * 需要积分
     */
    Long NEED_BALANCE = 6L;

    /**
     * 合法文件后缀
     */
    List<String> VALID_SUFFIX = Arrays.asList("xlsx", "xls");

    /**
     * GPT AI 提示
     */
    String GPT_AI_PROMPT = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
            "分析需求：\n" +
            "{数据分析的需求或者目标}\n" +
            "原始数据：\n" +
            "{csv格式的原始数据（如果数据不完整请填充默认值），用,作为分隔符}\n" +
            "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
            "【【【【【\n" +
            "{前端 Echarts V5 的 option 配置对象的json（此处json中所有的引号必须使用\"\"英文双引号、data中的元素必须是有英文双引号的字符串，并且json必须是完整内容格式的格式）格式代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
            "【【【【【\n" +
            "{明确的数据分析结论、越详细越好，不要生成多余的注释}\n" +
            "---\n" +
            "示例：\n" +
            "用户输入：\n" +
            "分析需求：\n" +
            "分析网站用户的增长情况\n" +
            "原始数据：\n" +
            "日期，用户数\n" +
            "1号，10\n" +
            "2号，20\n" +
            "3号，30\n" +
            "你的回复：\n" +
            "【【【【【\n" +
            "{\n" +
            "    \"title\": {\n" +
            "    \t\"text\": \"网站用户增长情况\"\n" +
            "    },\n" +
            "    \"xAxis\": {\n" +
            "    \t\"type\": \"category\",\n" +
            "    \t\"data\": [\"1号\", \"2号\", \"3号\"]\n" +
            "    },\n" +
            "    \"yAxis\": {\n" +
            "    \t\"type\": \"value\"\n" +
            "    },\n" +
            "    \"series\": [{\n" +
            "    \t\"data\": [10, 20, 30],\n" +
            "    \t\"type\": \"line\"\n" +
            "    }]\n" +
            "}\n" +
            "【【【【【\n" +
            "根据数据分析，网站用户数量在过去三天内持续增长，增长趋势稳定，其中第三天相比于前两天增长速度更快。建议继续保持网站的运营和推广，以维持用户数量的稳步增长。\n";

}
