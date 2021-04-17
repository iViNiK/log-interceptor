# log-interceptor

## Abstract

The log-interceptor component allows to propagate a correlation-id and the username of the authenticated user in the http call chain between the different services (soap and / or rest) and to log in a centralized and automated way the execution time of the methods that have a certain signature.

Provides basic performance monitoring features of application components (controller, service and repository) by logging the execution times of each call.

Below is an example of a log:

	2019-12-13 10:58:48.580 [correlationId: CID-07B6DFDB-C199-44B7-B4CA-B96763A0C817 ]  INFO  --- [nio-8080-exec-4] i.d.s.r.s.i.R.add(int,int)  - type=SERVICE method=com.acme.referenceimplementation.service.impl.ReferenceImplementationServiceImpl.add returned TIME=646
	2019-12-13 10:58:48.605 [correlationId: CID-07B6DFDB-C199-44B7-B4CA-B96763A0C817 ]  INFO  --- [nio-8080-exec-4] R.d.s.r.c.R.find(String)    - type=REST method=com.acme.referenceimplementation.controller.ReferenceImplementationController.find returned TIME=671

## Configuring log

In Spring the reference framework for logging is logback which is automatically loaded and configured by Spring Boot through its own starter-kits.
The default configuration provided by Spring can be customized through the _logback-spring.xml_ configuration file.
Through the configuration file it is possible to define specific _"appenders"_ for routing log events to specific destinations.
In particular, logback provides the SYSLOG protocol appender that allows you to send log events to a syslogd daemon listening on a given port.
An example of configuration of the syslog appender that can be taken as a reference and possibly extended, is shown below:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="SYSLOG" class="ch.qos.logback.classic.net.SyslogAppender">
	  <syslogHost>127.0.0.1</syslogHost>
	  <facility>LOCAL0</facility>
	  <port>514</port>
	  <throwableExcluded>true</throwableExcluded>
	  <suffixPattern>%d{yyyy-MM-dd HH:mm:ss.SSS,Europe/Rome} [%thread] %X{customData} %-5level %logger{36}%-40.40logger{39} - %msg%n</suffixPattern>  
  </appender>

    <appender name="Console"
        class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
            	%d{yyyy-MM-dd HH:mm:ss.SSS,Europe/Rome} %highlight(%X{customData} %-5level) --- [%blue(%15.15t)] %cyan(%-40.40logger{39}) - %msg%n
            </Pattern>
        </layout>
    </appender>
    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="Console" />
        <appender-ref ref="SYSLOG" />
    </root>
 
    <!-- LOG "com.acme.referenceimplementation*" at TRACE level -->
    <logger name="com.acme.referenceimplementation" level="trace" additivity="false">
        <appender-ref ref="Console" />
    </logger>
 
</configuration>
```

Among the configurations present in the example, the _`<suffixPattern>`_ tag, that specifies the format of the log message, deserves particular attention.

```xml
<suffixPattern>%d{yyyy-MM-dd HH:mm:ss.SSS,Europe/Rome} [%thread] %X{customData} %-5level %logger{36}%-40.40logger{39} - %msg%n</suffixPattern>
```

The pattern uses the _`% X {customData} `_ specifier that allows you to retrieve the context information (E.g. CORRELATION ID, USERNAME, ...) propagated by the
log-interceptor component related to the specific thread that is handling the http request.
With this technique, traceability in multithreaded environments is guaranteed as it will be possible to correlate the entire data flow and any exceptions,
that belong to that particular Correlation-ID / username, making it easy to identify in the event of an error which data set caused the exception and to
consequently reconstruct the flow.

Below is a sample log extract with the correlationid:

	2019-12-13 09:58:22.451 [correlationId: SGI-A88722F8-A17B-456B-9678-26D502BCE18D ]  ERROR --- [nio-8080-exec-8] S.d.s.r.s.i.R.testException()            - type=SERVICE method=com.acme.referenceimplementation.service.impl.ReferenceImplementationServiceImpl.testException detail=BusinessException{businessErrorCode=ERRCODE1, message=Message Error, debugMessage=Detailed Message Error}

## License
Copyright &copy;2019 by Vinicio Flamini <io@vinicioflamini.it>

    