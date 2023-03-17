package pt.bnurbit.challenge.callcenter.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.bnurbit.challenge.callcenter.data.CallRecord;
import pt.bnurbit.challenge.callcenter.data.CallType;
import pt.bnurbit.challenge.callcenter.dto.CallInputDTO;
import pt.bnurbit.challenge.callcenter.service.CallStatistics;

import java.util.List;

public interface ICall {

    /**
     * Create a call record representing a phone call between two numbers.
     */
    CallRecord create(CallInputDTO callInputDTO);

    /**
     * Create a list of call records representing a phone call between two numbers.
     */
    List<CallRecord> create(List<CallInputDTO> callInputDTO);

    /**
     * Delete a call record by its id.
     *
     * @param id The id of the call record
     */
    void delete(long id);

    /**
     * Get the list of call records for a given type.
     * Expects a {@link Pageable} object.
     *
     * @param type     Type of the call. Optional. Represented by {@link CallType}
     * @param pageable A Pageable object.
     * @return The list of call records in respect to the provided pageable.
     */
    Page<CallRecord> getCalls(CallType type, Pageable pageable);

    /**
     * Return the statistics of the call service, aggregated by day.
     * Statistics consist of:
     * <ul>
     * <li>Total call duration by type</li>
     * <li>Total number of calls</li>
     * <li>Number of calls by Caller Number</li>
     * <li>Number of calls by Callee Number</li>
     * <li>Total call cost.
     *  Inbound calls are free.
     *  Outbound calls cost 0.05 per minute after the first 5 minutes. The first 5 minutes cost 0.10.
     * </li>
     * </ul>
     *
     * @return the statistics object.
     */
    CallStatistics getStatistics();
}