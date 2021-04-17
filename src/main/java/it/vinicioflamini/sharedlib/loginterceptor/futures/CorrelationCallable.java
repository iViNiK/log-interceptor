package it.vinicioflamini.sharedlib.loginterceptor.futures;

import java.util.concurrent.Callable;

import org.slf4j.MDC;

import it.vinicioflamini.sharedlib.loginterceptor.log.RequestCorrelation;

public abstract class CorrelationCallable<V> implements Callable<V>{

	private String correlationId;
	private String userName;
	
	protected CorrelationCallable() {
		correlationId = RequestCorrelation.getId();
		userName = RequestCorrelation.getUserName();
	}

	@Override
	public V call() throws Exception {
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
		
		return run();
	}

	protected abstract V run();
	
}
