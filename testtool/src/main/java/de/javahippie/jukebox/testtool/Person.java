package de.javahippie.jukebox.testtool;

import de.javahippie.jukebox.annotation.Builder;
import java.time.LocalDate;

@Builder
public record Person(
        String firstName,
        String lastName,
        LocalDate birthDate,
        int heightInCentimeters){}
