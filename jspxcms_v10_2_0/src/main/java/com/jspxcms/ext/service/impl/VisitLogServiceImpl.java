package com.jspxcms.ext.service.impl;

import com.jspxcms.common.ip.IPSeeker;
import com.jspxcms.common.orm.Limitable;
import com.jspxcms.common.orm.RowSide;
import com.jspxcms.common.orm.SearchFilter;
import com.jspxcms.core.domain.Global;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.listener.SiteDeleteListener;
import com.jspxcms.core.listener.UserDeleteListener;
import com.jspxcms.core.service.GlobalService;
import com.jspxcms.ext.domain.GlobalVisit;
import com.jspxcms.ext.domain.VisitLog;
import com.jspxcms.ext.domain.VisitStat;
import com.jspxcms.ext.repository.VisitLogDao;
import com.jspxcms.ext.service.VisitLogService;
import com.jspxcms.ext.service.VisitStatService;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class VisitLogServiceImpl implements VisitLogService, SiteDeleteListener, UserDeleteListener {
    private static final Logger logger = LoggerFactory.getLogger(VisitLogServiceImpl.class);

    public Page<VisitLog> findAll(Integer siteId, Map<String, String[]> params, Pageable pageable) {
        return dao.findAll(spec(siteId, params), pageable);
    }

    public RowSide<VisitLog> findSide(Integer siteId, Map<String, String[]> params, VisitLog bean, Integer position, Sort sort) {
        if (position == null) {
            return new RowSide<VisitLog>();
        }
        Limitable limit = RowSide.limitable(position, sort);
        List<VisitLog> list = dao.findAll(spec(siteId, params), limit);
        return RowSide.create(list, bean);
    }

    private Specification<VisitLog> spec(final Integer siteId, Map<String, String[]> params) {
        Collection<SearchFilter> filters = SearchFilter.parse(params).values();
        final Specification<VisitLog> fsp = SearchFilter.spec(filters, VisitLog.class);
        Specification<VisitLog> sp = new Specification<VisitLog>() {
            public Predicate toPredicate(Root<VisitLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate pred = fsp.toPredicate(root, query, cb);
                if (siteId != null) {
                    pred = cb.and(pred, cb.equal(root.get("site").<Integer>get("id"), siteId));
                }
                return pred;
            }
        };
        return sp;
    }

    public List<VisitStat> trafficByMonth(Date begin, Integer siteId) {
        List<VisitStat> list = visitStatService.findByDate(begin, VisitStat.TYPE_TRAFFIC, VisitStat.PERIOD_MONTH, siteId);
        List<VisitStat> result = new ArrayList<>();
        DateTime dt;
        if (list.size() > 0) {
            dt = new DateTime(list.get(0).getDate());
        } else {
            dt = DateTime.now();
        }
        for (VisitStat stat : list) {
            while (dt.toDate().compareTo(stat.getDate()) < 0) {
                result.add(new VisitStat(siteId, dt.toDate(), VisitStat.TYPE_TRAFFIC, "NONE", 0L, 0L, 0L));
                dt = dt.plusMonths(1);
            }
            result.add(stat);
            dt = dt.plusMonths(1);
        }
        DateTime today = new DateTime().withMillisOfDay(0);
        while (dt.compareTo(today) < 0) {
            result.add(new VisitStat(siteId, dt.toDate(), VisitStat.TYPE_TRAFFIC, "NONE", 0L, 0L, 0L));
            dt = dt.plusMonths(1);
        }
        return result;
    }

    public List<VisitStat> trafficByDay(Date begin, Integer siteId) {
        List<VisitStat> list = visitStatService.findByDate(begin, VisitStat.TYPE_TRAFFIC, VisitStat.PERIOD_DAY, siteId);
        List<VisitStat> result = new ArrayList<>();
        DateTime dt = new DateTime(begin.getTime());
        for (VisitStat stat : list) {
            while (dt.toDate().compareTo(stat.getDate()) < 0) {
                result.add(new VisitStat(siteId, dt.toDate(), VisitStat.TYPE_TRAFFIC, "NONE", 0L, 0L, 0L));
                dt = dt.plusDays(1);
            }
            result.add(stat);
            dt = dt.plusDays(1);
        }
        DateTime today = new DateTime().withMillisOfDay(0);
        while (dt.compareTo(today) < 0) {
            result.add(new VisitStat(siteId, dt.toDate(), VisitStat.TYPE_TRAFFIC, "NONE", 0L, 0L, 0L));
            dt = dt.plusDays(1);
        }
        // 每日访问量没有当日的访问量，要加上当日的访问量
        List<VisitStat> todayStat = dao.trafficByDay(today.toDate(), new Date(System.currentTimeMillis()), siteId);
        if (!todayStat.isEmpty()) {
            result.add(todayStat.iterator().next());
        } else {
            result.add(new VisitStat(siteId, dt.toDate(), VisitStat.TYPE_TRAFFIC, "NONE", 0L, 0L, 0L));
        }
        return result;
    }

    public List<VisitStat> trafficByHour(Date begin, Date end, Integer siteId) {
        List<VisitStat> list = dao.trafficByHour(begin, end, siteId);
        List<VisitStat> result = new ArrayList<>();
        DateTime dt = new DateTime(begin.getTime());
        for (VisitStat stat : list) {
            while (dt.toDate().compareTo(stat.getDate()) < 0) {
                result.add(new VisitStat(siteId, dt.toDate(), VisitStat.TYPE_TRAFFIC, "NONE", 0L, 0L, 0L));
                dt = dt.plusHours(1);
            }
            result.add(stat);
            dt = dt.plusHours(1);
        }
        while (dt.toDate().compareTo(end) < 0) {
            result.add(new VisitStat(siteId, dt.toDate(), VisitStat.TYPE_TRAFFIC, "NONE", 0L, 0L, 0L));
            dt = dt.plusHours(1);
        }
        return result;
    }

    public List<VisitStat> trafficLast30Minute(Integer siteId) {
        DateTime end = new DateTime();
        end = end.plusMinutes(1).withMillisOfSecond(0);
        DateTime begin = end.minusMinutes(30);
        return trafficByMinute(begin.toDate(), end.toDate(), siteId);
    }

    public List<VisitStat> trafficByMinute(Date begin, Date end, Integer siteId) {
        List<VisitStat> list = dao.trafficByMinute(begin, end, siteId);
        List<VisitStat> result = new ArrayList<>();
        DateTime dt = new DateTime(begin.getTime());
        for (VisitStat stat : list) {
            while (dt.toDate().compareTo(stat.getDate()) < 0) {
                result.add(new VisitStat(siteId, dt.toDate(), VisitStat.TYPE_TRAFFIC, "NONE", 0L, 0L, 0L));
                dt = dt.plusMinutes(1);
            }
            result.add(stat);
            dt = dt.plusMinutes(1);
        }
        while (dt.toDate().compareTo(end) < 0) {
            result.add(new VisitStat(siteId, dt.toDate(), VisitStat.TYPE_TRAFFIC, "NONE", 0L, 0L, 0L));
            dt = dt.plusMinutes(1);
        }
        return result;
    }

    public Page<VisitStat> sourceByTime(Date begin, Integer siteId, Pageable pageable) {
        return visitStatService.sourceByTime(begin, new Date(), siteId, pageable);
    }

    public List<VisitStat> sourceCount(Date begin, Integer siteId, List<VisitStat> sourceList, int maxSize) {
        List<VisitStat> resultList = new ArrayList<>();
        List<VisitStat> sourceCount = visitStatService.sourceCount(begin, new Date(), siteId);
        if (!sourceCount.isEmpty()) {
            VisitStat count = sourceCount.iterator().next();
            long otherPv = count.getViews();
            long otherUv = count.getUniqueViews();
            long otherIp = count.getIpViews();
            int index = 1;
            for (VisitStat stat : sourceList) {
                if (index++ > maxSize) {
                    break;
                }
                otherPv -= stat.getViews();
                otherUv -= stat.getUniqueViews();
                otherIp -= stat.getIpViews();
                resultList.add(stat);
            }
            if (otherPv > 0) {
                resultList.add(new VisitStat(1, (Date) null, 1, VisitLog.SOURCE_OTHER, otherPv, otherUv, otherIp));
            }
        }
        return resultList;
    }

    public Page<VisitStat> urlByTime(Date begin, Date end, Integer siteId, Pageable pageable) {
        return dao.urlByTime(begin, end, siteId, pageable);
    }

    public List<VisitStat> countryByTime(Date begin, Date end, Integer siteId) {
        return dao.countryByTime(begin, end, siteId);
    }

    public List<VisitStat> browserByTime(Date begin, Date end, Integer siteId) {
//        return dao.browserByTime(begin, end, siteId);
        return visitStatService.browserByTime(begin, end, siteId);
    }

    public List<VisitStat> osByTime(Date begin, Date end, Integer siteId) {
        return dao.osByTime(begin, end, siteId);
    }

    public List<VisitStat> deviceByTime(Date begin, Date end, Integer siteId) {
        return dao.deviceByTime(begin, end, siteId);
    }

    public VisitLog get(Integer id) {
        return dao.findOne(id);
    }

    @Transactional
    public VisitLog save(String url, String referrer, String ip, String cookie, String userAgent, User user, Site site) {
        // 先统计
        stat();

        VisitLog bean = new VisitLog();
        bean.setUrl(url);
        bean.setReferrer(referrer);
        bean.setCookie(cookie);
        bean.setSite(site);
        bean.setUser(user);

        bean.setIp(ip);
        if (StringUtils.isNotBlank(ip)) {
            bean.setCountry(ipSeeker.getCountry(ip));
            bean.setArea(ipSeeker.getArea(ip));
        }

        bean.setUserAgent(userAgent);
        if (StringUtils.isNotBlank(userAgent)) {
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            bean.setBrowser(ua.getBrowser().toString());
            bean.setOs(ua.getOperatingSystem().toString());
            bean.setDevice(ua.getOperatingSystem().getDeviceType().toString());
        }

        if (StringUtils.isNoneBlank(bean.getReferrer(), bean.getUrl())) {
            try {
                URL accessURL = new URL(url);
                URL referrerURL = new URL(referrer);
                // url 和 referrer 的域名不同，则代表来源不同网站，设置来源域名
                if (!StringUtils.equals(referrerURL.getHost(), accessURL.getHost())) {
                    String source = referrerURL.getProtocol() + "://" + referrerURL.getHost();
                    if (referrerURL.getPort() >= 0) {
                        source += ":" + referrerURL.getPort();
                    }
                    bean.setSource(source);
                }
            } catch (MalformedURLException e) {
                logger.error("url: " + url + "; referrer: " + referrer, e);
            }
        }
        if (StringUtils.isBlank(bean.getSource())) {
            bean.setSource(VisitLog.SOURCE_DIRECT);
        }

        bean.applyDefaultValue();
        bean = dao.save(bean);
        return bean;
    }

    @Transactional
    protected void stat() {
        Global global = globalService.findUnique();
        GlobalVisit globalVisit = new GlobalVisit(global.getCustoms());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String statDate = globalVisit.getVisitStatDateByDay();
        Date now = new Date();
        String nowDate = dateFormat.format(now);
        // 不同日则统计
        if (!nowDate.equals(statDate)) {
            Date begin;
            if (StringUtils.isNotBlank(statDate)) {
                try {
                    begin = dateFormat.parse(statDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                begin = new Date(Long.MIN_VALUE);
            }
            globalVisit.setVisitStatDateByDay(nowDate);
            globalService.update(global, null);
            Date end = new DateTime().withMillisOfDay(0).toDate();
            visitStatService.save(dao.statTraffic(begin, end));
            visitStatService.save(dao.statSource(begin, end));
            visitStatService.save(dao.statUrl(begin, end));
            visitStatService.save(dao.statCountry(begin, end));
            visitStatService.save(dao.statBrowser(begin, end));
            visitStatService.save(dao.statOs(begin, end));
            visitStatService.save(dao.statDevice(begin, end));
            // 删除31天前的访问日志数据
            dao.deleteByTime(new DateTime(end).minusDays(31).toDate());
            // 删除3年前的统计数据
            visitStatService.deleteByTime(new DateTime(end).minusYears(3).toDate(), VisitStat.PERIOD_DAY);

            dateFormat = new SimpleDateFormat("yyyyMM");
            statDate = globalVisit.getVisitStatDateByMonth();
            nowDate = dateFormat.format(now);
            // 不同月则统计
            if (!nowDate.equals(statDate)) {
                if (StringUtils.isNotBlank(statDate)) {
                    try {
                        begin = dateFormat.parse(statDate);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    begin = new Date(Long.MIN_VALUE);
                }
                globalVisit.setVisitStatDateByMonth(nowDate);
                globalService.update(global, null);
                end = new DateTime().withDayOfMonth(1).withMillisOfDay(0).toDate();
                visitStatService.save(dao.statTrafficByMonth(begin, end));
            }
        }
    }

    @Transactional
    public VisitLog delete(Integer id) {
        VisitLog bean = dao.findOne(id);
        dao.delete(bean);
        return bean;
    }

    @Transactional
    public List<VisitLog> delete(Integer[] ids) {
        List<VisitLog> beans = new ArrayList<>(ids.length);
        for (Integer id : ids) {
            beans.add(delete(id));
        }
        return beans;
    }

    @Transactional
    public long deleteByDate(Date before, Integer siteId) {
        return dao.deleteByTimeAndSiteId(before, siteId);
    }

    @Override
    @Transactional
    public void preSiteDelete(Integer[] ids) {
        if (ArrayUtils.isNotEmpty(ids)) {
            dao.deleteBySiteId(Arrays.asList(ids));
        }
    }

    @Override
    @Transactional
    public void preUserDelete(Integer[] ids) {
        if (ArrayUtils.isNotEmpty(ids)) {
            dao.updateUserToNull(Arrays.asList(ids));
        }
    }

    private VisitStatService visitStatService;
    private GlobalService globalService;
    private IPSeeker ipSeeker;

    @Autowired
    public void setVisitStatService(VisitStatService visitStatService) {
        this.visitStatService = visitStatService;
    }

    @Autowired
    public void setGlobalService(GlobalService globalService) {
        this.globalService = globalService;
    }

    @Autowired
    public void setIpSeeker(IPSeeker ipSeeker) {
        this.ipSeeker = ipSeeker;
    }

    private VisitLogDao dao;

    @Autowired
    public void setDao(VisitLogDao dao) {
        this.dao = dao;
    }
}
