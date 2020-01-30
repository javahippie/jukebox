package de.javahippie.jukebox.testtool;

import de.javahippie.jukebox.annotation.Builder;

@Builder
public record Game(
        Person player1,
        Person player2,
        int pointsPlayer1,
        int pointsPlayer2){

        }
