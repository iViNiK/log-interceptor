package it.vinicioflamini.sharedlib.loginterceptor.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

@Aspect
@Configuration
public class Monitor {

	@Around("@annotation(it.vinicioflamini.sharedlib.loginterceptor.log.Log)")
	public Object logGeneric(ProceedingJoinPoint joinPoint) throws Throwable {
		return logExecutionTime(joinPoint, "");
	}

	@AfterThrowing(pointcut = "execution(* *.service.impl.*.*(..))", throwing = "ex")
	public void logAfterThrowingBusinessException(JoinPoint joinPoint, Throwable ex) throws Throwable {
		String logString = getInitialLogString("SERVICE", joinPoint);
		Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().toString());
		if (logger.isErrorEnabled()) {
			logger.error(String.format("%s detail = %s", logString, ex.toString()), ex);
		}
	}

	@AfterThrowing(pointcut = "execution(* *.business.*.*(..))", throwing = "ex")
	public void logAfterThrowingBusinessExceptionBusiness(JoinPoint joinPoint, Throwable ex) throws Throwable {
		String logString = getInitialLogString("BUSINESS", joinPoint);
		Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().toString());
		if (logger.isErrorEnabled()) {
			logger.error(String.format("%s detail = %s", logString, ex.toString()), ex);
		}
	}

	@Around("execution(* *.controller.* .*(..))")
	public Object logWeb(ProceedingJoinPoint joinPoint) throws Throwable {
		return logExecutionTime(joinPoint, "REST");
	}

	@Around("execution(* *.service.impl.*.*(..))")
	public Object logWS(ProceedingJoinPoint joinPoint) throws Throwable {
		return logExecutionTime(joinPoint, "SERVICE");
	}

	@Around("execution(* *.business.*.*(..))")
	public Object logBusinessFacade(ProceedingJoinPoint joinPoint) throws Throwable {
		return logExecutionTime(joinPoint, "BUSINESS");
	}

	/**/

	private static String getInitialLogString(String type, JoinPoint joinPoint) {
		StringBuilder sb = new StringBuilder();

		if (type != null && !type.trim().isEmpty()) {
			sb.append("type=" + type + " ");
		}

		sb.append("method=" + joinPoint.getSignature().getDeclaringTypeName() + "."
				+ joinPoint.getSignature().getName() + " ");

		return sb.toString();
	}

	private Object logExecutionTime(ProceedingJoinPoint joinPoint, String type) throws Throwable {
		Object returnValue = null;

		String logString = getInitialLogString(type, joinPoint);

		Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().toString());

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		if (logger.isInfoEnabled()) {
			logger.info(String.format("%s invoking", logString));
		}
		returnValue = joinPoint.proceed();
		stopWatch.stop();

		if (logger.isInfoEnabled()) {
			logger.info(String.format("%s returned TIME = %d", logString, stopWatch.getTotalTimeMillis()));
		}

		return returnValue;
	}

}
