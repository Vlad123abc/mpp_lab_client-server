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

    @Autowired
    private CursaRepository cursaRepository;

    @RequestMapping("/greeting")
    public  String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return String.format(template, name);
    }

    @RequestMapping(method=RequestMethod.GET)
    public List<Cursa> getAll()
    {
        System.out.println("Get all curse ...");
        return cursaRepository.getAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable String id){
        System.out.println("Get by id " + id);
        Cursa cursa = this.cursaRepository.getById(Long.valueOf(id));
        if (cursa == null)
            return new ResponseEntity<String>("Cursa not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<Cursa>(cursa, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Cursa create(@RequestBody Cursa cursa){
        this.cursaRepository.save(cursa);
        return cursa;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Cursa update(@RequestBody Cursa cursa) {
        System.out.println("Updating cursa ...");
        this.cursaRepository.update(cursa.getId(), cursa);
        return cursa;
    }

    @RequestMapping(value="/{id}", method= RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String id){
        System.out.println("Deleting cursa ... " + id);
        try {
            this.cursaRepository.delete(Long.valueOf(id));
            return new ResponseEntity<Cursa>(HttpStatus.OK);
        }catch (Exception ex){
            System.out.println("Ctrl Delete cursa exception");
            return new ResponseEntity<String>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/max_id")
    public Long getMaxId()
    {
        System.out.println("Get max id ...");
        return cursaRepository.getMaxId();
    }
}
