package com.jspxcms.ext.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.jspxcms.ext.domain.VisitStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.ext.domain.VisitLog;
import com.jspxcms.ext.repository.plus.VisitLogDaoPlus;

public interface VisitLogDao extends Repository<VisitLog, Integer>, VisitLogDaoPlus {
    Page<VisitLog> findAll(Specification<VisitLog> spec, Pageable pageable);

    List<VisitLog> findAll(Specification<VisitLog> spec, Limitable limitable);

    VisitLog findOne(Integer id);

    VisitLog save(VisitLog bean);

    void delete(VisitLog bean);

    // --------------------

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,8), 0, 'NONE', count(bean.id), count(distinct bean.cookie), count(distinct bean.ip))from VisitLog bean where bean.time >= ?1 and bean.time < ?2 group by SUBSTRING(bean.timeString,1,8), bean.site.id")
    List<VisitStat> statTraffic(Date begin, Date end);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,8), 0, 2, 'NONE', count(bean.id), count(distinct bean.cookie), count(distinct bean.ip))from VisitLog bean where bean.time >= ?1 and bean.time < ?2 group by SUBSTRING(bean.timeString,1,8), bean.site.id")
    List<VisitStat> statTrafficByMonth(Date begin, Date end);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,8), 1, bean.source, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 group by SUBSTRING(bean.timeString,1,8), bean.site.id, bean.source")
    List<VisitStat> statSource(Date begin, Date end);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,8), 2, bean.url, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 group by SUBSTRING(bean.timeString,1,8), bean.site.id, bean.url")
    List<VisitStat> statUrl(Date begin, Date end);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,8), 3, bean.country, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 group by SUBSTRING(bean.timeString,1,8), bean.site.id, bean.country")
    List<VisitStat> statCountry(Date begin, Date end);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,8), 4, bean.browser, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 group by SUBSTRING(bean.timeString,1,8), bean.site.id, bean.browser")
    List<VisitStat> statBrowser(Date begin, Date end);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,8), 5, bean.os, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 group by SUBSTRING(bean.timeString,1,8), bean.site.id, bean.os")
    List<VisitStat> statOs(Date begin, Date end);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,8), 6, bean.device, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 group by SUBSTRING(bean.timeString,1,8), bean.site.id, bean.device")
    List<VisitStat> statDevice(Date begin, Date end);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,8), 0, 'NONE', count(bean.id), count(distinct bean.cookie), count(distinct bean.ip))from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3 group by SUBSTRING(bean.timeString,1,8), bean.site.id")
    List<VisitStat> trafficByDay(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,10), 0, 'NONE', count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3 group by SUBSTRING(bean.timeString,1,10) order by SUBSTRING(bean.timeString,1,10), bean.site.id")
    List<VisitStat> trafficByHour(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, SUBSTRING(bean.timeString,1,12), 0, 'NONE', count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3 group by SUBSTRING(bean.timeString,1,12) order by SUBSTRING(bean.timeString,1,12), bean.site.id")
    List<VisitStat> trafficByMinute(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 1, bean.source, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3 group by bean.source, bean.site.id order by count(bean.id) desc")
    Page<VisitStat> sourceByTime(Date begin, Date end, Integer siteId, Pageable pageable);

    @Query("select new VisitStat(1, '', 1, '', count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3")
    List<VisitStat> sourceCount(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 2, bean.url, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3 group by bean.url, bean.site.id order by count(bean.url) desc")
    Page<VisitStat> urlByTime(Date begin, Date end, Integer siteId, Pageable pageable);

    @Query("select new VisitStat(bean.site.id, '', 3, bean.country, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3 group by bean.country, bean.site.id order by count(bean.country) desc")
    List<VisitStat> countryByTime(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 4, bean.browser, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3 group by bean.browser, bean.site.id order by count(bean.browser) desc")
    List<VisitStat> browserByTime(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 5, bean.os, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3 group by bean.os, bean.site.id order by count(bean.os) desc")
    List<VisitStat> osByTime(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 6, bean.device, count(bean.id), count(distinct bean.cookie), count(distinct bean.ip)) from VisitLog bean where bean.time >= ?1 and bean.time < ?2 and bean.site.id = ?3 group by bean.device, bean.site.id order by count(bean.device) desc")
    List<VisitStat> deviceByTime(Date begin, Date end, Integer siteId);

    @Modifying
    @Query("delete from VisitLog bean where bean.time < ?1 and bean.site.id = ?2")
    int deleteByTimeAndSiteId(Date before, Integer siteId);

    @Modifying
    @Query("delete from VisitLog bean where bean.time < ?1")
    int deleteByTime(Date before);

    @Modifying
    @Query("delete from VisitLog bean where bean.site.id in (?1)")
    int deleteBySiteId(Collection<Integer> siteIds);

    @Modifying
    @Query("update VisitLog bean set bean.user.id = null where bean.user.id in (?1)")
    int updateUserToNull(Collection<Integer> userIds);
}
