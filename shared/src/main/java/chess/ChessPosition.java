package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int col;
    private int row;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return col == that.col && row == that.row;
    }

    @Override
    public String toString() {
        String string;
        switch (col){
            case 1 -> string = "a";
            case 2 -> string = "b";
            case 3 -> string = "c";
            case 4 -> string = "d";
            case 5 -> string = "e";
            case 6 -> string = "f";
            case 7 -> string = "g";
            case 8 -> string = "h";
            default -> string = "";
        }
        string += row;
        return string;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }
}
