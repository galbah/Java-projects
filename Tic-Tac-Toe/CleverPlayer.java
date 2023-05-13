public class CleverPlayer implements Player{
    /** this class controls the clever BOT Player moves in game
     */

    @Override
    public void playTurn(Board board, Mark mark) {
        for(int i = 0 ; i < board.getSize() ; i++){
            for(int j = 0 ; j < board.getSize() ; j++){
                if(board.putMark(mark, i, j))
                    return;
            }
        }
    }


}
