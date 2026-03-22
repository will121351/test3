package com.jspxcms.ext.domain;

import com.jspxcms.core.support.Configurable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GlobalVisit implements Configurable {
    public static final String PREFIX = "sys_visit_";
    private static final String VISIT_STAT_DATE_BY_DAY = PREFIX + "statDateByDay";
    private static final String VISIT_STAT_DATE_BY_MONTH = PREFIX + "statDateByMonth";

    private Map<String, String> customs;

    public GlobalVisit() {
    }

    public GlobalVisit(Map<String, String> customs) {
        this.customs = customs;
    }

    public String getVisitStatDateByDay() {
        return getCustoms().get(VISIT_STAT_DATE_BY_DAY);
    }

    public void setVisitStatDateByDay(String statDateByDay) {
        if (StringUtils.isNotBlank(statDateByDay)) {
            getCustoms().put(VISIT_STAT_DATE_BY_DAY, statDateByDay);
        } else {
            getCustoms().remove(VISIT_STAT_DATE_BY_DAY);
        }
    }

    public String getVisitStatDateByMonth() {
        return getCustoms().get(VISIT_STAT_DATE_BY_MONTH);
    }

    public void setVisitStatDateByMonth(String statDateByMonth) {
        if (StringUtils.isNotBlank(statDateByMonth)) {
            getCustoms().put(VISIT_STAT_DATE_BY_MONTH, statDateByMonth);
        } else {
            getCustoms().remove(VISIT_STAT_DATE_BY_MONTH);
        }
    }

    public Map<String, String> getCustoms() {
        if (customs == null) {
            customs = new HashMap<>();
        }
        return customs;
    }

    public void setCustoms(Map<String, String> customs) {
        this.customs = customs;
    }

    public String getPrefix() {
        return PREFIX;
    }
}
