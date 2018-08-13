package cn.zzk.test.domain;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class FullName {

    @Length(min = 1, max = 2)
    private String firstName;

    @Length(min = 1, max = 3)
    private String lastName;

    public FullName(@Length(min = 1, max = 2) String firstName, @Length(min = 1, max = 3) String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
