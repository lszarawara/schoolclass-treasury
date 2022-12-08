package pl.sda.treasury.configuration;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.sda.treasury.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserDetailsServiceAdapter implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetailsAdapter loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .map(UserDetailsAdapter::new)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika " + username));
    }
}
