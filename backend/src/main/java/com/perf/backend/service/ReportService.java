package com.perf.backend.service;

import com.perf.backend.entity.Report;
import com.perf.backend.mapper.ReportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

  private final ReportMapper reportMapper;

  public ReportService(ReportMapper reportMapper) {
    this.reportMapper = reportMapper;
  }

  @Transactional
  public Report createReport(Integer userId, Integer recordId) {
    Report report = new Report();
    report.setUserId(userId);
    report.setRecordId(recordId);
    reportMapper.insert(report);
    return report;
  }

  public Report findById(Integer reportId) {
    return reportMapper.selectById(reportId);
  }

  @Transactional
  public boolean updateComprehensiveData(Integer reportId, String comprehensiveData) {
    Report report = findById(reportId);
    if (report == null) {
      return false;
    }
    report.setComprehensiveData(comprehensiveData);
    return reportMapper.updateById(report) > 0;
  }

  @Transactional
  public boolean updateDimensionData(Integer reportId, String dimensionData) {
    Report report = findById(reportId);
    if (report == null) {
      return false;
    }
    report.setDimensionData(dimensionData);
    return reportMapper.updateById(report) > 0;
  }

  @Transactional
  public boolean updateImplementationData(Integer reportId, String implementationData) {
    Report report = findById(reportId);
    if (report == null) {
      return false;
    }
    report.setImplementationData(implementationData);
    return reportMapper.updateById(report) > 0;
  }
}
