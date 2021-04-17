package it.vinicioflamini.sharedlib.loginterceptor.log.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import it.vinicioflamini.sharedlib.loginterceptor.log.RequestCorrelation;

public class CorrelationHeaderFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(CorrelationHeaderFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		log.info("filtering");
		final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		String correlationId = httpServletRequest.getHeader(RequestCorrelation.CORRELATION_ID_HEADER);
		String userName = httpServletRequest.getHeader(RequestCorrelation.USERNAME_HEADER);

		try {
			if (!currentRequestIsAsyncDispatcher(httpServletRequest)) {
				if (correlationId == null) {
					String uuid = UUID.randomUUID().toString();
					correlationId = RequestCorrelation.CORRELATION_ID_PREFIX + uuid.toUpperCase();
				}

				RequestCorrelation.setId(correlationId);
				RequestCorrelation.setUserName(userName);

				StringBuilder sb = new StringBuilder();
				sb.append("[");
				if (userName != null && !userName.trim().isEmpty()) {
					sb.append("user: " + userName);
					sb.append(" | ");
				}
				sb.append("correlationId: " + correlationId);
				sb.append(" ] ");
				String customData = sb.toString();
				MDC.put("customData", customData);
			}

			filterChain.doFilter(httpServletRequest, servletResponse);
		} finally {
			MDC.clear();
		}
	}

	private boolean currentRequestIsAsyncDispatcher(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getDispatcherType().equals(DispatcherType.ASYNC);
	}

	@Override
	public void destroy() {
	}

}