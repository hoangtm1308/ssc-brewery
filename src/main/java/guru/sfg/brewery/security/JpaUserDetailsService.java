package guru.sfg.brewery.security;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("--------------- Getting user info via JPA ---------------------------");

        return userRepository.findByUsername(username).orElseThrow(() -> {
            return new UsernameNotFoundException("User name" + username +"not found");
        });

//        return new org.springframework.security.core.userdetails.User(user.getUsername(),
//                user.getPassword(),
//                user.isEnabled(),
//                user.isAccountNonExpired(),
//                user.isCredentialsNonExpired(),
//                user.isAccountNonLocked(), user.getAuthorities());
    }

    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities) {
        if(authorities != null && authorities.size() > 0){
            Set<GrantedAuthority> grantedAuthoritySet = authorities.stream()
                    .map(authority -> authority.getPermission()) //Can use Method Reference
                    .map(role -> new SimpleGrantedAuthority(role)) //Can use Method Reference
                    .collect(Collectors.toSet());
            return grantedAuthoritySet;
        }else {
            return new HashSet<>();
        }
    }

}
