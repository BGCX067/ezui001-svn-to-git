package cn.vstore.appserver.apilog;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @version $Id: ApiLogFilter.java 7437 2011-03-03 06:26:58Z yellow $
 */
public class ApiLogFilter implements Filter {

    private final Logger L = LoggerFactory.getLogger(ApiLogFilter.class);
    private ExecutorService executorService;
    private ApiLogService apiLogService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        long startRunMS = System.currentTimeMillis();
        try {
            chain.doFilter(req, resp);
        } finally {
            long endRunMS = System.currentTimeMillis();
            if (!(req instanceof HttpServletRequest)) {
                // 不應該發生這種狀況
                L.warn("ApiLogFilter's ServletRequest not instanceof HttpServletRequest. Can't insert APP_STORESERVER_LOG.");
                return;
            }
            HttpServletRequest httpReq = (HttpServletRequest) req;
            executorService.execute(new ApiLogHandler(apiLogService, httpReq,
                    startRunMS, endRunMS));
        }
    }

    @Override
    public void init(FilterConfig cfg) throws ServletException {
        ApplicationContext appContext = WebApplicationContextUtils
                .getWebApplicationContext(cfg.getServletContext());
        apiLogService = (ApiLogService) appContext.getBean("ApiLogService");
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void destroy() {
    }
}