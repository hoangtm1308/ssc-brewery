package guru.sfg.brewery.domain.security;

import guru.sfg.brewery.domain.Customer;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User implements UserDetails, CredentialsContainer {

    //Tạo user tái hiện từ User của Spring Security
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String password;
    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    //từ Set<Role> của User cho vào stream để lấy các Set<Authority> của từng role cho vào thành Stream của các Set<Authority>
    // dùng flatMap biến đổi thành 1 Set các Authority duy nhất
    @Transient
    public Set<GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> role.getAuthorities())
                .flatMap(authorities -> authorities.stream())
                .map(authority -> {
                    return new SimpleGrantedAuthority(authority.getPermission());
                })
                .collect(Collectors.toSet());
    }

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    private Set<Role> roles;

    @Builder.Default //Khi dùng builder mà không set giá trị sẽ mặc định để giá trị này
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean userGoogle2fa = false;

    private String google2FaSecret;

    @Transient
    private Boolean google2faRequired = true;

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;
}
