
public class Tournament{
    /** this class controls running a new tournament, keeping score,
     * building players and board according to given arguments.
     * at the end it prints the total score
     */

    private final int rounds;
    private final Renderer renderer;
    private final Player[] players;
    private final int[] results = new int[] {0,0,0};

    public static void main(String[] args) {
        int rounds = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);
        int winStreak = Integer.parseInt(args[2]);
        RendererFactory rendererFactory = new RendererFactory();
        Renderer renderer = rendererFactory.buildRenderer(args[3], size);
        if (renderer == null){
            System.err.print("renderer name is not legal");
            return;
        }
        PlayerFactory playerFactory = new PlayerFactory();
        Player player1 = playerFactory.buildPlayer(args[4].toLowerCase());
        Player player2 = playerFactory.buildPlayer(args[5].toLowerCase());
        if (player2 == null || player1 == null){
            System.err.print("player name is not legal");
            return;
        }
        Tournament tournament = new Tournament(rounds, renderer, new Player[]{player1, player2});
        tournament.playTournament(size, winStreak,
            new String[]{args[4].toLowerCase(), args[5].toLowerCase()});
    }

    public Tournament(int rounds, Renderer renderer, Player[] players){
        this.players = players;
        this.renderer = renderer;
        this.rounds = rounds;
    }

    /** runs a new tournament
     * @param size size of board for new tournament
     * @param winStreak the size of streak a player should get to win a game
     * @param playerNames array with players names
     */
    void playTournament(int size, int winStreak, String[] playerNames){
        for (int i = 0 ; i < rounds ; i++){
            Game game = new Game(players[i % 2], players[(i + 1) % 2], size, winStreak, renderer);
            Mark winnerMark = game.run();
            if (winnerMark == Mark.X){
                results[i%2]++;
            }
            else if (winnerMark == Mark.O) {
                results[(i+1)%2]++;
            }
            else{
                results[2]++;
            }
        }
        printResults(playerNames);
    }

    // prints results of tournament
    private void printResults(String[] playerNames){
        System.out.println("######### Results #########");
        System.out.println("Player 1, " +playerNames[0]+ " won: "+results[0]+" rounds");
        System.out.println("Player 2, " +playerNames[1]+ " won: "+results[1]+" rounds");
        System.out.println("Ties: "+results[2]);
    }


}
