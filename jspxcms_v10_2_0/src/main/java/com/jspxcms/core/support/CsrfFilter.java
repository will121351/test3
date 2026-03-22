package com.jspxcms.core.support;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class CsrfFilter extends OncePerRequestFilter {
    private Collection<String> domains;
    // 临时过滤路径。对用户管理使用`csrf token`的方式进行防范。
    private List<String> paths = Arrays.asList("/cmscp/core/user/save.do", "/cmscp/core/user/update.do", "/cmscp/core/user/delete.do", "/cmscp/core/user/delete_password.do", "/cmscp/core/user/check.do", "/cmscp/core/user/lock.do", "/cmscp/core/user/unlock.do",
            "/cmscp/core/user_global/save.do", "/cmscp/core/user_global/update.do", "/cmscp/core/user_global/delete.do", "/cmscp/core/user_global/delete_password.do", "/cmscp/core/user_global/check.do", "/cmscp/core/user_global/lock.do", "/cmscp/core/user_global/unlock.do");

    public CsrfFilter(Collection<String> domains) {
        this.domains = domains;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = CsrfToken.loadTokenFromSession(request);
        boolean missingToken = token == null;
        if (token == null) {
            token = UUID.randomUUID().toString().replace("-", "");
            CsrfToken.saveTokenToSession(token, request, response);
        }
        request.setAttribute(CsrfToken.PARAMETER_NAME, token);

        // GET 等方式不用提供Token，自动放行，不能用于修改数据。修改数据必须使用 POST、PUT、DELETE、PATCH 方式并且Referer要合法。
        if (Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS").contains(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();
        if (paths.contains(uri) && !token.equals(request.getParameter(CsrfToken.PARAMETER_NAME))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, missingToken ? "CSRF Token Missing" : "CSRF Token Invalid");
            return;
        }
        if (!domains.isEmpty() && !verifyDomains(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF Protection: Referer Illegal");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean verifyDomains(HttpServletRequest request) {
        // 从 HTTP 头中取得 Referer 值
        String referer = request.getHeader("Referer");
        // 判断 Referer 是否以 合法的域名 开头。
        if (referer != null) {
            // 如 http://mysite.com/abc.html https://www.mysite.com:8080/abc.html
            if (referer.indexOf("://") > 0) referer = referer.substring(referer.indexOf("://") + 3);
            // 如 mysite.com/abc.html
            if (referer.indexOf("/") > 0) referer = referer.substring(0, referer.indexOf("/"));
            // 如 mysite.com:8080
            if (referer.indexOf(":") > 0) referer = referer.substring(0, referer.indexOf(":"));
            // 如 mysite.com
            for (String domain : domains) {
                if (referer.endsWith(domain)) return true;
            }
        }
        return false;
    }

    private boolean verifyToken(HttpServletRequest request, String token) {
        return token.equals(request.getParameter(CsrfToken.PARAMETER_NAME));
    }
}
