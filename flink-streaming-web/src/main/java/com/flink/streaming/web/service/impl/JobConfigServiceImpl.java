package com.flink.streaming.web.service.impl;

import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.mapper.JobConfigMapper;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.entity.JobConfig;
import com.flink.streaming.web.model.param.JobConfigParam;
import com.flink.streaming.web.service.JobConfigService;
import com.flink.streaming.web.service.JobRunLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-14
 * @time 19:02
 */
@Slf4j
@Service
public class JobConfigServiceImpl implements JobConfigService {


    @Autowired
    private JobConfigMapper jobConfigMapper;

    @Autowired
    private JobRunLogService jobRunLogService;

    @Override
    public void addJobConfig(JobConfigDTO jobConfigDTO) {
        if (jobConfigDTO == null) {
            return;
        }
        this.checkJobName(jobConfigDTO.getJobName(), jobConfigDTO.getId());

        jobConfigMapper.insert(JobConfigDTO.toEntity(jobConfigDTO));

    }

    @Override
    public void updateJobConfigById(JobConfigDTO jobConfigDTO) {
        if (jobConfigDTO == null || jobConfigDTO.getId() == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_PARAM_IS_NULL);
        }
        JobConfig jobConfig= jobConfigMapper.selectByPrimaryKey(jobConfigDTO.getId());
        if (jobConfig==null){
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }


        if (StringUtils.isNotEmpty(jobConfigDTO.getJobName())) {
            this.checkJobName(jobConfigDTO.getJobName(), jobConfigDTO.getId());
        }
        jobConfigMapper.updateByPrimaryKeySelective(JobConfigDTO.toEntity(jobConfigDTO));
    }

    @Override
    public void updateJobConfigStatusById(Long id, JobConfigStatus jobConfigStatus) {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setId(id);
        jobConfig.setStauts(jobConfigStatus.getCode());
        jobConfigMapper.updateByPrimaryKeySelective(jobConfig);

    }

    @Override
    public JobConfigDTO getJobConfigById(Long id) {
        if (id == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_PARAM_IS_NULL);
        }
        return JobConfigDTO.toDTO(jobConfigMapper.selectByPrimaryKey(id));
    }

    @Override
    public void openOrClose(Long id, YN yn, String userName) {
        if (id == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_PARAM_IS_NULL);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setId(id);
        jobConfig.setIsOpen(yn.getValue());
        jobConfig.setEditor(userName);
        jobConfigMapper.updateByPrimaryKeySelective(jobConfig);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobConfigById(Long id, String userName) {
        JobConfigDTO jobConfigDTO = this.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException("配置不存在！");
        }
        if (JobConfigStatus.RUN.equals(jobConfigDTO.getStauts()) || JobConfigStatus.STARTING.equals(jobConfigDTO.getStauts())) {
            throw new BizException("任务正在启动中或者正在运行，请先停止任务");
        }
        if (YN.Y.getValue() == jobConfigDTO.getIsOpen().intValue()) {
            throw new BizException("请先关闭配置");
        }
        jobConfigMapper.deleteById(id, userName);
        jobRunLogService.deleteLogByJobConfigId(id);
    }

    @Override
    public PageModel<JobConfigDTO> queryJobConfig(JobConfigParam jobConfigParam) {
        if (jobConfigParam == null) {
            jobConfigParam = new JobConfigParam();
        }

        PageHelper.startPage(jobConfigParam.getPageNum(), jobConfigParam.getPageSize(), YN.Y.getCode());

        Page<JobConfig> page = jobConfigMapper.findJobConfig(jobConfigParam);
        if (page == null) {
            return null;
        }
        PageModel<JobConfigDTO> pageModel = new PageModel<JobConfigDTO>();
        pageModel.setPageNum(page.getPageNum());
        pageModel.setPages(page.getPages());
        pageModel.setPageSize(page.getPageSize());
        pageModel.setTotal(page.getTotal());
        pageModel.addAll(JobConfigDTO.toListDTO(page.getResult()));
        return pageModel;
    }

    @Override
    public List<JobConfigDTO> findRunJobConfig() {
        return JobConfigDTO.toListDTO(jobConfigMapper.findJobConfigByRun());
    }


    /**
     * 检查任务名称是不是重复
     *
     * @author zhuhuipei
     * @date 2020-07-14
     * @time 13:56
     */
    private void checkJobName(String jobName, Long id) {
        long count = jobConfigMapper.selectCountByJobName(jobName, id);
        if (count >= 1) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_NAME_IS_EXIST);
        }
    }
}
