package com.coherentsolutions.java.restapi.testing;

import java.util.Optional;

public class User {
    private final Optional<Integer> age;
    private final String name;
    private final Sex sex;
    private final Optional<String> zipCode;

    public User(Optional<Integer> age, String name, Sex sex, Optional<String> zipCode) {
        this.age = age;
        this.name = name;
        this.sex = sex;
        this.zipCode = zipCode;
    }
    public Optional<Optional<Integer>> getAge() {
        return Optional.ofNullable(age);
    }
    public String getName() {
        return name;
    }

    public Sex getSex() {
        return sex;
    }

    public Optional<Optional<String>> getZipCode() {
        return Optional.ofNullable(zipCode);
    }
    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
