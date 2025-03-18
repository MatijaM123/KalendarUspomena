package KalendarUspomena.Controller;


import KalendarUspomena.Model.Uspomena;
import KalendarUspomena.Service.UspomenaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uspomene")
public class UspomenaController {
  private  final UspomenaService uspomenaService;


  public UspomenaController(UspomenaService uspomenaService) {
    this.uspomenaService = uspomenaService;
  }

  @GetMapping("/user")
  public ResponseEntity<List<Uspomena>> getSlikeZaUsera(@RequestHeader("Authorization") String jwt) {
    return ResponseEntity.ok(uspomenaService.getAllUspomena(jwt.substring(8)));
  }
}
