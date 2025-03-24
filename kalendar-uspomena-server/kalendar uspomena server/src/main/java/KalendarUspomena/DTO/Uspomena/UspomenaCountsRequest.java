package KalendarUspomena.DTO.Uspomena;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Data
public class UspomenaCountsRequest {
  int year;
  int month;
}
