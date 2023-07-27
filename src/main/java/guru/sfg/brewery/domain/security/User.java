package guru.sfg.brewery.domain.security;

import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    //Tạo user tái hiện từ User của Spring Security
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String password;
    private String username;

    @Transient
    private Set<Authority> authorities;

    //từ Set<Role> của User cho vào stream để lấy các Set<Authority> của từng role cho vào thành Stream của các Set<Authority>
    // dùng flatMap biến đổi thành 1 Set các Authority duy nhất
    public Set<Authority> getAuthorities() {
        return this.roles.stream()
                .map(role -> role.getAuthorities())
                .flatMap(authorities1 -> authorities1.stream())
                .collect(Collectors.toSet());
    }

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
    inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    private Set<Role> roles;

    @Builder.Default //Khi dùng builder mà không set giá trị sẽ mặc định để giá trị này
    private boolean accountNonExpired = true;

    @Builder.Default
    private boolean accountNonLocked = true;

    @Builder.Default
    private boolean credentialsNonExpired = true;

    @Builder.Default
    private boolean enabled = true;
}
