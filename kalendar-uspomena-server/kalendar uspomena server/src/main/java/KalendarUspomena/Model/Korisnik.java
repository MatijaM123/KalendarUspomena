package KalendarUspomena.Model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;
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
@Table(name = "Korisnik")
@Builder
public class Korisnik {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_korisnik")
  private Long id;
  @Column(name = "username", nullable = false, unique = true, length = 50)
  private String username;
  @Column(name = "ime", nullable = false, length = 30)
  private String ime;
  @Column(name = "prezime", nullable = false, length = 30)
  private String prezime;
  @Column(name = "email", nullable = false, unique = true, length = 50)
  private String email;
  @Column(name = "password")
  private String password;
  @Column(name = "datum_prijave")
  @Temporal(TemporalType.DATE)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.mm.yyyy")
  private Date dateCreated;

  @ManyToOne
  @JoinColumn(name = "uloga_id")
  private Uloga uloga;

  @OneToMany(mappedBy = "korisnik", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Uspomena> uspomene;
}
