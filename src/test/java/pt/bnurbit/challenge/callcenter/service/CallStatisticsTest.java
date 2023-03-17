package pt.bnurbit.challenge.callcenter.service;

import org.junit.jupiter.api.Test;
import pt.bnurbit.challenge.callcenter.config.CallConfigurationProperties;
import pt.bnurbit.challenge.callcenter.data.CallRecord;
import pt.bnurbit.challenge.callcenter.dto.CallInputDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CallStatisticsTest {

    @Test
    void buildStatistics() {

        // Arrange
        LocalDate baseDate = LocalDate.of(2021,1,1);
        LocalTime baseTime = LocalTime.of(10,0,0);

        // Arrange
        List<CallInputDTO> callInputDTOS = List.of(
                new CallInputDTO("1", "2", "Inbound", LocalDateTime.of(baseDate,baseTime).toInstant(ZoneOffset.UTC), LocalDateTime.of(baseDate,baseTime).plusMinutes(10).toInstant(ZoneOffset.UTC)),
                new CallInputDTO("2", "3", "Outbound", LocalDateTime.of(baseDate,baseTime).toInstant(ZoneOffset.UTC), LocalDateTime.of(baseDate,baseTime).plusMinutes(7).plusSeconds(43).toInstant(ZoneOffset.UTC)),
                new CallInputDTO("3", "4", "Outbound", LocalDateTime.of(baseDate,baseTime).plusDays(1).toInstant(ZoneOffset.UTC), LocalDateTime.of(baseDate,baseTime).plusDays(1).plusMinutes(3).toInstant(ZoneOffset.UTC))
        );
        List<CallRecord> records = CallService.fromCallInputList(callInputDTOS);

        CallConfigurationProperties callConfigurationProperties = new CallConfigurationProperties();
        callConfigurationProperties.setFirstFiveMinutesCost(0.10);
        callConfigurationProperties.setCostPerMinuteAfterFiveMinutes(0.05);
        CallStatistics callStatistics = new CallStatistics(callConfigurationProperties);

        callStatistics.buildStatistics(records);
        SortedMap<LocalDate, CallStatistics.DailyStatistics> map = callStatistics.getDailyStatisticsMap();

        // Assert
        assertEquals(2, map.size());
        assertEquals(600, map.get(baseDate).getTotalCallDurationInbound());
        assertEquals(463, map.get(baseDate).getTotalCallDurationOutbound());
        assertEquals(2, map.get(baseDate).getNumberOfCalls());
        assertEquals(2, map.get(baseDate).getCallsByCallerNumber().size());
        assertEquals(2, map.get(baseDate).getCallsByCalleeNumber().size());
        assertEquals(0.25, map.get(baseDate).getTotalCallCost());
        assertEquals(0.1, map.get(baseDate.plusDays(1)).getTotalCallCost());
    }
}