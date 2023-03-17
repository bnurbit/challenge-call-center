package pt.bnurbit.challenge.callcenter.data;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import pt.bnurbit.challenge.callcenter.exceptions.InvalidValueForEnumException;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public enum CallType {

    INBOUND("Inbound"), OUTBOUND("Outbound");

    private final String callTypeString;
    private static final Random random = new Random();

    @Override
    @JsonValue
    public String toString() {
        return callTypeString;
    }

    public static String valuesAsString(){
        return Stream.of(CallType.values()).map(Object::toString).collect(Collectors.joining(",","[","]"));
    }

    public static CallType enumFromString(String enumString){

        if(enumString == null || enumString.isBlank()){
            return null;
        }

        for(CallType type : CallType.values()){
            if(enumString.equals(type.toString())){
                return type;
            }
        }
        throw new InvalidValueForEnumException(CallType.class, CallType.valuesAsString());
    }

    public static CallType randomType()  {
        return CallType.values()[random.nextInt(values().length)];
    }
}
