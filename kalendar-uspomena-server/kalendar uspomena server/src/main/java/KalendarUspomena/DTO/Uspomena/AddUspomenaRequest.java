package KalendarUspomena.DTO.Uspomena;


import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AddUspomenaRequest {
  private Date datum;
  private String slika;
}
