public class GeniusPlayer implements Player {
    /** this class controls the genius BOT Player moves in game
     */

    @Override
    public void playTurn(Board board, Mark mark) {
        for(int i = 1 ; i < board.getSize() ; i++){
            for(int j = 0 ; j < board.getSize() ; j++){
                if(board.putMark(mark, j, i))
                    return;
            }
        }
        for(int i = 0 ; i < board.getSize() ; i++){
            if(board.putMark(mark, i, 0))
                return;
        }
    }


}
