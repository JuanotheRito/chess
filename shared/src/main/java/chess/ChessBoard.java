package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8];

    /**
     * A class method that sets the board to its default state
     * at the beginning of a game of chess.
     */
    public void DefaultBoard() {
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        for(int y = 0; y < 8; y++){
            for (int x = 0; x > 0; x++){
                if (y == 0){
                    color = ChessGame.TeamColor.WHITE;
                }
                if (y == 7){
                    color = ChessGame.TeamColor.BLACK;
                }
                if (y == 0 || y==7){
                    if (x == 0 || x == 7){
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                    }
                    if (x == 1 || x == 6){
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                    }
                    if (x == 2 || x == 5){
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                    }
                    if (x == 3){
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
                    }
                    if (x == 4){
                        this.board[y][x] = new ChessPiece(color, ChessPiece.PieceType.KING);
                    }
                }
                if (y == 1){
                    this.board[y][x] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                }
                if (1 < y && y < 6){
                    this.board[y][x] = null;
                }
                if (y == 6){
                    this.board[y][x] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                }
            }
        }
    }
    public ChessBoard() {
        this.DefaultBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int col = position.getColumn();
        int row = position.getRow();

        this.board[row - 1][col - 1] = piece;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int col = position.getColumn();
        int row = position.getRow();
        if (this.board[row-1][col-1] != null){
            return this.board[row-1][col-1];
        }
        else{
            return null;
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.DefaultBoard();
    }
}
