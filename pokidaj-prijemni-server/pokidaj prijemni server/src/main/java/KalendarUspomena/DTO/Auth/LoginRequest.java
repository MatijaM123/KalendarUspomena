package KalendarUspomena.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
public class LoginRequest {
  private String identificator;
  private String password;
}
