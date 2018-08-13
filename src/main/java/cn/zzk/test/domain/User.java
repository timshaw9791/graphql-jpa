package cn.zzk.test.domain;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Data
public class User {

    @Email
    private String email;

    @Length(min = 11, max = 11)
    private String phone;

    @Length(min = 2, max = 8)
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String email, String phone, String password) {
        this.email = email;
        this.phone = phone;
        this.password = password;

    }

    public User(){

    }
}
