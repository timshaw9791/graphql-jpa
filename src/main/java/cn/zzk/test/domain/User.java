package cn.zzk.test.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Getter
@Setter
public class User {

    @Email
    private String email;

    @Length(min = 11, max = 11)
    private String phone;

    @Length(min = 2, max = 8)
    private String password;


    public User(String email, String phone, String password) {
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
