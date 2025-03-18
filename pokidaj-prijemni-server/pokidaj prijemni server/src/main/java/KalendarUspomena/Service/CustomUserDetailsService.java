package KalendarUspomena.Service;

import KalendarUspomena.Model.Korisnik;
import KalendarUspomena.Repository.KorisnikRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final KorisnikRepository korisnikRepository;

  public CustomUserDetailsService(KorisnikRepository korisnikRepository) {
    this.korisnikRepository = korisnikRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    Korisnik korisnik = korisnikRepository.findByEmail(identifier);
    if(korisnik==null) {
      korisnik = korisnikRepository.findByUsername(identifier);
      if(korisnik==null) throw new UsernameNotFoundException("Korisnik nije pronađen sa identifikatorom: " + identifier);
    }
    return User.builder()
        .username(korisnik.getUsername())
        .password(korisnik.getPassword())
        .roles(korisnik.getUloga().getNaziv())
        .build();
  }

}
