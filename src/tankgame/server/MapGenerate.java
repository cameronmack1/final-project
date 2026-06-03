package tankgame.server;

import java.util.*;

/**
 *
 * @author Colin
 */
public class MapGenerate {

    private Random rand = new Random();

    public int[][] generate(int row, int col) {
        int[][] maze = new int[row][col];

        // (0 = path, 1 = wall)
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                maze[i][j] = 1;
            }
        }

        // start carving thru it
        carvePassages(1, 1, maze);
        return maze;
    }

    private void carvePassages(int row, int col, int[][] maze) {
        maze[row][col] = 0;

        printMaze(maze);
        System.out.println("---------------------------");

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
                maze[row + direction[0] / 2][col + direction[1] / 2] = 0;

                carvePassages(newRow, newCol, maze);
            }
        }
    }

    private boolean validCheck(int row, int col, int[][] maze) {
        // check to make sure everything falls within the maze's bounds on every side
        return row > 0 && row < maze.length - 1 && col > 0 && col < maze[0].length - 1 && maze[row][col] == 1;
    }

    private void shuffle(int[][] compass) {
        for (int i = compass.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);

            int[] temp = compass[i];
            compass[i] = compass[j];
            compass[j] = temp;
        }
    }

    private void printMaze(int[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if (maze[i][j] == 1) {
                    System.out.print("|");
                } else {
                    System.out.print("_");
                }
            }
            System.out.println();
        }
    }
}
