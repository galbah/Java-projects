public class Board {
    private int size = 4; // default size if not given
    private Mark[][] board;

    public Board(){
        board = new Mark[4][4];
        for(int i = 0 ; i  < this.size ; i++){
            for(int j = 0 ; j  < this.size ; j++){
                board[i][j] = Mark.BLANK;
            }
        }
    }

    public Board(int size){
        this.size = size;
        this.board = new Mark[size][size];
        for(int i = 0 ; i  < this.size ; i++){
            for(int j = 0 ; j  < this.size ; j++){
                board[i][j] = Mark.BLANK;
            }
        }
    }

    public int getSize(){
        return this.size;
    }

    Mark[][] getBoard(){
        return this.board;
    }

    /** tries to put given mark in given coordinate
     * @return false if mission failed, true otherwise
     */
    public boolean putMark(Mark mark, int row, int col){
        if(board[row][col] == Mark.BLANK){
            board[row][col] = mark;
            return true;
        }
        return false;
    }

    Mark getMark(int row, int col){
        return board[row][col];
    }
}
