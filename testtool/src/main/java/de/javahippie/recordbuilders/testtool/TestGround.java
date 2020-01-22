package de.javahippie.recordbuilders.testtool;

import de.javahippie.recordbuilders.testtool.PersonBuilder;

import java.time.LocalDate;

public class TestGround {

    public static void main(String... args) {

        var person = PersonBuilder.builder()
                .withFirstName("Tim")
                .withLastName("Zöller")
                .withBirthDate(LocalDate.of(1989, 2, 2))
                .withHeightInCentimeters(186)
                .build();

        System.out.println(person);
    }
}
