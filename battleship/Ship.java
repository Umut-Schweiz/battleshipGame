package battleship;

public class Ship {
    int start;
    int end;
    int constant;
    boolean isHorizontal;
    Ships type;

    public Ship(int start, int end, int constant, boolean isHorizontal, Ships type) {
        this.start = start;
        this.end = end;
        this.constant = constant;
        this.isHorizontal = isHorizontal;
        this.type = type;
    }

}
