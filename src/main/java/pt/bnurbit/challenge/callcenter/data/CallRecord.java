package pt.bnurbit.challenge.callcenter.data;

import lombok.Getter;
import pt.bnurbit.challenge.callcenter.dto.CallInputDTO;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A call record represents a phone call between two numbers.
 */
@Entity(name = "CallRecord")
@Table(name = "tCallRecord")
@Getter
public class CallRecord {

    /**
     * Generated Id of the call record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The phone number of the caller.
     */
    @Column(nullable = false, updatable = false)
    private String callerNumber;

    /**
     * The phone number of the callee.
     */
    @Column(nullable = false, updatable = false)
    private String calleeNumber;

    /**
     * Start timestamp of the call.
     */
    @Column(nullable = false, updatable = false)
    private Instant startTime;

    /**
     * End timestamp of the call.
     */
    @Column(nullable = false, updatable = false)
    private Instant endTime;

    /**
     * Type of the call. Represented by {@link CallType}.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private CallType type;

    private CallRecord() {
    }

    public CallRecord(CallInputDTO callInputDTO) {
        this.callerNumber = callInputDTO.getCallerNumber();
        this.calleeNumber = callInputDTO.getCalleeNumber();
        this.startTime = callInputDTO.getStartTime();
        this.endTime = callInputDTO.getEndTime();
        this.type = callInputDTO.getType();
    }

    public long duration() {
        return Duration.between(startTime, endTime).getSeconds();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CallRecord)) {
            return false;
        }
        CallRecord call = (CallRecord) o;
        return Objects.equals(id, call.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, callerNumber, calleeNumber);
    }

    @Override
    public String toString() {
        return "Call from " + callerNumber + " to " + calleeNumber;
    }
}
