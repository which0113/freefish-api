package com.which.api.constant;

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

}
