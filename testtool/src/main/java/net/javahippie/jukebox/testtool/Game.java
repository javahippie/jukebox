package net.javahippie.jukebox.testtool;

import net.javahippie.jukebox.annotation.Builder;

@Builder
public record Game(
        Person player1,
        Person player2,
        int pointsPlayer1,
        int pointsPlayer2){

        }
