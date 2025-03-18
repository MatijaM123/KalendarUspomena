package KalendarUspomena.Model;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "uloge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Uloga {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private  Long idUloga;
  private  String naziv;
}
