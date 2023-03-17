package pt.bnurbit.challenge.callcenter.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pt.bnurbit.challenge.callcenter.api.ICall;
import pt.bnurbit.challenge.callcenter.data.CallRecord;
import pt.bnurbit.challenge.callcenter.data.CallType;
import pt.bnurbit.challenge.callcenter.dto.CallInputDTO;
import pt.bnurbit.challenge.callcenter.service.CallStatistics;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/call-center/calls")
@Validated
public class CallsController {

    @Autowired
    private ICall callService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCall(@RequestBody @Valid CallInputDTO callInput, BindingResult result) {

        if(result.hasErrors()){
            return new ResponseEntity<>(String.format("Could not add call record. Errors: %s", errorsAsString(result)), HttpStatus.BAD_REQUEST);
        }

        callService.create(callInput);

        return new ResponseEntity<>("Successfully added 1 call record.", HttpStatus.OK);
    }

    @PostMapping(value = "/createMany", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCall(@RequestBody @Valid List<CallInputDTO> callInput, BindingResult result) {

        if (result.hasErrors() || callInput.isEmpty()) {
            return new ResponseEntity<>(String.format("Could not add call records. Errors: %s",errorsAsString(result)), HttpStatus.BAD_REQUEST);
        }

        callService.create(callInput);

        return new ResponseEntity<>(String.format("Successfully added %d call records.", callInput.size()), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public void deleteCall(@PathVariable(value = "id") long id) {
        callService.delete(id);
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CallRecord> getPagedCalls(
            @RequestParam(value = "type", required = false) String callType,
            @RequestParam(value = "pageNumber") @Min(value = 0, message = "Page number must not be less than 0") int pageNumber,
            @RequestParam(value = "pageSize") @Min(value = 1, message = "Page size must not be less than 1") int pageSize,
            @RequestParam(value = "reverseSort", required = false) boolean reverseSort,
            @RequestParam(value = "sortFields", required = false) String[] sortFields
    ) {

        Sort.Direction direction = reverseSort ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = sortFields == null || sortFields.length < 1 || Arrays.stream(sortFields).anyMatch(String::isBlank)
                ? PageRequest.of(pageNumber, pageSize)
                : PageRequest.of(pageNumber, pageSize, direction, sortFields);

        return callService.getCalls(CallType.enumFromString(callType), pageable).getContent();
    }

    @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public CallStatistics getStatistics() {
        return callService.getStatistics();
    }

    private String errorsAsString(BindingResult bindingResult){
        return bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
    }
}
