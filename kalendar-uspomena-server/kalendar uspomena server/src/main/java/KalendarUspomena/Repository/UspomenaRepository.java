package KalendarUspomena.Repository;
import KalendarUspomena.Model.Uspomena;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UspomenaRepository extends JpaRepository<Uspomena, Long> {

  @Query(value = """
    SELECT d.dan, COALESCE(COUNT(u.id), 0) AS broj_uspomena
    FROM generate_series(1, CAST(DATE_PART('days', 
           DATE_TRUNC('month', make_date(:year, :month, 1)) + INTERVAL '1 month - 1 day') AS INTEGER)) AS d(dan)
    LEFT JOIN uspomena u 
        ON u.korisnik_id = :idKorisnik 
        AND u.datum >= make_date(:year, :month, d.dan) 
        AND u.datum < make_date(:year, :month, d.dan) + INTERVAL '1 day' - INTERVAL '1 second'
    GROUP BY d.dan
    ORDER BY d.dan
    """, nativeQuery = true)
  List<Object[]> findBrojUspomenaPoDanu(@Param("idKorisnik") Long idKorisnik,
                                        @Param("year") int year,
                                        @Param("month") int month);

  @Query(value = """
      Select *
      from uspomena u 
      WHERE u.datum BETWEEN make_date(:year, :month, :day)
                            AND make_date(:year, :month, :day) + INTERVAL '1 day' - INTERVAL '1 second'
      and u.korisnik_id = :idKorisnik
      """,nativeQuery = true)
  List<Uspomena> getUspomenePoDanu(@Param("idKorisnik") Long idKorisnik,
                                   @Param("year") int year,
                                   @Param("month") int month,
                                   @Param("day") int day);


  @Query(value = """
      Select * from uspomena u  where u.id = :id;
      """, nativeQuery = true)
  Uspomena getUspomena(@Param("id") Long id);
}
