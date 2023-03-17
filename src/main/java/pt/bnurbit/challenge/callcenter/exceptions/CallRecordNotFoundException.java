package pt.bnurbit.challenge.callcenter.exceptions;

public class CallRecordNotFoundException extends RuntimeException {

    public CallRecordNotFoundException(String message) {
        super(message);
    }
}
