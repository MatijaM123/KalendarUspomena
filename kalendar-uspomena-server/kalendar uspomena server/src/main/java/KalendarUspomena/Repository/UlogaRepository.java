package KalendarUspomena.Repository;

import KalendarUspomena.Model.Uloga;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UlogaRepository extends JpaRepository<Uloga, Long> {
  @Query("SELECT u FROM Uloga u WHERE u.naziv = ?1")
  Optional<Uloga> findByNaziv(String naziv);
}
