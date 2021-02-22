import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Tetris extends JPanel {
    private Color[][] board;
    private Color[][] nextShapeBoard = new Color[4][4];
    private Color[][] holdingShapeBoard = new Color[4][4];
    private final int rows = 24;
    private final int cols = 12;
    private int score = 0;
    private Queue<Boolean[][]> nextShapes = new Queue<>(5);     //Queue to access the next pieces
    private Queue<Integer> nextColors = new Queue<>(5);         //Queue to keep track of colors of next pieces
    private final Boolean[][] I_PIECE = {
            {true, true, true, true}
    };
    private final Boolean[][] J_PIECE = {
            {true, false, false},
            {true, true, true}
    };
    private final Boolean[][] L_PIECE = {
            {false, false, true},
            {true, true, true}
    };
    private final Boolean[][] O_PIECE = {
            {true, true},
            {true, true}
    };
    private final Boolean[][] S_PIECE = {
            {false, true, true},
            {true, true, false}
    };
    private final Boolean[][] Z_PIECE = {
            {true, true, false},
            {false, true, true}
    };
    private final Boolean[][] T_PIECE = {
            {false, true, false},
            {true, true, true}
    };
    private Boolean[][][] TETRIS_PIECES = {
            I_PIECE, J_PIECE, L_PIECE, O_PIECE, S_PIECE, Z_PIECE, T_PIECE
    };
    private Color[] TETRIS_PIECES_COLORS = {
            Color.cyan, Color.orange, Color.pink, Color.yellow, Color.red, Color.green, Color.magenta
    };
    private Integer currentShape;
    private Integer holdingColor;
    private Boolean[][] currentPiece;
    private Boolean[][] holdingPiece;
    private int curX;
    private int curY;
    private boolean holdUsed = false;

    /**
     * starts the game
     */
    public void initializeBoard() {
        holdingPiece = null;
        holdingColor = null;
        Integer randNum;
        for (int i = 0; i < 5; i++) {
            Random random = new Random();
            randNum = random.nextInt(TETRIS_PIECES_COLORS.length);
            nextColors.enqueue(randNum);
            nextShapes.enqueue(TETRIS_PIECES[randNum]);

        }
        board = new Color[rows][cols];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (j == 0 || j == 11 || i == 23) {
                    board[i][j] = Color.BLACK;
                } else
                    board[i][j] = Color.BLUE;
            }
        }
        newFallingPiece();
    }

    /**
     * generates new shape either when game starts or when shape can't move down
     */
    private void newFallingPiece(){
        currentShape = nextColors.dequeue();
        currentPiece = nextShapes.dequeue();
        Random random = new Random();
        Integer randNum = random.nextInt(TETRIS_PIECES.length);
        nextColors.enqueue(randNum);
        nextShapes.enqueue(TETRIS_PIECES[randNum]);
        fillNextShapeBox(nextShapes.front(), nextColors.front());
        curX = 6-TETRIS_PIECES[currentShape][0].length/2;
        curY = 0;
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[0].length; j++) {
                if (currentPiece[i][j]){
                    board[i][curX+j] = TETRIS_PIECES_COLORS[currentShape];
                }
            }
        }
        repaint();
    }

    /**
     * sets the color for the box that displays next shape
     * @param shape next piece
     * @param num index of the shape - to keep the color constant
     */
    public void fillNextShapeBox(Boolean[][] shape, Integer num){
        for (int i = 0; i < nextShapeBoard.length; i++) {
            for (int j = 0; j < nextShapeBoard[0].length; j++) {
                nextShapeBoard[i][j] = Color.white;
            }
        }
        for (int i = 0; i < shape.length; i++){
            for (int j = 0; j < shape[0].length; j++) {
                if (TETRIS_PIECES[num][i][j])
                    nextShapeBoard[i+1][j] = TETRIS_PIECES_COLORS[num];
            }
        }
    }

    /**
     * allows players to hold the piece and use the either the stored or the new shape
     */
    public void hold(){
        eraseFallingPiece();
        if (holdingPiece == null){
            holdingPiece = currentPiece;
            holdingColor = currentShape;
            newFallingPiece();
        } else{
            Boolean[][] tempShape = currentPiece;
            Integer tempInt = currentShape;
            currentPiece = holdingPiece;
            currentShape = holdingColor;
            holdingPiece = tempShape;
            holdingColor = tempInt;
            curX = 6-TETRIS_PIECES[currentShape][0].length/2;
            curY = 0;
        }
        fillHoldingShapeBox();
        repaint();
        holdUsed = true;
    }

    /**
     * sets the color for the box that displays holding shape
     */
    private void fillHoldingShapeBox(){
        for (int i = 0; i < holdingShapeBoard.length; i++) {
            for (int j = 0; j < holdingShapeBoard[0].length; j++) {
                holdingShapeBoard[i][j] = Color.white;
            }
        }
        if (holdingPiece != null){
            for (int i = 0; i < TETRIS_PIECES[holdingColor].length; i++) {
                for (int j = 0; j < TETRIS_PIECES[holdingColor][0].length; j++) {
                    if (TETRIS_PIECES[holdingColor][i][j]){
                        holdingShapeBoard[i+1][j] = TETRIS_PIECES_COLORS[holdingColor];
                    }
                }
            }
        }
    }


    /**
     * erase the shape before changing the location
     */
    private void eraseFallingPiece(){
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[0].length; j++) {
                if (currentPiece[i][j]){
                    board[curY+i][curX+j] = Color.blue;
                }
            }
        }
    }

    /**
     * moves the shape in entered location
     * @param dRow change in vertical
     * @param dCol change in horizontal
     */
    public void moveFallingPiece(int dRow, int dCol){
        eraseFallingPiece();
        curY = curY + dRow;
        curX = curX + dCol;
        if (collidesAt()){
            curY = curY - dRow;
            curX = curX - dCol;
        } else{
            if(dRow == 1)
                score = score + 1;
        }
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[0].length; j++) {
                if (currentPiece[i][j]) {
                    board[curY+i][curX+j] = TETRIS_PIECES_COLORS[currentShape];
                }
            }
        }
        repaint();
    }

    /**
     * drops the shape to the lowest possible location
     */
    public void hardDrop(){
        eraseFallingPiece();
        curY = curY + 1;
        while (!collidesAt()){
            curY += 1;
        }
        curY = curY - 1;
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[0].length; j++) {
                if (currentPiece[i][j]) {
                    board[curY+i][curX+j] = TETRIS_PIECES_COLORS[currentShape];
                }
            }
        }
        repaint();
        score = score + 10;
        clearRows();
    }

    /**
     * drops the shape one row down
     */
    public void dropDown(){
        eraseFallingPiece();
        curY = curY + 1;
        if (collidesAt()){
            curY = curY - 1;
            for (int i = 0; i < currentPiece.length; i++) {
                for (int j = 0; j < currentPiece[0].length; j++) {
                    if (currentPiece[i][j]) {
                        board[curY+i][curX+j] = TETRIS_PIECES_COLORS[currentShape];
                    }
                }
            }
            repaint();
            clearRows();
        }
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[0].length; j++) {
                if (currentPiece[i][j]) {
                    board[curY+i][curX+j] = TETRIS_PIECES_COLORS[currentShape];
                }
            }
        }
        repaint();
    }

    /**
     * rotates the piece
     */
    public void rotateFallingPiece(){
        eraseFallingPiece();
        Boolean[][] tempPiece = currentPiece;
        currentPiece = new Boolean[tempPiece[0].length][tempPiece.length];
        for (int i = 0; i < tempPiece.length; i++) {
            for (int j = 0; j < tempPiece[0].length; j++) {
                currentPiece[j][currentPiece[0].length-1-i] = tempPiece[i][j];
            }
        }
        if(collidesAt()) {
            currentPiece = tempPiece;
        }
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[0].length; j++) {
                if (currentPiece[i][j]) {
                    board[curY + i][curX + j] = TETRIS_PIECES_COLORS[currentShape];
                }
            }
        }
        repaint();
    }

    /**
     * checks if the shape can be drawn in the location
     * @return false if move possible
     */
    private boolean collidesAt() {
        if (curX == 0 || curX == board[0].length-1 || curY < 0 || curY == board.length-1){
            return true;
        }
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[0].length; j++) {
                if (currentPiece[i][j] && board[curY + i][curX + j] != Color.blue) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * draw the whole game
     * @param graphics
     */
    public void paint(Graphics graphics) {
        graphics.fillRect(0, 0, 25, 25);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                graphics.setColor(board[i][j]);
                graphics.fillRect(j * 26, i * 26, 25, 25);
            }
        }
        for (int i = 0; i < nextShapeBoard.length; i++) {
            for (int j = 0; j < nextShapeBoard[0].length; j++) {
                graphics.setColor(nextShapeBoard[i][j]);
                graphics.fillRect(j * 26 + 317 + 5, (i + 1) * 26, 25, 25);
            }
        }
        for (int i = 0; i < holdingShapeBoard.length; i++) {
            for (int j = 0; j < holdingShapeBoard[0].length; j++) {
                graphics.setColor(holdingShapeBoard[i][j]);
                graphics.fillRect(j * 26 + 317, (i+6) * 26, 25, 25);
            }

        }
        Font font = new Font("Comic Sans MS ", Font.BOLD, 15);
        graphics.setFont(font);
        graphics.setColor(Color.yellow);
        graphics.drawString("Score: " + score, 210, 25);
        graphics.setColor(Color.BLACK);
        graphics.drawString("Next Piece", 330, 20);
        graphics.drawString("Holding Piece", 330, 20 + 5*26);
    }

    /**
     * checks if the shapes overlap on spawn location
     * @return if game is over
     */
    public boolean endGame(){
        if (board[1][6] == Color.blue){
            return false;
        }
        return true;
    }

    /**
     * moves the all the rows above the row entered one row down
     * @param row row to be cleared
     */
    private void deleteRow(int row){
        for (int i = row-1; i > 0 ; i--) {
            for (int j = 0; j < board[0].length; j++) {
                board[i+1][j] = board[i][j];
            }
        }
    }

    /**
     * clears the row when the whole row isn't blue
     */
    private void clearRows(){
        holdUsed = false;
        boolean gap;
        int numRows = 0;
        for (int i = 0; i < board.length-1; i++) {
            gap = false;
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == Color.blue){
                    gap = true;
                    break;
                }
            }
            if (!gap){
                deleteRow(i);
                numRows+=1;
            }
        }
        switch (numRows){
            case 1:
                score = score + 100;
                break;
            case 2:
                score = score + 250;
                break;
            case 3:
                score = score + 375;
                break;
            case 4:
                score = score + 500;
                break;
        }
        if(!endGame()){
            newFallingPiece();
        } else
            gameOver();
    }

    /**
     * pops up message when the game is over while also showing the score
     */
    public void gameOver(){
        JOptionPane.showConfirmDialog(this,"Score: " + score, "Game Over", JOptionPane.DEFAULT_OPTION);
        if (JOptionPane.showConfirmDialog(this,"Score: " + score, "Game Over", JOptionPane.DEFAULT_OPTION) == 0){
            System.exit(0);
        }
    }

    /**
     * runs the game
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        Tetris game = new Tetris();
        frame.setBackground(Color.white);
        frame.add(game);
        frame.setSize(500, 650);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Tetris");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        game.initializeBoard();
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            /**
             * manipulates the shapes (drop, rotate, move, hold, and hard drop)
             * @param e key pressed
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    game.moveFallingPiece(0,1);
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    game.moveFallingPiece(0,-1);
                if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    game.moveFallingPiece(1,0);
                if (e.getKeyCode() == KeyEvent.VK_UP)
                    game.rotateFallingPiece();
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    if (!game.holdUsed)
                        game.hold();
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    System.exit(0);
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    game.hardDrop();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        /**
         * makes the game go faster as points increase
         */
        new Thread(){
            public void run(){
                while(true){
                    try{
                        if (game.score < 300)
                            Thread.sleep(1000);
                        else if (game.score < 600)
                            Thread.sleep(850);
                        else if (game.score < 800)
                            Thread.sleep(700);
                        else if (game.score < 1000)
                            Thread.sleep(600);
                        else
                            Thread.sleep(500);
                        game.dropDown();
                    } catch (InterruptedException e){

                    }
                }
            }
        }.start();
    }
}