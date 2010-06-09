package de.ahoehma.owr.game.ai;

import de.ahoehma.owr.game.core.Cell;

public class RobotMove {

  private final Cell sourceCell;
  private final Cell targetCell;

  public RobotMove(final Cell source, final Cell target) {
    this.sourceCell = source;
    this.targetCell = target;
  }

  public boolean contains(final Cell theCell) {
    return sourceCell.equals(theCell) || targetCell.equals(theCell);
  }

  public int distance() {
    if (sourceCell.getCol() == targetCell.getCol()) {
      if (sourceCell.getRow() > targetCell.getRow()) {
        return sourceCell.getRow() - targetCell.getRow();
      } else {
        return targetCell.getRow() - sourceCell.getRow();
      }
    }
    if (sourceCell.getRow() == targetCell.getRow()) {
      if (sourceCell.getCol() > targetCell.getCol()) {
        return sourceCell.getCol() - targetCell.getCol();
      } else {
        return targetCell.getCol() - sourceCell.getCol();
      }
    }
    // all other vectors not supported
    return 0;
  }

  public Cell getSourceCell() {
    return sourceCell;
  }

  public Cell getTargetCell() {
    return targetCell;
  }

  @Override
  public String toString() {
    return String.format("%d,%d->%d,%d", sourceCell.getCol(), sourceCell.getRow(), targetCell.getCol(), targetCell
        .getRow());
  }
}