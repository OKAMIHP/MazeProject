import java.util.Random;
import java.util.Scanner;

public class MazeGame {
    public static void main(String[] args) {
        MazeRunner mazeRunner = new MazeRunner();
        mazeRunner.playGame();
    }
}

class MazeRunner {
    private static final int MAZE_SIZE = 10;
    private char[][] maze;

    public MazeRunner() {
        initializeMaze();
    }

    public void initializeMaze() {
        maze = new char[MAZE_SIZE][MAZE_SIZE];

        Random random = new Random();

        // Set blank spaces
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                maze[i][j] = '0';
            }
        }

        // Add walls
        for (int i = 1; i < MAZE_SIZE - 1; i++) {
            for (int j = 1; j < MAZE_SIZE - 1; j++) {
                if (random.nextInt(4) == 0) { // 1 in 4 chance of placing a wall
                    maze[i][j] = '1';
                }
            }
        }

        // Ensure there is a path to the exit
        maze[0][1] = '0';
        maze[1][0] = '0';

        // Set exit
        maze[MAZE_SIZE - 1][MAZE_SIZE - 1] = '3';

        // Place the man randomly
        placeMan();
    }

    public void placeMan() {
        Random random = new Random();
        int manX, manY;
        do {
            manX = random.nextInt(MAZE_SIZE);
            manY = random.nextInt(MAZE_SIZE);
        } while (maze[manX][manY] != '0'); // Ensure man starts in an open space

        maze[manX][manY] = '2';
    }

    public void printMaze() {
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                if (maze[i][j] == '2') {
                    System.out.print('0' + " ");  // Display an open space instead of the man
                } else {
                    System.out.print(maze[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public void communicateWithMan() {
        System.out.println("Man: \"I'm lost in the maze. Where should I go?\"");
    }

    public void guideMan(String direction) {
        // Find the man's position
        int[] manPosition = findMan();
        int manX = manPosition[0];
        int manY = manPosition[1];

        int offsetX = 0;
        int offsetY = 0;

        // Check for walls around the specified position
        boolean wallToLeft = manY > 0 && maze[manX][manY - 1] == '1';
        boolean wallToRight = manY < MAZE_SIZE - 1 && maze[manX][manY + 1] == '1';
        boolean wallAbove = manX > 0 && maze[manX - 1][manY] == '1';
        boolean wallBelow = manX < MAZE_SIZE - 1 && maze[manX + 1][manY] == '1';

        // Determine if the man is near a wall in the specified direction
        switch (direction.toLowerCase()) {
            case "left":
                if (manY > 0) {
                    offsetX = 0;
                    offsetY = -1;
                } else {
                    System.out.println("Man: \"I'm at the left border of the maze. Where should I go?\"");
                    return; // Exit the method without further processing
                }
                break;
            case "right":
                if (manY < MAZE_SIZE - 1) {
                    offsetX = 0;
                    offsetY = 1;
                } else {
                    System.out.println("Man: \"I'm at the right border of the maze. Where should I go?\"");
                    return; // Exit the method without further processing
                }
                break;
            case "up":
                if (manX > 0) {
                    offsetX = -1;
                    offsetY = 0;
                } else {
                    System.out.println("Man: \"I'm at the top border of the maze. Where should I go?\"");
                    return; // Exit the method without further processing
                }
                break;
            case "down":
                if (manX < MAZE_SIZE - 1) {
                    offsetX = 1;
                    offsetY = 0;
                } else {
                    System.out.println("Man: \"I'm at the bottom border of the maze. Where should I go?\"");
                    return; // Exit the method without further processing
                }
                break;
            default:
                System.out.println("Guide: \"Invalid direction. Please choose Left, Right, Up, or Down.\"");
                return; // Exit the method without further processing
        }

        // Print walls information
        if (offsetX == 0 && offsetY == -1) System.out.print(wallToLeft ? "Left " : "");
        if (offsetX == 0 && offsetY == 1) System.out.print(wallToRight ? "Right " : "");
        if (offsetX == -1 && offsetY == 0) System.out.print(wallAbove ? "Above " : "");
        if (offsetX == 1 && offsetY == 0) System.out.print(wallBelow ? "Below" : "");
        System.out.println("\"");

        // Move the man
        moveMan(manX, manY, offsetX, offsetY);
    }
    public void moveMan(int currentX, int currentY, int offsetX, int offsetY) {
        int newX = currentX + offsetX;
        int newY = currentY + offsetY;

        // Check if the target position is within the maze boundaries
        if (newX >= 0 && newX < MAZE_SIZE && newY >= 0 && newY < MAZE_SIZE) {
            // Check if the target position is not a wall
            if (maze[newX][newY] != '1') {
                // Move the man only if the target position is not a wall
                maze[currentX][currentY] = '0';
                maze[newX][newY] = '2';

                // Check for walls around the man after moving
                boolean wallToLeft = newY > 0 && maze[newX][newY - 1] == '1';
                boolean wallToRight = newY < MAZE_SIZE - 1 && maze[newX][newY + 1] == '1';
                boolean wallAbove = newX > 0 && maze[newX - 1][newY] == '1';
                boolean wallBelow = newX < MAZE_SIZE - 1 && maze[newX + 1][newY] == '1';

                // Check if there are walls around the man after moving
                boolean wallsPresent = wallToLeft || wallToRight || wallAbove || wallBelow;

                // Print walls information
                if (wallsPresent) {
                    System.out.print("Man: \"Walls around: ");
                    if (wallToLeft) System.out.print("Left ");
                    if (wallToRight) System.out.print("Right ");
                    if (wallAbove) System.out.print("Above ");
                    if (wallBelow) System.out.print("Below");
                    System.out.println("\"");
                }

                // Check if there are no walls around the man after moving
                if (!wallsPresent) {
                    System.out.println("Man: \"There are no walls around me. Where should I go?\"");
                }
            } else {
                // Inform the man about the wall
                System.out.println("Man: \"I cannot move into a wall. Where should I go?\"");
            }
        } else {
            // Inform the man about the border
            System.out.println("Man: \"I cannot move beyond the border. Where should I go?\"");
        }
    }
    public int[] findMan() {
        int[] position = new int[2];
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                if (maze[i][j] == '2') {
                    position[0] = i;
                    position[1] = j;
                    return position;
                }
            }
        }
        return position;
    }
    public boolean isExitReached() {
        int[] manPosition = findMan();
        return manPosition[0] == MAZE_SIZE - 1 && manPosition[1] == MAZE_SIZE - 1;
    }

    public void playGame() {
        Scanner scanner = new Scanner(System.in);
        communicateWithMan();

        while (true) {
            printMaze();
            System.out.print("Guide: \"Navigate Left, Right, Up, or Down: ");
            String direction = scanner.nextLine();

            guideMan(direction);

            if (isExitReached()) {
                System.out.println("Man: \"Thanks for guiding me. I reached the exit.\"");
                break;
            }
        }
    }
}