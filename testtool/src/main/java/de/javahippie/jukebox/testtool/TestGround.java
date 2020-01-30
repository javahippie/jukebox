package de.javahippie.jukebox.testtool;

import java.time.LocalDate;

public class TestGround {

    public static void main(String... args) {

        var game = GameBuilder.builder()
                .withPlayer1(PersonBuilder.builder()
                        .withFirstName("Rüdiger")
                        .withLastName("Behrens")
                        .withBirthDate(LocalDate.of(1982, 3, 17))
                        .withHeightInCentimeters(186)
                        .build())
                .withPlayer2(PersonBuilder.builder()
                        .withFirstName("Frank")
                        .withLastName("Meier")
                        .withBirthDate(LocalDate.of(1990, 2, 2))
                        .withHeightInCentimeters(170)
                        .build())
                .withPointsPlayer1(10)
                .withPointsPlayer2(20)
                .build();

        System.out.println(game);
    }
}
