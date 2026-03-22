package com.jspxcms.ext.service.impl;

import java.util.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.common.orm.Limitable;
import com.jspxcms.common.orm.RowSide;
import com.jspxcms.common.orm.SearchFilter;
import com.jspxcms.ext.domain.VisitStat;
import com.jspxcms.core.domain.Site;
import com.jspxcms.ext.repository.VisitStatDao;
import com.jspxcms.core.service.SiteService;
import com.jspxcms.ext.service.VisitStatService;

@Service
@Transactional(readOnly = true)
public class VisitStatServiceImpl implements VisitStatService {
    public Page<VisitStat> findAll(Map<String, String[]> params, Pageable pageable) {
        return dao.findAll(spec(params), pageable);
    }

    public RowSide<VisitStat> findSide(Map<String, String[]> params, VisitStat bean, Integer position, Sort sort) {
        if (position == null) {
            return new RowSide<>();
        }
        Limitable limit = RowSide.limitable(position, sort);
        List<VisitStat> list = dao.findAll(spec(params), limit);
        return RowSide.create(list, bean);
    }

    private Specification<VisitStat> spec(Map<String, String[]> params) {
        Collection<SearchFilter> filters = SearchFilter.parse(params).values();
        final Specification<VisitStat> fsp = SearchFilter.spec(filters, VisitStat.class);
        return new Specification<VisitStat>() {
            public Predicate toPredicate(Root<VisitStat> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate pred = fsp.toPredicate(root, query, cb);
                return pred;
            }
        };
    }

    public List<VisitStat> findByDate(Date begin, Integer type, Integer period, Integer siteId) {
        return dao.findByDate(begin, type, period, siteId);
    }

    public Page<VisitStat> sourceByTime(Date begin, Date end, Integer siteId, Pageable pageable) {
        return dao.sourceByTime(begin, end, siteId, pageable);
    }

    public List<VisitStat> sourceCount(Date begin, Date end, Integer siteId) {
        return dao.sourceCount(begin, end, siteId);
    }

    public Page<VisitStat> urlByTime(Date begin, Date end, Integer siteId, Pageable pageable) {
        return dao.urlByTime(begin, end, siteId, pageable);
    }

    public List<VisitStat> countryByTime(Date begin, Date end, Integer siteId) {
        return dao.countryByTime(begin, end, siteId);
    }

    public List<VisitStat> browserByTime(Date begin, Date end, Integer siteId) {
        return dao.browserByTime(begin, end, siteId);
    }

    public List<VisitStat> osByTime(Date begin, Date end, Integer siteId) {
        return dao.osByTime(begin, end, siteId);
    }

    public List<VisitStat> deviceByTime(Date begin, Date end, Integer siteId) {
        return dao.deviceByTime(begin, end, siteId);
    }

    public int deleteByTime(Date before, Integer period) {
        return dao.deleteByTime(before, period);
    }

    public VisitStat get(Integer id) {
        return dao.findOne(id);
    }

    @Transactional
    public void save(List<VisitStat> list) {
        for (VisitStat bean : list) {
            save(bean, bean.getSite().getId());
        }
    }

    @Transactional
    public VisitStat save(VisitStat bean, Integer siteId) {
        Site site = siteService.get(siteId);
        bean.setSite(site);
        bean.applyDefaultValue();
        bean = dao.save(bean);
        return bean;
    }

    @Transactional
    public VisitStat update(VisitStat bean) {
        bean.applyDefaultValue();
        bean = dao.save(bean);
        return bean;
    }

    @Transactional
    public VisitStat delete(Integer id) {
        VisitStat bean = dao.findOne(id);
        dao.delete(bean);
        return bean;
    }

    @Transactional
    public List<VisitStat> delete(Integer[] ids) {
        List<VisitStat> beans = new ArrayList<>(ids.length);
        for (Integer id : ids) {
            beans.add(delete(id));
        }
        return beans;
    }

    private SiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    private VisitStatDao dao;

    @Autowired
    public void setDao(VisitStatDao dao) {
        this.dao = dao;
    }
}
