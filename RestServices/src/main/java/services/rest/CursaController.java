package services.rest;

import org.example.Cursa;
import org.example.repository.CursaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/transport/curse")
public class CursaController
{
    private static final String template = "Hello, %s!";

//    @Autowired
//    private CursaRepository cursaRepository;

    @RequestMapping("/greeting")
    public  String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return String.format(template, name);
    }

//    @RequestMapping( method=RequestMethod.GET)
//    public List<Cursa> getAll()
//    {
//        System.out.println("Get all curse ...");
//        return cursaRepository.getAll();
//    }
}
