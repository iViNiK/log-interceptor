package it.vinicioflamini.sharedlib.loginterceptor.config;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.vinicioflamini.sharedlib.loginterceptor.log.Monitor;
import it.vinicioflamini.sharedlib.loginterceptor.log.filter.CorrelationHeaderFilter;

@Configuration
public class LogConfig extends Monitor {
	@Bean
	public FilterRegistrationBean<CorrelationHeaderFilter> correlationHeaderFilter() {
		FilterRegistrationBean<CorrelationHeaderFilter> filterRegBean = new FilterRegistrationBean<>();
		filterRegBean.setFilter(new CorrelationHeaderFilter());
		filterRegBean.setUrlPatterns(Arrays.asList("/*"));
		return filterRegBean;
	}
}
