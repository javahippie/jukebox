package net.javahippie.jukebox.testtool;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BuilderTest {

    public static final String PERSON_1_FIRST_NAME = "Rüdiger";
    public static final String PERSON_1_LAST_NAME = "Behrens";
    public static final LocalDate PERSON_1_BIRTH_DATE = LocalDate.of(1982, 3, 17);
    public static final int PERSON_1_HEIGHT = 186;
    public static final int PERSON_1_POINTS = 10;

    public static final String PERSON_2_FIRST_NAME = "Frank";
    public static final String PERSON_2_LAST_NAME = "Meier";
    public static final LocalDate PERSON_2_BIRTH_DATE = LocalDate.of(1990, 2, 2);
    public static final int PERSON_2_HEIGHT = 170;
    public static final int PERSON_2_POINTS = 20;

    @Test
    public void testBuilderFields() {
        var game = GameBuilder.builder()
                .player1(PersonBuilder.builder()
                        .firstName(PERSON_1_FIRST_NAME)
                        .lastName(PERSON_1_LAST_NAME)
                        .birthDate(PERSON_1_BIRTH_DATE)
                        .heightInCentimeters(PERSON_1_HEIGHT)
                        .build())
                .player2(PersonBuilder.builder()
                        .firstName(PERSON_2_FIRST_NAME)
                        .lastName(PERSON_2_LAST_NAME)
                        .birthDate(PERSON_2_BIRTH_DATE)
                        .heightInCentimeters(PERSON_2_HEIGHT)
                        .build())
                .pointsPlayer1(PERSON_1_POINTS)
                .pointsPlayer2(PERSON_2_POINTS)
                .build();

        assertEquals(PERSON_1_FIRST_NAME, game.player1().firstName());
        assertEquals(PERSON_1_LAST_NAME, game.player1().lastName());
        assertEquals(PERSON_1_BIRTH_DATE, game.player1().birthDate());
        assertEquals(PERSON_1_HEIGHT, game.player1().heightInCentimeters());
        assertEquals(PERSON_1_POINTS, game.pointsPlayer1());

        assertEquals(PERSON_2_FIRST_NAME, game.player2().firstName());
        assertEquals(PERSON_2_LAST_NAME, game.player2().lastName());
        assertEquals(PERSON_2_BIRTH_DATE, game.player2().birthDate());
        assertEquals(PERSON_2_HEIGHT, game.player2().heightInCentimeters());
        assertEquals(PERSON_2_POINTS, game.pointsPlayer2());
    }

    @Test
    public void testRecordValidation() {
        var personBuilder = PersonBuilder.builder()
                .firstName(PERSON_1_FIRST_NAME)
                .lastName(PERSON_1_LAST_NAME)
                .birthDate(PERSON_1_BIRTH_DATE)
                .heightInCentimeters(0);

        assertThrows(IllegalArgumentException.class, personBuilder::build);
    }

}
