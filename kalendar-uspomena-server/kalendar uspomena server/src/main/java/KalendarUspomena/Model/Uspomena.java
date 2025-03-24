package KalendarUspomena.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "Uspomena")
@Builder
public class Uspomena {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String opis;
  @Column(columnDefinition = "TEXT")
  private String slika; // URL slike ili Base64 enkodiran string


  private Date datum;

  @ManyToOne
  @JoinColumn(name = "korisnik_id", nullable = false)
  private Korisnik korisnik;
}
