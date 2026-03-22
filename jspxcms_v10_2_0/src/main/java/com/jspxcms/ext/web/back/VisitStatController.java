package com.jspxcms.ext.web.back;

import static com.jspxcms.core.constant.Constants.CREATE;
import static com.jspxcms.core.constant.Constants.DELETE_SUCCESS;
import static com.jspxcms.core.constant.Constants.EDIT;
import static com.jspxcms.core.constant.Constants.MESSAGE;
import static com.jspxcms.core.constant.Constants.OPRT;
import static com.jspxcms.core.constant.Constants.SAVE_SUCCESS;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jspxcms.common.orm.RowSide;
import com.jspxcms.common.web.Servlets;
import com.jspxcms.ext.domain.VisitStat;
import com.jspxcms.ext.service.VisitStatService;
import com.jspxcms.core.constant.Constants;
import com.jspxcms.core.support.Context;

@Controller
@RequestMapping("/ext/visit_stat")
public class VisitStatController {
    private static final Logger logger = LoggerFactory.getLogger(VisitStatController.class);

    @RequiresPermissions("ext:visit_stat:list")
    @RequestMapping("list.do")
    public String list(@PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable, HttpServletRequest request, org.springframework.ui.Model modelMap) {
        Map<String, String[]> params = Servlets.getParamValuesMap(request, Constants.SEARCH_PREFIX);
        Page<VisitStat> pagedList = service.findAll(params, pageable);
        modelMap.addAttribute("pagedList", pagedList);
        return "ext/visit_stat/visit_stat_list";
    }

    @RequiresPermissions("ext:visit_stat:delete")
    @RequestMapping("delete.do")
    public String delete(Integer[] ids, RedirectAttributes ra) {
        List<VisitStat> beans = service.delete(ids);
        for (VisitStat bean : beans) {
            logger.info("delete VisitStat, name={}.", bean.getName());
        }
        ra.addFlashAttribute(MESSAGE, DELETE_SUCCESS);
        return "redirect:list.do";
    }

    @ModelAttribute("bean")
    public VisitStat preloadBean(@RequestParam(required = false) Integer oid) {
        return oid != null ? service.get(oid) : null;
    }

    @Autowired
    private VisitStatService service;
}
