package KalendarUspomena.DTO.Uspomena;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class UspomenaIn{
  String image;
  Date   date;
  String jwt;
}
