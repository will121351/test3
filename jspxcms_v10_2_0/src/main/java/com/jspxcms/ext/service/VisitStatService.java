package com.jspxcms.ext.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.jspxcms.common.orm.RowSide;
import com.jspxcms.ext.domain.VisitStat;

public interface VisitStatService {
    Page<VisitStat> findAll(Map<String, String[]> params, Pageable pageable);

    RowSide<VisitStat> findSide(Map<String, String[]> params, VisitStat bean, Integer position, Sort sort);

    List<VisitStat> findByDate(Date begin, Integer type, Integer period, Integer siteId);

    int deleteByTime(Date before, Integer period);

    Page<VisitStat> sourceByTime(Date begin, Date end, Integer siteId, Pageable pageable);

    List<VisitStat> sourceCount(Date begin, Date end, Integer siteId);

    Page<VisitStat> urlByTime(Date begin, Date end, Integer siteId, Pageable pageable);

    List<VisitStat> countryByTime(Date begin, Date end, Integer siteId);

    List<VisitStat> browserByTime(Date begin, Date end, Integer siteId);

    List<VisitStat> osByTime(Date begin, Date end, Integer siteId);

    List<VisitStat> deviceByTime(Date begin, Date end, Integer siteId);

    VisitStat get(Integer id);

    void save(List<VisitStat> list);

    VisitStat save(VisitStat bean, Integer siteId);

    VisitStat update(VisitStat bean);

    VisitStat delete(Integer id);

    List<VisitStat> delete(Integer[] ids);
}
