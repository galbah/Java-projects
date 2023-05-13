public class Game {
    /** this class controls the game rules, running it and finishing it
     */

    private final Board board;
    private int winStreak = 3; // default win streak if not given
    private final Player playerX;
    private final Player playerO;
    private final Renderer renderer; // the kind of board the program prints
    private Mark winner = Mark.BLANK; // saves the winner at end of game - BLANK for tie

    public Game(Player playerX, Player playerO, Renderer renderer){
        board = new Board();
        this.playerX = playerX;
        this.playerO = playerO;
        this.renderer = renderer;
    }

    public Game(Player playerX, Player playerO, int size, int winStreak, Renderer renderer){
        if (winStreak < 2 || winStreak > size){
            winStreak = size;
        }
        this.board = new Board(size);
        this.winStreak = winStreak;
        this.playerX = playerX;
        this.playerO = playerO;
        this.renderer = renderer;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public Mark run(){ // runs game and returns winner's Mark
        renderer.renderBoard(board);
        while(!isGameFinished(Mark.O)){
            playerX.playTurn(board, Mark.X);
            renderer.renderBoard(board);
            if(isGameFinished(Mark.X))
                break;
            playerO.playTurn(board, Mark.O);
            renderer.renderBoard(board);
        }
        return winner;
    }

    /**
     * check if game is finished - could be when board is full or if there is a winner
     * @param lastPlayedMark MArk of player that played last because only he could win in last turn
     * @return true if game is finished, false otherwise
     */
    private boolean isGameFinished(Mark lastPlayedMark){
        Mark mark;
        boolean isBoardFull = true;
        for(int i = 0 ; i < board.getSize() ; i++){
            for(int j = 0 ; j < board.getSize() ; j++){
                if(board.getMark(i,j) == lastPlayedMark ){
                    mark = board.getMark(i,j);
                    if(checkRow(i,j,mark) != Mark.BLANK || checkCol(i,j,mark) != Mark.BLANK ||
                       checkDiag(i,j,mark) != Mark.BLANK || checkAntiDiag(i,j,mark) != Mark.BLANK){
                        winner = mark;
                        return true;
                    }
                }
                if (board.getMark(i,j) == Mark.BLANK)
                    isBoardFull = false;
            }
        }
        return isBoardFull;
    }

    // helper for isGameFinished, checked if there is a win in current row
    private Mark checkRow(int row, int col, Mark mark){
        int streak = 0;
        while(inLimits(row,col) && board.getMark(row, col) == mark){
            streak++;
            col++;
        }
        if (streak == winStreak)
            return mark;
        return Mark.BLANK;
    }

    // helper for isGameFinished, checked if there is a win in current colum
    private Mark checkCol(int row, int col, Mark mark){
        int streak = 0;
        while(inLimits(row,col) && board.getMark(row, col) == mark){
            streak++;
            row++;
        }
        if (streak == winStreak)
            return mark;
        return Mark.BLANK;
    }

    // helper for isGameFinished, checked if there is a win in current diagonal
    private Mark checkDiag(int row, int col, Mark mark){
        int streak = 0;
        while(inLimits(row,col) && board.getMark(row, col) == mark){
            streak++;
            row++;
            col++;
        }
        if (streak == winStreak)
            return mark;
        return Mark.BLANK;
    }

    // helper for isGameFinished, checked if there is a win in current anti-diagonal
    private Mark checkAntiDiag(int row, int col, Mark mark){
        int streak = 0;
        while(inLimits(row,col) && board.getMark(row, col) == mark){
            streak++;
            row++;
            col--;
        }
        if (streak == winStreak)
            return mark;
        return Mark.BLANK;
    }

    // checks if coordinate is in board limits
    boolean inLimits(int row, int col){
        return (row >=0 && row < board.getSize() && col >= 0 && col < board.getSize());
    }
}