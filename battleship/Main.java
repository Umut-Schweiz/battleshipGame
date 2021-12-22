package battleship;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final char[][] player1 = new char[10][10];
    private static final char[][] player2 = new char[10][10];
    private static final char[][] emptyGameArea = new char[10][10];

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {


        createPlayerGameArea(player1, 1);
        promptEnterKey();
        createPlayerGameArea(player2, 2);
        promptEnterKey();
        startGame();


    }


    public static void createPlayerGameArea(char player[][], int playerNunber) {

        System.out.printf("Player %d, place your ships on the game field\n", playerNunber);

        for (Ships shipType : Ships.values()) { // creation of coordinates of all ship types
            printBoard(player);
            System.out.printf("Enter the coordinates of the %s (%d cells):\n\n", shipType.name, shipType.cells);

            while (true) {
                String[] coordinates = promptCoordinates();// example coordinates {"F3", "F7"}
                Cell c1 = parseCoordinate(coordinates[0]);
                Cell c2 = parseCoordinate(coordinates[1]);

                Optional<Ship> ship = createShip(c1, c2, shipType);

                if (ship.isPresent()) { //isPresent in Optional class is to control null
                    if (addShip(ship.get(), player)) {//get() in Optional getting the value of ship(new ship)
                        break;
                    }
                }
            }
        }

        printBoard(player);

    }


    static {
        init(player1);
        init(player2);
        init(emptyGameArea);
    }

    static void init(char board[][]) {
        for (int i = 0; i < 10; i++) {
            for (int k = 0; k < 10; k++) {
                board[i][k] = '~';
            }
        }
    }

    static String[] promptCoordinates() {
        return scanner.nextLine().split(" ");
    }

    static Optional<Ship> createShip(Cell start, Cell end, Ships shipType) {

        if ((start.x != end.x) == (start.y != end.y)) {// to control same line
            /*
            FOR EXAMPLE A2 E2
            start.x = 0(A)
            end.x = 4(E)
            start.y = 2(1)
            end.y = 2(1)
             */

            System.out.println("\nError! Invalid coordinates! Try again:\n");

            return Optional.empty(); // create an empty Object
        }

        boolean isHorizontal = (start.x == end.x);
        int x, y, constant;

        if (isHorizontal) {

            x = start.y;
            y = end.y;
            constant = start.x;
        } else {
            x = start.x;
            y = end.x;
            constant = start.y;
        }

        if ((Math.abs(x - y) + 1) != shipType.cells) {
            System.out.printf("\nError! Wrong length for the %s! Try again:\n\n", shipType.name);

            return Optional.empty();
        }

        // Makes sure x (start) is smaller than y (end)
        if (x > y) {
            int temp = x;
            x = y;
            y = temp;
        }
        //Optinal.of ==> can never take a null value
        return Optional.of(new Ship(x, y, constant, isHorizontal, shipType));
    }

    static boolean addShip(Ship ship, char player[][]) {
        boolean isValidPosition = true;

        // Check board for ship location +1 square around is clear
        for (int i = ship.constant - 1; i <= ship.constant + 1; i++) {
            if (i < 0 || i > 9) {
                continue;
            }

            for (int k = ship.start - 1; k <= ship.end + 1; k++) {
                if (k < 0 || k > 9) {
                    continue;
                }

                if (ship.isHorizontal) {
                    if (player[i][k] != '~') {
                        isValidPosition = false;
                    }
                } else {
                    if (player[k][i] != '~') {
                        isValidPosition = false;
                    }
                }
            }
        }

        if (!isValidPosition) {
            System.out.println("Error! You placed it too close to another ship. Try again:");
            return false;
        }

        for (int i = ship.start; i <= ship.end; i++) {
            if (ship.isHorizontal) {
                player[ship.constant][i] = 'O';
            } else {
                player[i][ship.constant] = 'O';
            }
        }

        return true;
    }

//    static boolean isSurroundingClear (ship)

    static Cell parseCoordinate(String coordinate) {

        int x = (coordinate.charAt(0)) % 65; // we found x coordinate for field array
        int y = Integer.parseInt(coordinate.substring(1)) - 1; // we found y coordinate for field array

        return new Cell(x, y);
    }

    static void printBoard(char player[][]) {
        System.out.println("\n  1 2 3 4 5 6 7 8 9 10");

        for (int i = 0; i < 10; i++) {
            System.out.print((char) (i + 65));

            for (int k = 0; k < 10; k++) {
                System.out.print(" " + player[i][k]);
            }
            System.out.println();
        }
        System.out.println();
    }


    public static void playerTurn(int playerNumber) {
        if (playerNumber == 1) {
            printBoard(emptyGameArea);
            System.out.println("---------------------");
            printBoard(player1);
            System.out.printf("Player %d, it's your turn:\n", playerNumber);

        } else {
            printBoard(emptyGameArea);
            System.out.println("---------------------");
            printBoard(player2);
            System.out.printf("Player %d, it's your turn:\n", playerNumber);
        }
    }

    static void startGame() {

        playerTurn(1);

        String shootCoordinate = scanner.next();
        Cell shootCell = parseCoordinate(shootCoordinate);


        try {

            if (player2[shootCell.x][shootCell.y] == 'O') {
                player2[shootCell.x][shootCell.y] = 'X';
                System.out.println("You hit a ship!");
                promptEnterKey();
                player2Turn();

            } else {
                player2[shootCell.x][shootCell.y] = 'M';
                System.out.println("You missed!");
                promptEnterKey();
                player2Turn();
            }

        } catch (Exception e) {
            System.out.println("Error! You entered the wrong coordinates! Try again:\n");
            player1Turn();

        }
    }


    static void player1Turn() {

        playerTurn(1);

        String shootCoordinate = scanner.next();
        Cell shootCell = parseCoordinate(shootCoordinate);


        try {
            if (player2[shootCell.x][shootCell.y] == 'O') {

                player2[shootCell.x][shootCell.y] = 'X';

                if (allSunk(player2)) {
                    //playerTurn(1);
                    //currentBoard(player1);
                    System.out.println("\nYou sank the last ship. You won. Congratulations!");
                } else if (isSunkShip(shootCell.x, shootCell.y, player2)) {

                    System.out.println("You sank a ship!");
                    promptEnterKey();
                    player2Turn();

                } else {
                    System.out.println("You hit a ship!");
                    promptEnterKey();
                    player2Turn();
                }

            } else if (player2[shootCell.x][shootCell.y] == 'X' || player2[shootCell.x][shootCell.y] == 'M') {

                System.out.println("Error! You have shot this forehead before! Please try again:\n");
                player1Turn();

            } else {
                player2[shootCell.x][shootCell.y] = 'M';
                System.out.println("You missed!");
                promptEnterKey();
                player2Turn();
            }
//


        } catch (Exception e) {
            System.out.println("Error! You entered the wrong coordinates! Try again:\n");
            player1Turn();

        }

    }


    static void player2Turn() {

        playerTurn(2);

        String shootCoordinate = scanner.next();
        Cell shootCell = parseCoordinate(shootCoordinate);


        try {

            if (player1[shootCell.x][shootCell.y] == 'O') {

                player1[shootCell.x][shootCell.y] = 'X';

                if (allSunk(player1)) {
                    System.out.println("\nYou sank the last ship. You won. Congratulations!");
                } else if (isSunkShip(shootCell.x, shootCell.y, player1)) {

                    System.out.println("You sank a ship!");
                    promptEnterKey();
                    player1Turn();

                } else {
                    System.out.println("You hit a ship!");
                    promptEnterKey();
                    player1Turn();
                }

            } else if (player1[shootCell.x][shootCell.y] == 'X' || player1[shootCell.x][shootCell.y] == 'M') {

                System.out.println("Error! You have shot this forehead before! Please try again:\n");
                player2Turn();
            } else {

                player1[shootCell.x][shootCell.y] = 'M';
                System.out.println("You missed!");
                promptEnterKey();
                player1Turn();
            }


        } catch (Exception e) {
            System.out.println("Error! You entered the wrong coordinates! Try again:\n");
            player2Turn();
        }

    }


    static boolean allSunk(char board[][]) {

        int counter = 0;

        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {
                if (board[i][j] == 'O') {
                    counter++;
                }
            }
        }

        if (counter == 0) {
            return true;
        } else {
            return false;
        }
    }


    public static void promptEnterKey() {
        System.out.println("Press Enter and pass the move to another player");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean isSunkShip(int x, int y, char playNet[][]) {
        int i = 1;
        int j = 1;
        while (y - i >= 0 && playNet[x][y - i] != 'M' && playNet[x][y - i] != '~') {
            if (playNet[x][y - i] == 'O') return false;
            i++;
        }
        while (y + i < 10 && playNet[x][y + j] != 'M' && playNet[x][y + j] != '~') {
            if (playNet[x][y + j] == 'O') return false;
            j++;
        }
        while (x - i >= 0 && playNet[x - i][y] != 'M' && playNet[x - i][y] != '~') {
            if (playNet[x - i][y] == 'O') return false;
            i++;
        }
        while (x + j < 10 && playNet[x + j][y] != 'M' && playNet[x + j][y] != '~') {
            if (playNet[x + j][y] == 'O') return false;
            j++;
        }
        return true;
    }
}


