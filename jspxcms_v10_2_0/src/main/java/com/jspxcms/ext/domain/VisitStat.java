package com.jspxcms.ext.domain;

import com.jspxcms.core.domain.Site;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cms_visit_stat")
public class VisitStat {
    // 类型：访问量
    public static final int TYPE_TRAFFIC = 0;
    // 类型：来源
    public static final int TYPE_SOURCE = 1;
    // 类型：URL
    public static final int TYPE_URL = 2;
    // 类型：省份
    public static final int TYPE_COUNTRY = 3;
    // 类型：浏览器
    public static final int TYPE_BROWSER = 4;
    // 类型：操作系统
    public static final int TYPE_OS = 5;
    // 类型：设备
    public static final int TYPE_DEVICE = 6;
    // 时期：按日
    public static final int PERIOD_DAY = 1;
    // 时期：按月
    public static final int PERIOD_MONTH = 2;

    public VisitStat() {
    }

    public VisitStat(Integer siteId, String dateString, Integer type, String name, Long views, Long uniqueViews, Long ipViews) {
        this(siteId, (Date) null, type, name, views, uniqueViews, ipViews);
        if (StringUtils.isNotBlank(dateString)) {
            try {
                if (dateString.length() == 8) {
                    setDate(new Timestamp(new SimpleDateFormat("yyyyMMdd").parse(dateString).getTime()));
                } else if (dateString.length() == 10) {
                    setDate(new Timestamp(new SimpleDateFormat("yyyyMMddHH").parse(dateString).getTime()));
                } else if (dateString.length() == 12) {
                    setDate(new Timestamp(new SimpleDateFormat("yyyyMMddHHmm").parse(dateString).getTime()));
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public VisitStat(Integer siteId, String dateString, Integer type, Integer period, String name, Long views, Long uniqueViews, Long ipViews) {
        this(siteId, (Date) null, type, name, views, uniqueViews, ipViews);
        this.setPeriod(period);
        if (StringUtils.isNotBlank(dateString)) {
            try {
                if (dateString.length() == 8) {
                    setDate(new Timestamp(new SimpleDateFormat("yyyyMMdd").parse(dateString).getTime()));
                } else if (dateString.length() == 10) {
                    setDate(new Timestamp(new SimpleDateFormat("yyyyMMddHH").parse(dateString).getTime()));
                } else if (dateString.length() == 12) {
                    setDate(new Timestamp(new SimpleDateFormat("yyyyMMddHHmm").parse(dateString).getTime()));
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public VisitStat(Integer siteId, Date date, Integer type, String name, Long views, Long uniqueViews, Long ipViews) {
        if (siteId != null) {
            Site site = new Site();
            site.setId(siteId);
            setSite(site);
        }
        if (date != null) {
            setDate(new Timestamp(date.getTime()));
        }
        setType(type);
        setName(name);
        if (views != null) {
            setViews(views.intValue());
        }
        if (uniqueViews != null) {
            setUniqueViews(uniqueViews.intValue());
        }
        if (ipViews != null) {
            setIpViews(ipViews.intValue());
        }
    }

    @Transient
    public void applyDefaultValue() {
        if (getDate() == null) {
            setDate(new Timestamp(System.currentTimeMillis()));
        }
        if (getPeriod() == null) {
            setPeriod(PERIOD_DAY);
        }
    }

    @Id
    @Column(name = "visitstat_id_", unique = true, nullable = false)
    @TableGenerator(name = "tg_cms_visit_stat", pkColumnValue = "cms_visit_stat", initialValue = 1, allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tg_cms_visit_stat")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id_", nullable = false)
    private Site site;
    @Column(name = "type_", nullable = false)
    private Integer type;
    @Column(name = "period_", nullable = false)
    private Integer period;
    @Column(name = "name_", nullable = false, length = 255)
    private String name;
    //    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_", nullable = false)
    private Timestamp date;
    @Column(name = "views_", nullable = false)
    private Integer views;
    @Column(name = "unique_views_", nullable = false)
    private Integer uniqueViews;
    @Column(name = "ip_views_", nullable = false)
    private Integer ipViews;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getUniqueViews() {
        return uniqueViews;
    }

    public void setUniqueViews(Integer uniqueViews) {
        this.uniqueViews = uniqueViews;
    }

    public Integer getIpViews() {
        return ipViews;
    }

    public void setIpViews(Integer ipViews) {
        this.ipViews = ipViews;
    }
}
