package cn.zzk.test.domain;

import cn.zzk.validator.anntations.DomainRule;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class Person {

    @Min(18)
    private int age;

    @DomainRule("firstName && lastName")
    private FullName fullName;

    public Person(int age, String firstName, String lastName) {
        this.age = age;
        this.fullName = new FullName(firstName, lastName);
    }

    public Person() {

    }
}
