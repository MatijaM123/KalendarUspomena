package KalendarUspomena.DTO.Uspomena;

import KalendarUspomena.Model.Uspomena;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Data
public class GetUspomeneResponse {
  List<UspomenaDTO> uspomene;
}
