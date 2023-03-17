package pt.bnurbit.challenge.callcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pt.bnurbit.challenge.callcenter.config.CallConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CallConfigurationProperties.class)
public class CallCenterServer {

    public static void main(String[] args) {
        SpringApplication.run(CallCenterServer.class, args);
    }

}
