import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.TRACE

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}
root(TRACE, ["STDOUT"])
logger("org.eclipse.jetty", INFO)
logger("io.netty", INFO)