package guru.sfg.brewery.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

@Slf4j
public class PasswordEncodingTest {

    static final String PASSWORD = "hoangtm";

    @Test
    void hashingExample(){
        //using MD5 to hash value
        log.info(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));

        //Adding hash value to password and hashing using MD5 encoding
        String salted = PASSWORD + "ThisIsMySALTVALUE";
        log.info(DigestUtils.md5DigestAsHex(salted.getBytes()));
    }
}
