package de.javahippie.recordbuilders.testtool;

import de.javahippie.recordbuilders.annotation.Builder;

@Builder
public record Game(
        Person player1,
        Person player2,
        int pointsPlayer1,
        int pointsPlayer2){

        }
