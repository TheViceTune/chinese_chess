package Game.Pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Board.Move;
import Game.Board.Move.AttackMove;
import Game.Board.Move.MajorMove;
import Game.Board.Tile;
import Game.Side;

public class Advisor extends Piece {
    private final int[] CANDIDATE;

    public Advisor(final int position, final Side pieceSide) {
        super(PieceType.ADVISOR, position, pieceSide);
        this.CANDIDATE = determineCandidateOffsets(this.position);
    }

    private int[] determineCandidateOffsets(int position) {
        if (position == 13 || position == 76) {
            return new int[] { -10, -8, 8, 10 };
        } else if (position == 3 || position == 66) {
            return new int[] { 10 };
        } else if (position == 5 || position == 68) {
            return new int[] { 8 };
        } else if (position == 21 || position == 84) {
            return new int[] { -8 };
        } else {
            return new int[] { -10 };
        }
    }

    @Override
    public Collection<Move> legalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int nextCoord : CANDIDATE) {
            int candidateDestination = this.position + nextCoord;
            if (BoardUtils.isValid(candidateDestination)) {
                if (isFirstColumnKingdomExclusion(this.position, nextCoord) ||
                        isSecondColumnKingdomExclusion(this.position, nextCoord) ||
                        isTopRowKingdomExclusion(this.position, nextCoord) ||
                        isTopRowKingdomExclusionBlack(this.position, candidateDestination)) {
                    continue;
                }
                final Tile candidateTile = board.getTile(candidateDestination);

                if (!candidateTile.isOccupied()) {
                    legalMoves.add(new MajorMove(board, this, candidateDestination));
                } else {
                    final Piece atDestination = candidateTile.getPiece();
                    final Side destinationSide = atDestination.getSide();

                    if (this.pieceSide != destinationSide) {
                        legalMoves.add(new AttackMove(board, this, candidateDestination, atDestination));
                    }
                }
            }
        }
        return legalMoves;
    }

    @Override
    public Advisor movePiece(Move move) {
        return new Advisor(move.getDestinationCoordinates(), move.getMovePiece().getSide());
    }

    @Override
    public String toString() {
        return PieceType.ADVISOR.toString();
    }

    public static boolean isFirstColumnKingdomExclusion(final int position, final int candidateOffset) {
        return BoardUtils.FOURTH_COLUMN[position] && ((candidateOffset == -10) || (candidateOffset == -1)
                || (candidateOffset == 8));
    }

    public static boolean isSecondColumnKingdomExclusion(final int position, final int candidateOffset) {
        return BoardUtils.SIXTH_COLUMN[position] && ((candidateOffset == 10) || (candidateOffset == 1)
                || (candidateOffset == -8));
    }

    public static boolean isTopRowKingdomExclusion(final int position, final int candidateOffset) {
        return BoardUtils.SEVENTH_RANK[position] && ((candidateOffset == 8) || (candidateOffset == 9)
                || (candidateOffset == 10));
    }

    public static boolean isTopRowKingdomExclusionBlack(final int position, final int candidateOffset) {
        return BoardUtils.THIRD_RANK[position] && ((candidateOffset == 8) || (candidateOffset == 9)
                || (candidateOffset == 10));
    }
}
