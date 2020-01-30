package de.javahippie.jukebox.testtool;

import de.javahippie.jukebox.annotation.Builder;

import java.time.LocalDate;

@Builder
public record Person(
        String firstName,
        String lastName,
        LocalDate birthDate,
        int heightInCentimeters) {

    public Person {
        if (heightInCentimeters <= 0) {
            throw new IllegalArgumentException("Component 'heightInCentimeters' must be > 0, was %s".formatted(heightInCentimeters));
        }
    }
}
