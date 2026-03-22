package com.jspxcms.ext.web.back;

import com.jspxcms.common.orm.RowSide;
import com.jspxcms.common.web.Servlets;
import com.jspxcms.core.constant.Constants;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.service.OperationLogService;
import com.jspxcms.core.support.Backends;
import com.jspxcms.core.support.Context;
import com.jspxcms.ext.domain.VisitLog;
import com.jspxcms.ext.domain.VisitStat;
import com.jspxcms.ext.service.VisitLogService;
import com.jspxcms.ext.service.VisitStatService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.jspxcms.core.constant.Constants.*;

@Controller
@RequestMapping("/ext/visit_log")
public class VisitLogController {
    private static final Logger logger = LoggerFactory.getLogger(VisitLogController.class);

    @RequiresPermissions("ext:visit_log:list")
    @GetMapping("list.do")
    public String list(@PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable, HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Site site = Context.getCurrentSite();
        Map<String, String[]> params = Servlets.getParamValuesMap(request, Constants.SEARCH_PREFIX);
        Page<VisitLog> pagedList = service.findAll(site.getId(), params, pageable);
        modelMap.addAttribute("pagedList", pagedList);
        return "ext/visit_log/visit_log_list";
    }

    @RequiresPermissions("ext:visit_log:view")
    @GetMapping("view.do")
    public String view(Integer id, Integer position, @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable, HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Site site = Context.getCurrentSite();
        VisitLog bean = service.get(id);
        Map<String, String[]> params = Servlets.getParamValuesMap(request, Constants.SEARCH_PREFIX);
        RowSide<VisitLog> side = service.findSide(site.getId(), params, bean, position, pageable.getSort());
        modelMap.addAttribute("bean", bean);
        modelMap.addAttribute("side", side);
        modelMap.addAttribute("position", position);
        modelMap.addAttribute(OPRT, EDIT);
        return "ext/visit_log/visit_log_form";
    }

    @RequiresPermissions("ext:visit_log:delete")
    @RequestMapping("delete.do")
    public String delete(Integer[] ids, HttpServletRequest request, RedirectAttributes ra) {
        Site site = Context.getCurrentSite();
        validateIds(ids, site.getId());
        List<VisitLog> beans = service.delete(ids);
        for (VisitLog bean : beans) {
            logService.operation("opr.visitLog.batchDelete", bean.getUrl(), null, bean.getId(), request);
            logger.info("delete VisitLog, url={}.", bean.getUrl());
        }
        ra.addFlashAttribute(MESSAGE, DELETE_SUCCESS);
        return "redirect:list.do";
    }

    @RequiresPermissions("ext:visit_log:delete")
    @PostMapping("batch_delete.do")
    public String batchDelete(Date before, HttpServletRequest request, RedirectAttributes ra) {
        Site site = Context.getCurrentSite();
        long count = service.deleteByDate(before, site.getId());
        logService.operation("opr.visitLog.batchDelete", ISODateTimeFormat.date().print(new DateTime(before)), null, null, request);
        logger.info("delete VisitLog, date <= {}, count: {}.", before, count);
        ra.addFlashAttribute(MESSAGE, DELETE_SUCCESS);
        return "redirect:list.do";
    }

    @RequiresPermissions("ext:visit_log:traffic_analysis")
    @GetMapping("traffic_analysis.do")
    public String trafficAnalysis(String period, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Date begin = getBeginDate(period);
        List<VisitStat> list;
        String groupBy;
        if (System.currentTimeMillis() - begin.getTime() <= 24 * 60 * 60 * 1000L) {
            // 间隔时间小于24小时，则按小时分组
            list = service.trafficByHour(begin, getNextDay(), siteId);
            groupBy = "hour";
        } else if (System.currentTimeMillis() - begin.getTime() <= 366 * 24 * 60 * 60 * 1000L) {
            // 间隔时间小于1年，则按天分组
            list = service.trafficByDay(begin, siteId);
            groupBy = "day";
        } else {
            // 按月分组
            list = service.trafficByMonth(begin, siteId);
            groupBy = "month";
        }
        List<VisitStat> minuteList = service.trafficLast30Minute(siteId);
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("minuteList", minuteList);
        modelMap.addAttribute("period", period);
        modelMap.addAttribute("groupBy", groupBy);
        return "ext/visit_log/visit_traffic_analysis";
    }


    @RequiresPermissions("ext:visit_log:source_analysis")
    @GetMapping("source_analysis.do")
    public String sourceAnalysis(String period, Pageable pageable, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Date begin = getBeginDate(period);
        Page<VisitStat> pagedList = service.sourceByTime(begin, siteId, pageable);
        List<VisitStat> sourceList = service.sourceCount(begin, siteId, pagedList.getContent(), 9);
        modelMap.addAttribute("pagedList", pagedList);
        modelMap.addAttribute("sourceList", sourceList);
        modelMap.addAttribute("period", period);
        return "ext/visit_log/visit_source_analysis";
    }

    @RequiresPermissions("ext:visit_log:url_analysis")
    @GetMapping("url_analysis.do")
    public String urlAnalysis(String period, Pageable pageable, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Date begin = getBeginDate(period);
        Page<VisitStat> pagedList = statService.urlByTime(begin, getNextDay(), siteId, pageable);
        modelMap.addAttribute("pagedList", pagedList);
        modelMap.addAttribute("period", period);
        return "ext/visit_log/visit_url_analysis";
    }

    @RequiresPermissions("ext:visit_log:country_analysis")
    @GetMapping("country_analysis.do")
    public String countryAnalysis(String period, Pageable pageable, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Date begin = getBeginDate(period);
        List<VisitStat> list = statService.countryByTime(begin, getNextDay(), siteId);
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("period", period);
        return "ext/visit_log/visit_country_analysis";
    }

    @RequiresPermissions("ext:visit_log:browser_analysis")
    @GetMapping("browser_analysis.do")
    public String browserAnalysis(String period, Pageable pageable, HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Date begin = getBeginDate(period);
        List<VisitStat> list = statService.browserByTime(begin, getNextDay(), siteId);
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("period", period);
        return "ext/visit_log/visit_browser_analysis";
    }

    @RequiresPermissions("ext:visit_log:os_analysis")
    @GetMapping("os_analysis.do")
    public String osAnalysis(String period, Pageable pageable, HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Date begin = getBeginDate(period);
        List<VisitStat> list = statService.osByTime(begin, getNextDay(), siteId);
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("period", period);
        return "ext/visit_log/visit_os_analysis";
    }

    @RequiresPermissions("ext:visit_log:device_analysis")
    @GetMapping("device_analysis.do")
    public String deviceAnalysis(String period, Pageable pageable, HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        Date begin = getBeginDate(period);
        List<VisitStat> list = statService.deviceByTime(begin, getNextDay(), siteId);
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("period", period);
        return "ext/visit_log/visit_device_analysis";
    }

    @RequestMapping("visit_log_delete_job.do")
    public String scheduleJob(HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Integer siteId = Context.getCurrentSiteId();
        modelMap.addAttribute("includePage", "../../ext/visit_log/visit_log_delete_job.jsp");
        return "core/schedule_job/schedule_job_form";
    }

    private void validateIds(Integer[] ids, Integer siteId) {
        for (Integer id : ids) {
            Backends.validateDataInSite(service.get(id), siteId);
        }
    }

    private static Date getBeginDate(String period) {
        if ("today".equals(period)) {
            // 今日
            return DateTime.now().withMillisOfDay(0).toDate();
        } else if ("last1Year".equals(period)) {
            // 最近一年
            return DateTime.now().minusYears(1).toDate();
        } else if ("last3Year".equals(period)) {
            // 最近三年
            return DateTime.now().minusYears(3).toDate();
        } else if("all".equals(period)) {
            // 全部
            return new DateTime(0).toDate();
        } else{
            // 最近30日 last30Day 默认值
            return DateTime.now().minusDays(30).withMillisOfDay(0).toDate();
        }
    }

    /**
     * 第二天凌晨0点。否则查不到当天数据。
     */
    private static Date getNextDay() {
        return new DateTime().plusDays(1).withMillisOfDay(0).toDate();
    }

    @Autowired
    private OperationLogService logService;
    @Autowired
    private VisitLogService service;
    @Autowired
    private VisitStatService statService;
}
