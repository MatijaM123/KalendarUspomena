package KalendarUspomena.Service;

import KalendarUspomena.DTO.Uspomena.AddUspomenaRequest;
import KalendarUspomena.DTO.Uspomena.UspomenaDTO;
import KalendarUspomena.Model.Korisnik;
import KalendarUspomena.Model.Uspomena;
import KalendarUspomena.Repository.UspomenaRepository;
import KalendarUspomena.Util.JwtUtil;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UspomenaService {
  private final UspomenaRepository uspomenaRepository;
  private  final  JwtUtil jwtUtil;

  public List<Integer> getBrojUspomenaPoDanu(String token, int year, int month) throws  Exception{
      Korisnik k = jwtUtil.extractUser(token.substring(7));
      List<Object[]> rezultati = uspomenaRepository.findBrojUspomenaPoDanu(k.getId(), year, month);
    return rezultati.stream()
        .map(r -> ((Number) r[1]).intValue()) // r[1] je broj uspomena
        .toList();
  }

  public Uspomena save(AddUspomenaRequest request, String token) throws Exception {
    Korisnik k = jwtUtil.extractUser(token);
    Uspomena u = Uspomena.builder()
        .datum(request.getDatum())
        .slika(request.getSlika())
        .korisnik(k)
        .build();
    return uspomenaRepository.save(u);
  }

  public Optional<Uspomena> findById(Long id) {
    return uspomenaRepository.findById(id);
  }

  public void deleteById(Long id,String jwt) throws  Exception {
      String username = jwtUtil.extractUsername(jwt);
      Uspomena u = uspomenaRepository.getUspomena(id);
      if(Objects.equals(username, u.getKorisnik().getUsername()))
        uspomenaRepository.deleteById(id);
      else throw new IllegalArgumentException("You are not authorized to do this!");
  }

  public List<UspomenaDTO> getUspomene(String jwt, int year, int month, int day) throws Exception {
    Korisnik k = jwtUtil.extractUser(jwt.substring(7));
    List<Uspomena> rezultati = uspomenaRepository.getUspomenePoDanu(k.getId(), year, month,day);
    return rezultati.stream().map(r -> new UspomenaDTO(r.getSlika(),r.getId())).toList();
  }
}
