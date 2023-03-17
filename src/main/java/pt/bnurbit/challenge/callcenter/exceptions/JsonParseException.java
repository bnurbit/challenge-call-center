package pt.bnurbit.challenge.callcenter.exceptions;

public class JsonParseException extends RuntimeException {

    public JsonParseException(Class<?> classType) {
        super(String.format("Could not parse JSON string to object %s.", classType.getName()));
    }
}
