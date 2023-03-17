package pt.bnurbit.challenge.callcenter.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.bnurbit.challenge.callcenter.api.ICall;
import pt.bnurbit.challenge.callcenter.api.ICallDevTools;
import pt.bnurbit.challenge.callcenter.data.CallRecord;
import pt.bnurbit.challenge.callcenter.data.CallType;
import pt.bnurbit.challenge.callcenter.dto.CallInputDTO;
import pt.bnurbit.challenge.callcenter.exceptions.JsonParseException;

import javax.validation.constraints.Min;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Controller
public class CallsControllerClient {

    private static final int AMOUNT = 1000;

    @Autowired
    private ICall callService;
    @Autowired
    private ICallDevTools callDevTools;

    @GetMapping("/")
    public String homePage(Model model) {
        return getPagedCalls(null, 0, 10, false, null, model);
    }

    @GetMapping("/generate")
    public String generate() {
        callDevTools.generate(AMOUNT);
        return "redirect:/";
    }

    @GetMapping("/clear")
    public String clear() {
        callDevTools.clear();
        return "redirect:/";
    }

    @GetMapping("/createForm")
    public String createForm(Model model) {
        CallInputDTO callInput = new CallInputDTO("", "", "Inbound", Instant.now(), Instant.now());
        model.addAttribute("callInput", callInput);
        return "create";
    }

    @PostMapping(value = "/create")
    public String create(@ModelAttribute("callInput") CallInputDTO callInput) {

        callService.create(callInput);
        return "redirect:/";
    }

    @PostMapping(value = "/createMany")
    public String createMany(Model model, @ModelAttribute(value = "callInputJSON") String callInputJSON) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        List<CallInputDTO> callInputDTOList;
        try {
            callInputDTOList = Arrays.asList(objectMapper.readValue(callInputJSON, CallInputDTO[].class));
        } catch (JsonProcessingException e) {
            throw new JsonParseException(CallInputDTO[].class);
        }
        callService.create(callInputDTOList);
        return "redirect:/";
    }


    @GetMapping(value = "/delete/{id}")
    public String deleteCall(@PathVariable(value = "id") long id) {
        callService.delete(id);
        return "redirect:/";
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPagedCalls(
            @RequestParam(value = "type", required = false) String callType,
            @RequestParam(value = "pageNumber") @Min(value = 0, message = "Page number must not be less than 0") int pageNumber,
            @RequestParam(value = "pageSize") @Min(value = 1, message = "Page size must not be less than 1") int pageSize,
            @RequestParam(value = "reverseSort", required = false) boolean reverseSort,
            @RequestParam(value = "sortFields", required = false) String[] sortFields,
            Model model
    ) {

        Sort.Direction direction = reverseSort ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = sortFields == null || sortFields.length < 1 || Arrays.stream(sortFields).anyMatch(String::isBlank)
                ? PageRequest.of(pageNumber, pageSize)
                : PageRequest.of(pageNumber, pageSize, direction, sortFields);

        Page<CallRecord> page = callService.getCalls(CallType.enumFromString(callType), pageable);

        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("currentPageSize", page.getSize());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("callsData", page.getContent());
        model.addAttribute("reverseSort", reverseSort);
        model.addAttribute("sortFields", sortFields);
        model.addAttribute("type", callType);

        return "index";
    }

    @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStatistics(Model model) {

        model.addAttribute("statistics", callService.getStatistics().getDailyStatisticsMap());
        return "statistics";
    }
}
