package tankgame.game;

import java.util.*;

/**
 *
 * @author Colin
 */
public class MapGenerate {

    private Random rand = new Random();

    public boolean[][] generate(long seed) {
        int row = 17;//height
        int col = 33;//width
        rand.setSeed(seed);
        //round row and column up to the nearest odd number if they are even
        boolean[][] maze = new boolean[row][col];

        // (0 = path, 1 = wall)
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                maze[i][j] = true;
            }
        }

        // start carving thru it
        carvePassages(1, 1, maze);
        printMaze(maze);
        return maze;
    }

    private void carvePassages(int row, int col, boolean[][] maze) {
        maze[row][col] = false;

        // the directions the maze can travel
        int[][] compass = {
            {-2, 0}, // (upwards)
            {2, 0}, // (the opposite of upwards)
            {0, -2}, // (left)
            {0, 2}, // (the other way)
        };

        shuffle(compass);

        // trying out each direction
        for (int[] direction : compass) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (validCheck(newRow, newCol, maze)) {
                maze[row + direction[0] / 2][col + direction[1] / 2] = false;

                carvePassages(newRow, newCol, maze);
            }
        }
    }

    private boolean validCheck(int row, int col, boolean[][] maze) {
        // check to make sure everything falls within the maze's bounds on every side
        return row > 0 && row < maze.length - 1 && col > 0 && col < maze[0].length - 1 && maze[row][col];
    }

    private void shuffle(int[][] compass) {
        for (int i = compass.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);

            int[] temp = compass[i];
            compass[i] = compass[j];
            compass[j] = temp;
        }
    }
    
    public static void printMaze(boolean[][] maze){
        for(boolean[] row : maze){
            for(boolean value : row){
                if(value) System.out.print("#");
                else System.out.print(" ");
            }
        System.out.println("");
        }
    }
}
