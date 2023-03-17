package pt.bnurbit.challenge.callcenter.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pt.bnurbit.challenge.callcenter.config.CallConfigurationProperties;
import pt.bnurbit.challenge.callcenter.data.CallRecord;
import pt.bnurbit.challenge.callcenter.data.CallType;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

@Slf4j
public class CallStatistics {

    private static final int FIVE_MINUTES = 300;
    private final CallConfigurationProperties properties;
    private SortedMap<LocalDate, DailyStatistics> dailyStatisticsMap;

    public CallStatistics(CallConfigurationProperties properties) {
        this.properties = properties;
    }

    public SortedMap<LocalDate, DailyStatistics> getDailyStatisticsMap() {
        return dailyStatisticsMap;
    }

    public void buildStatistics(List<CallRecord> records) {

        this.dailyStatisticsMap = records.stream().collect(
                Collectors.groupingBy(r -> LocalDate.ofInstant(r.getStartTime(), ZoneOffset.UTC), TreeMap::new, Collectors.collectingAndThen(Collectors.toList(), DailyStatistics::new))
        );
    }

    @Getter
    class DailyStatistics {

        private final long totalCallDurationInbound;
        private final long totalCallDurationOutbound;
        private final long numberOfCalls;
        private final Map<String, Long> callsByCallerNumber;
        private final Map<String, Long> callsByCalleeNumber;
        private final double totalCallCost;

        @JsonIgnore
        private final LongFunction<Double> function = value -> value > FIVE_MINUTES
                ? properties.getFirstFiveMinutesCost() + properties.getCostPerMinuteAfterFiveMinutes() * calculateMinutes(value)
                : properties.getFirstFiveMinutesCost();

        public DailyStatistics(List<CallRecord> records) {

            this.totalCallDurationInbound = records.stream().filter(t -> t.getType().equals(CallType.INBOUND)).mapToLong(CallRecord::duration).sum();
            this.totalCallDurationOutbound = records.stream().filter(t -> t.getType().equals(CallType.OUTBOUND)).mapToLong(CallRecord::duration).sum();
            this.numberOfCalls = records.size();
            this.callsByCallerNumber = records.stream().collect(Collectors.groupingBy(CallRecord::getCallerNumber, Collectors.counting()));
            this.callsByCalleeNumber = records.stream().collect(Collectors.groupingBy(CallRecord::getCalleeNumber, Collectors.counting()));
            this.totalCallCost = Math.round(records.stream().filter(t -> t.getType().equals(CallType.OUTBOUND)).mapToDouble(l -> function.apply(l.duration())).sum() * 100.0) / 100.0;
        }

        private long calculateMinutes(long durationSec) {
            return (long) Math.ceil((double) (durationSec - FIVE_MINUTES) / 60);
        }
    }
}