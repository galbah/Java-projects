public class PlayerFactory {
    /** this class controls building Players for game,
     * it returns new variable of type Player according to given name.
     */

    public Player buildPlayer(String playerName) {

        switch (playerName) {

            case ("human"):
                return new HumanPlayer();
            case ("clever"):
                return new CleverPlayer();
            case ("genius"):
                return new GeniusPlayer();
            case ("whatever"):
                return new WhateverPlayer();
            default:
                return null;
        }
    }
}
