package com.jspxcms.ext.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jspxcms.ext.domain.VisitStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.jspxcms.common.orm.RowSide;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;
import com.jspxcms.ext.domain.VisitLog;

public interface VisitLogService {
    Page<VisitLog> findAll(Integer siteId, Map<String, String[]> params, Pageable pageable);

    RowSide<VisitLog> findSide(Integer siteId, Map<String, String[]> params, VisitLog bean, Integer position, Sort sort);

    List<VisitStat> trafficByMonth(Date begin, Integer siteId);

    List<VisitStat> trafficByDay(Date begin, Integer siteId);

    List<VisitStat> trafficByHour(Date begin, Date end, Integer siteId);

    List<VisitStat> trafficLast30Minute(Integer siteId);

    List<VisitStat> trafficByMinute(Date begin, Date end, Integer siteId);

    List<VisitStat> sourceCount(Date begin, Integer siteId, List<VisitStat> sourceList, int maxSize);

    Page<VisitStat> sourceByTime(Date begin, Integer siteId, Pageable pageable);

    Page<VisitStat> urlByTime(Date begin, Date end, Integer siteId, Pageable pageable);

    List<VisitStat> countryByTime(Date begin, Date end, Integer siteId);

    List<VisitStat> browserByTime(Date begin, Date end, Integer siteId);

    List<VisitStat> osByTime(Date begin, Date end, Integer siteId);

    List<VisitStat> deviceByTime(Date begin, Date end, Integer siteId);

    VisitLog get(Integer id);

    VisitLog save(String url, String referrer, String ip, String cookie, String userAgent, User user, Site site);

    VisitLog delete(Integer id);

    List<VisitLog> delete(Integer[] ids);

    long deleteByDate(Date before, Integer siteId);
}
