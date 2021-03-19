package pt.bnurbit.challenge.td;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/td")
public class TdController {

    @RequestMapping
    public String home() {
        return "Hello Docker World";
    }

    
}
