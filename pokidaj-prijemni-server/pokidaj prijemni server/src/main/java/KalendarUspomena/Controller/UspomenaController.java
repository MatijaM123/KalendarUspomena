package KalendarUspomena.Controller;


import KalendarUspomena.DTO.Exceptions.ErrorMessage;
import KalendarUspomena.DTO.Uspomena.AddUspomenaRequest;
import KalendarUspomena.DTO.Uspomena.GetUspomeneResponse;
import KalendarUspomena.DTO.Uspomena.UspomenaCountsResponse;
import KalendarUspomena.DTO.Uspomena.UspomenaDTO;
import KalendarUspomena.Model.Uspomena;
import KalendarUspomena.Service.UspomenaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uspomene")
public class UspomenaController {
  private final UspomenaService uspomenaService;

  public UspomenaController(UspomenaService uspomenaService) {
    this.uspomenaService = uspomenaService;
  }

  @GetMapping("/counts")
  public ResponseEntity<?> getBrojUspomenaPoDanu(@RequestHeader("Authorization") String jwt,
                                                 @RequestParam int year,
                                                 @RequestParam int month) {
  try {
    List<Integer> l = uspomenaService.getBrojUspomenaPoDanu(jwt, year, month);
    return ResponseEntity.ok(new UspomenaCountsResponse(l));
  }catch (IllegalArgumentException e){
    return  ResponseEntity.badRequest().body(e.getMessage());
  }catch (Exception e){
    return ResponseEntity.status(500).body(new ErrorMessage("Greška na serveru!").toString());
  }

  }

  @GetMapping("/")
  public ResponseEntity<?> getUspomene(@RequestHeader("Authorization") String jwt,
                                       @RequestParam int year,
                                       @RequestParam int month,
                                       @RequestParam int day){
    try {
      List<UspomenaDTO> l = uspomenaService.getUspomene(jwt, year, month,day);
      return ResponseEntity.ok(new GetUspomeneResponse(l));
    }catch (IllegalArgumentException e){
      return  ResponseEntity.badRequest().body(e.getMessage());
    }catch (Exception e){
      return ResponseEntity.status(500).body(new ErrorMessage("Greška na serveru!").toString());
    }
  }

  @PostMapping("/add")
  public ResponseEntity<?> addUspomena(@RequestHeader("Authorization") String jwt,
      @RequestBody AddUspomenaRequest request) {
    System.out.println(jwt);
    System.out.println(request.getSlika());
    System.out.println(request.getDatum());

    try{
       uspomenaService.save(request,jwt.substring(7));
       return  ResponseEntity.ok("Uspešno dodata uspomena!");
    }catch (IllegalArgumentException e){
      return  ResponseEntity.badRequest().body(e.getMessage());
    }catch (Exception e){
      return ResponseEntity.status(500).body(new ErrorMessage("Greška na serveru!").toString());
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteUspomena(@RequestHeader("Authorization") String jwt,
                                          @RequestParam Long id) {
    try {
      uspomenaService.deleteById(id, jwt.substring(7));
    }catch (IllegalArgumentException e){
      return ResponseEntity.status(400).body(e.getMessage());
    }catch (Exception e){
      return ResponseEntity.status(500).body("Greška na serveru!");
    }
    return null;
  }
}
