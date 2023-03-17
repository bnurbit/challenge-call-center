package pt.bnurbit.challenge.callcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pt.bnurbit.challenge.callcenter.api.ICall;
import pt.bnurbit.challenge.callcenter.api.ICallDevTools;
import pt.bnurbit.challenge.callcenter.config.CallConfigurationProperties;
import pt.bnurbit.challenge.callcenter.data.CallRecord;
import pt.bnurbit.challenge.callcenter.data.CallRepository;
import pt.bnurbit.challenge.callcenter.data.CallType;
import pt.bnurbit.challenge.callcenter.dto.CallInputDTO;
import pt.bnurbit.challenge.callcenter.exceptions.CallRecordNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CallService implements ICall, ICallDevTools {

    private final Random random = new Random();

    @Autowired
    private CallRepository repository;
    @Autowired
    private CallConfigurationProperties callProperties;

    @Override
    public CallRecord create(CallInputDTO callInputDTO) {

        CallRecord record = new CallRecord(callInputDTO);
        return repository.save(record);
    }

    @Override
    public List<CallRecord> create(List<CallInputDTO> callInputDTO) {

        long startTime = System.nanoTime();

        List<CallRecord> records = repository.saveAll(fromCallInputList(callInputDTO));

        if (log.isInfoEnabled()) {
            log.info("Added {} call records in {} ms", callInputDTO.size(), (System.nanoTime() - startTime) / 1000000);
        }
        return records;
    }

    @Override
    public void delete(long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.debug("Deleted call record with id {}", id);
        } else {
            throw new CallRecordNotFoundException("Could not delete call record with id '{}'. Record could not be found.");
        }
    }

    @Override
    public Page<CallRecord> getCalls(CallType type, Pageable pageable) {
        return type == null ? repository.findAll(pageable) : repository.findAllByType(type, pageable);
    }

    @Override
    public CallStatistics getStatistics() {
        long startTime = System.nanoTime();

        CallStatistics callStatistics = new CallStatistics(callProperties);
        callStatistics.buildStatistics(repository.findAll());

        if (log.isInfoEnabled()) {
            log.info("Calculated call statistics in {} ms", (System.nanoTime() - startTime) / 1000000);
        }

        return callStatistics;
    }

    protected static List<CallRecord> fromCallInputList(List<CallInputDTO> callInputDTOS){
        return callInputDTOS.stream().map(CallRecord::new).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        repository.deleteAll();
    }

    @Override
    public void generate(int n) {

        long timer = System.nanoTime();
        int baseDate = 1609459200;

        int bound = (int) Math.abs(baseDate - Instant.now().toEpochMilli()/1000); // 01-01-2020 to current date

        List<CallInputDTO> callInputDTOS = new ArrayList<>();
        for(int i = 0; i < n; i++){

            int resultCaller = random.nextInt(89)+10;
            int resultCallee = random.nextInt(89)+10;

            String callerNumber = String.format("+351 9164231%d", resultCaller);
            String calleeNumber = String.format("+351 9164232%d", resultCallee);

            String callType = CallType.randomType().toString();

            int resultStartTime = random.nextInt(bound) + baseDate;
            Instant startTime = Instant.ofEpochSecond(resultStartTime);

            int resultEndTime = random.nextInt(1000) + 20;
            Instant endTime = Instant.ofEpochSecond((long) resultStartTime + resultEndTime);

            callInputDTOS.add(new CallInputDTO(callerNumber, calleeNumber, callType, startTime, endTime));
        }

        this.create(callInputDTOS);

        if (log.isInfoEnabled()) {
            log.info("Generated {} call records in {} ms", n, (System.nanoTime() - timer) / 1000000);
        }
    }
}
