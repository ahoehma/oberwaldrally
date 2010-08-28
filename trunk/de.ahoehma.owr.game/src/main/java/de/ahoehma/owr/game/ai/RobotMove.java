package de.ahoehma.owr.game.ai;

import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Cell;
import de.ahoehma.owr.game.core.Robot;

/**
 * A {@link RobotMove} is one move on a {@link Board playing field}.
 * 
 * <p>
 * Currently only vertical or horizontal moves supported. The game doesn't allow diagonal moves. Maybe this restriction
 * should be better bind to the board, so we can hack some other variants of this game ?! :D
 * </p>
 * 
 * @author andreas
 * @since 1.0.0
 */
public final class RobotMove {

  private final Cell sourceCell;
  private final Cell targetCell;
  private final int distance;
  private final Robot robot;

  public RobotMove(final Robot theRobot, final Cell aSourceCell, final Cell aTargetCell) {
    robot = theRobot;
    sourceCell = aSourceCell;
    targetCell = aTargetCell;
    distance = calculateDistance();
  }

  private int calculateDistance() throws IllegalArgumentException {
    if (sourceCell.getCol() == targetCell.getCol()) {
      if (sourceCell.getRow() > targetCell.getRow()) {
        return sourceCell.getRow() - targetCell.getRow();
      }
      else {
        return targetCell.getRow() - sourceCell.getRow();
      }
    }
    if (sourceCell.getRow() == targetCell.getRow()) {
      if (sourceCell.getCol() > targetCell.getCol()) {
        return sourceCell.getCol() - targetCell.getCol();
      }
      else {
        return targetCell.getCol() - sourceCell.getCol();
      }
    }
    // all other vectors not supported
    throw new UnsupportedOperationException(String.format(
        "The current implemenation doesn't support diagonal moves from '%s' to '%s'", sourceCell, targetCell));
  }

  public boolean contains(final Cell theCell) {
    return sourceCell.equals(theCell) || targetCell.equals(theCell);
  }

  /**
   * @return the numeric distance from the {@link Cell source-cell} to the {@link Cell target-cell}
   */
  public int distance() {
    return distance;
  }

  /**
   * @return the {@link Cell} where the move started
   */
  public Cell getSourceCell() {
    return sourceCell;
  }

  /**
   * @return the {@link Cell} where the move ends
   */
  public Cell getTargetCell() {
    return targetCell;
  }

  @Override
  public String toString() {
    return String.format("Robot %s moved from %d,%d -> %d,%d", robot, sourceCell.getCol(), sourceCell.getRow(),
        targetCell.getCol(), targetCell.getRow());
  }
}