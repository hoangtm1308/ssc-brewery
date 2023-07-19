package guru.sfg.brewery.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.DigestUtils;

import static org.junit.Assert.assertTrue;

@Slf4j
public class PasswordEncodingTest {

    static final String PASSWORD = "hoangtm";

    @Test
    void hashingExampleWithMD5(){
        //using MD5 to hash value
        //Spring Security giữ để support những hệ thống cổ
        log.info(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));

        //Adding hash value to password and hashing using MD5 encoding
        String salted = PASSWORD + "ThisIsMySALTVALUE";
        log.info(DigestUtils.md5DigestAsHex(salted.getBytes()));
    }

    @Test
    void testNoOpEncoding(){
        //Spring Security giữ để support những hệ thống cổ
        PasswordEncoder noOp = NoOpPasswordEncoder.getInstance();
        log.info(noOp.encode(PASSWORD));
    }

    @Test
    void testLDAPEncoding(){
        PasswordEncoder ldap = new LdapShaPasswordEncoder();

        log.info(ldap.encode(PASSWORD));
        log.info(ldap.encode(PASSWORD));

        String encodePwd = ldap.encode(PASSWORD);

        Assertions.assertTrue(ldap.matches(PASSWORD,encodePwd));
    }

    @Test
    void testSha256(){
        PasswordEncoder sha256 = new StandardPasswordEncoder();

        log.info(sha256.encode(PASSWORD));
        log.info(sha256.encode(PASSWORD));
    }

    @Test
    void testBcrypt(){
        PasswordEncoder bcrypt = new BCryptPasswordEncoder();

        log.info(bcrypt.encode(PASSWORD));
        log.info(bcrypt.encode(PASSWORD));
    }

    @Test
    void testBcrypt15(){
        PasswordEncoder bcrypt = new BCryptPasswordEncoder(15);

        log.info(bcrypt.encode(PASSWORD));
        log.info(bcrypt.encode(PASSWORD));
        log.info(bcrypt.encode("tiger"));
    }
}
