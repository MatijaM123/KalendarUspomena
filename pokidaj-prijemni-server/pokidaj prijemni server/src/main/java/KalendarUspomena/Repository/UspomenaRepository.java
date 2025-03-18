package KalendarUspomena.Repository;
import KalendarUspomena.Model.Korisnik;
import KalendarUspomena.Model.Uspomena;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UspomenaRepository extends JpaRepository<Uspomena, Korisnik> {
  List<Uspomena> findByKorisnik(Korisnik korisnik);
}
