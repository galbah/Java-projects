import java.util.Scanner;

public class HumanPlayer implements Player {
    /** this class controls human player moves in game,
     * receives the coordinates from user and tries to put a mark on board.
     */

    private static final Scanner scanner = new Scanner(System.in);

    public HumanPlayer(){}

    @Override
    public void playTurn(Board board, Mark mark) {
        boolean invalidCoords = true;
        System.out.println("Player "+ mark +", type coordinates: ");
        while(invalidCoords) {
            int coords = scanner.nextInt();
            int row = coords / 10;
            int col = coords % 10;
            if (row < 0 || row >= board.getSize() || col < 0 ||
                    col >= board.getSize() || !board.putMark(mark, row, col)) {
                System.out.println("Invalid coordinates, type again");
            }
            else{
                invalidCoords = false;
                board.putMark(mark, row, col);
            }
        }
    }

}
