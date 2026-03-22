package com.jspxcms.ext.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.ext.domain.VisitStat;
import com.jspxcms.ext.repository.plus.VisitStatDaoPlus;

public interface VisitStatDao extends Repository<VisitStat, Integer>, VisitStatDaoPlus {
    Page<VisitStat> findAll(Specification<VisitStat> spec, Pageable pageable);

    List<VisitStat> findAll(Specification<VisitStat> spec, Limitable limitable);

    VisitStat findOne(Integer id);

    VisitStat save(VisitStat bean);

    void delete(VisitStat bean);

    // --------------------

    @Query("from VisitStat bean where bean.date >= ?1 and bean.type = ?2 and bean.type = ?3 and bean.site.id = ?4 order by bean.date asc")
    List<VisitStat> findByDate(Date begin, Integer type, Integer period, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 1, bean.name, count(bean.views), count(distinct bean.uniqueViews), count(distinct bean.ipViews)) from VisitStat bean where bean.date >= ?1 and bean.date < ?2 and bean.site.id = ?3 and bean.type = 1 group by bean.name, bean.site.id order by count(bean.views) desc")
    Page<VisitStat> sourceByTime(Date begin, Date end, Integer siteId, Pageable pageable);

    @Query("select new VisitStat(1, '', 1, '', count(bean.views), count(distinct bean.uniqueViews), count(distinct bean.ipViews)) from VisitStat bean where bean.date >= ?1 and bean.date < ?2 and bean.site.id = ?3 and bean.type = 1")
    List<VisitStat> sourceCount(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 2, bean.name, count(bean.id), count(bean.uniqueViews), count(bean.ipViews)) from VisitStat bean where bean.date >= ?1 and bean.date < ?2 and bean.site.id = ?3 and bean.type = 2 group by bean.name, bean.site.id order by count(bean.views) desc")
    Page<VisitStat> urlByTime(Date begin, Date end, Integer siteId, Pageable pageable);

    @Query("select new VisitStat(bean.site.id, '', 3, bean.name, count(bean.id), count(bean.uniqueViews), count(bean.ipViews)) from VisitStat bean where bean.date >= ?1 and bean.date < ?2 and bean.site.id = ?3 and bean.type = 3 group by bean.name, bean.site.id order by count(bean.views) desc")
    List<VisitStat> countryByTime(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 4, bean.name, count(bean.id), count(bean.uniqueViews), count(bean.ipViews)) from VisitStat bean where bean.date >= ?1 and bean.date < ?2 and bean.site.id = ?3 and bean.type = 4 group by bean.name, bean.site.id order by count(bean.views) desc")
    List<VisitStat> browserByTime(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 5, bean.name, count(bean.id), count(bean.uniqueViews), count(bean.ipViews)) from VisitStat bean where bean.date >= ?1 and bean.date < ?2 and bean.site.id = ?3 and bean.type = 5 group by bean.name, bean.site.id order by count(bean.views) desc")
    List<VisitStat> osByTime(Date begin, Date end, Integer siteId);

    @Query("select new VisitStat(bean.site.id, '', 6, bean.name, count(bean.id), count(bean.uniqueViews), count(bean.ipViews)) from VisitStat bean where bean.date >= ?1 and bean.date < ?2 and bean.site.id = ?3 and bean.type = 6 group by bean.name, bean.site.id order by count(bean.views) desc")
    List<VisitStat> deviceByTime(Date begin, Date end, Integer siteId);

    @Modifying
    @Query("delete from VisitStat bean where bean.date < ?1 and bean.period = ?2")
    int deleteByTime(Date before, Integer period);
}
