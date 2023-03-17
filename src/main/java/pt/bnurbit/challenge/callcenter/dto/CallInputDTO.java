package pt.bnurbit.challenge.callcenter.dto;

import lombok.Getter;
import pt.bnurbit.challenge.callcenter.data.CallType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
public class CallInputDTO {

    @NotBlank(message = "Caller Number is mandatory")
    private String callerNumber;

    @NotBlank(message = "Callee Number is mandatory")
    private String calleeNumber;

    @NotNull(message = "Call type is mandatory")
    private CallType type;

    @NotNull(message = "Start time is mandatory")
    private Instant startTime;

    @NotNull(message = "End time is mandatory")
    private Instant endTime;

    private CallInputDTO(){}

    public CallInputDTO(String callerNumber, String calleeNumber, String type, Instant startTime, Instant endTime) {
        this.callerNumber = callerNumber;
        this.calleeNumber = calleeNumber;
        this.type = CallType.enumFromString(type);
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
