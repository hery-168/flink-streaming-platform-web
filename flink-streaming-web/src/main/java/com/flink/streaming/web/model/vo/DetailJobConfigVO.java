package com.flink.streaming.web.model.vo;

import com.flink.streaming.web.common.util.DateFormatUtils;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-04
 * @time 01:28
 */
@Data
public class DetailJobConfigVO {

    private Long id;

    /**
     * 任务名称
     */
    private String jobName;


    /**
     * flink运行配置
     */
    private String jobId;


    private String deployMode;
    /**
     * 1:开启 0: 关闭
     */
    private Integer isOpen;


    private String openStr;


    private Integer stauts;


    private String stautsStr;


    private String lastStartTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String editTime;

    /**
     * flink运行配置
     */
    private String flinkRunConfig;

    /**
     * flink运行配置
     */
    private String flinkCheckpointConfig;



    /**
     * udf注册名称如
     * utc2local|com.streaming.flink.udf.UTC2Local 多个可用;分隔
     * utc2local代表组册的名称
     * com.streaming.flink.udf.UTC2Local代表类名
     *
     */
    private String udfRegisterName;

    /**
     * udf地址 如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String  udfJarPath;

    /**
     * sql语句
     */
    private String flinkSql;



    public static DetailJobConfigVO toVO(JobConfigDTO jobConfigDTO) {
        if (jobConfigDTO == null) {
            return null;
        }
        DetailJobConfigVO detailJobConfigVO = new DetailJobConfigVO();
        detailJobConfigVO.setId(jobConfigDTO.getId());
        detailJobConfigVO.setJobName(jobConfigDTO.getJobName());
        detailJobConfigVO.setFlinkRunConfig(jobConfigDTO.getFlinkRunConfig());
        if (StringUtils.isNotEmpty(jobConfigDTO.getFlinkCheckpointConfig())){
            detailJobConfigVO.setFlinkCheckpointConfig(jobConfigDTO.getFlinkCheckpointConfig().replaceAll("\"","&quot;"));
        }
        detailJobConfigVO.setJobId(jobConfigDTO.getJobId());
        detailJobConfigVO.setIsOpen(jobConfigDTO.getIsOpen());
        detailJobConfigVO.setOpenStr(YN.getYNByValue(jobConfigDTO.getIsOpen()).getDescribe());
        detailJobConfigVO.setStautsStr(jobConfigDTO.getStauts().getDesc());
        detailJobConfigVO.setStauts(jobConfigDTO.getStauts().getCode());
        detailJobConfigVO.setLastStartTime(DateFormatUtils.toFormatString(jobConfigDTO.getLastStartTime()));
        detailJobConfigVO.setCreateTime(DateFormatUtils.toFormatString(jobConfigDTO.getCreateTime()));
        detailJobConfigVO.setEditTime(DateFormatUtils.toFormatString(jobConfigDTO.getEditTime()));
        detailJobConfigVO.setFlinkSql(jobConfigDTO.getFlinkSql());
        detailJobConfigVO.setDeployMode(jobConfigDTO.getDeployModeEnum().name());
        detailJobConfigVO.setUdfJarPath(jobConfigDTO.getUdfJarPath());
        detailJobConfigVO.setUdfRegisterName(jobConfigDTO.getUdfRegisterName());
        return detailJobConfigVO;
    }



}
