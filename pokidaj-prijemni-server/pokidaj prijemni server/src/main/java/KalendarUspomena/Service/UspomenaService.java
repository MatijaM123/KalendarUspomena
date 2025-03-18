package KalendarUspomena.Service;


import KalendarUspomena.DTO.Uspomena.UspomenaIn;
import KalendarUspomena.Model.Korisnik;
import KalendarUspomena.Model.Uspomena;
import KalendarUspomena.Repository.KorisnikRepository;
import KalendarUspomena.Repository.UspomenaRepository;
import KalendarUspomena.Util.JwtUtil;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UspomenaService {
  private final UspomenaRepository uspomenaRepository;
  private  final KorisnikRepository korisnikRepository;
  private final JwtUtil jwtUtil;

  public UspomenaService(UspomenaRepository uspomenaRepository,
                         KorisnikRepository korisnikRepository, JwtUtil jwtUtil) {
    this.uspomenaRepository = uspomenaRepository;
    this.korisnikRepository = korisnikRepository;
    this.jwtUtil = jwtUtil;
  }

  // Čuvanje slike u bazi
  public Uspomena saveUspomena(UspomenaIn uspomenaIn) throws IOException {
    Uspomena uspomena = new Uspomena();
    uspomena.setImage(Base64.getDecoder().decode(uspomenaIn.getImage())); // Pretvaranje u byte[]
    uspomena.setDateCreated(uspomenaIn.getDate());
    uspomena.setKorisnik(korisnikRepository.findByUsername(jwtUtil.extractUsername(uspomenaIn.getJwt())));
    return  uspomenaRepository.save(uspomena);
  }

  // Dohvatanje svih slika u Base64 formatu
  public List<Uspomena> getAllUspomena(String jwt) {
    return uspomenaRepository.findByKorisnik(korisnikRepository.findByUsername(jwtUtil.extractUsername(jwt)));
  }

}
