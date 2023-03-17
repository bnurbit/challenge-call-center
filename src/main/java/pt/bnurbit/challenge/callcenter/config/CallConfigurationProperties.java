package pt.bnurbit.challenge.callcenter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("challenge.call-center.call")
@Getter
@Setter
public class CallConfigurationProperties {

    /**
     * The cost of calls in the first five minutes.
     */
    private double firstFiveMinutesCost;

    /**
     * The cost per minute of calls after the first five minutes.
     */
    private double costPerMinuteAfterFiveMinutes;
}
