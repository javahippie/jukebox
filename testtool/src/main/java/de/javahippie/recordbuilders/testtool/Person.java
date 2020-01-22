package de.javahippie.recordbuilders.testtool;

import de.javahippie.recordbuilders.annotation.Builder;
import java.time.LocalDate;

@Builder
public record Person(
        String firstName,
        String lastName,
        LocalDate birthDate,
        int heightInCentimeters){}
