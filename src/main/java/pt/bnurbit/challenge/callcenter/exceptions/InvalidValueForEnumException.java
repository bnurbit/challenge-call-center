package pt.bnurbit.challenge.callcenter.exceptions;

public class InvalidValueForEnumException extends RuntimeException {

    public InvalidValueForEnumException(Class<?> enumClass, String possibleValues) {
        super(String.format("Invalid value for %s. Possible values are %s.", enumClass.getName(), possibleValues));
    }
}
