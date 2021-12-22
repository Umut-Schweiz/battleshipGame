package battleship;

public enum Ships {

    AIRCRAFT_CARRIER(5, "Aircraft Carrier"),
    BATTLESHIP      (4, "Battleship"),
    SUBMARINE       (3, "Submarine"),
    CRUISER         (3, "Cruiser"),
    DESTROYER       (2, "Destroyer");

    int cells;
    String name;

    Ships(int cells, String name) {
        this.cells = cells;
        this.name = name;
    }
}
