package de.ahoehma.owr.game.core;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Robot {

  private static final Log LOG = LogFactory.getLog(Robot.class);

  final int color;
  final int startCol, startRow;
  final Set<Cell> cellHistory = new HashSet<Cell>();

  int col, row;
  int moveCol, moveRow;
  boolean moving;
  boolean moveWest;
  boolean moveNord;
  boolean moveEast;
  boolean moveSourth;

  public Robot(final int theStartCol, final int theStartRow) {
    this(theStartCol, theStartRow, Symbol.BLUE);
  }

  public Robot(final int theStartCol, final int theStartRow, final int theColor) {
    col = theStartCol;
    row = theStartRow;
    startCol = theStartCol;
    startRow = theStartRow;
    color = theColor;
  }

  public Set<Cell> getCellHistory() {
    return cellHistory;
  }

  public int getCol() {
    return col;
  }

  public int getColor() {
    return color;
  }

  public int getMoveCol() {
    return moveCol;
  }

  public int getMoveRow() {
    return moveRow;
  }

  public int getRow() {
    return row;
  }

  public boolean isMoving() {
    return moving;
  }

  public void move(final Cell theTargetCell) {
    setPosition(theTargetCell);
    cellHistory.add(theTargetCell);
    LOG.debug(String.format("Moved robot to %d, %d", col, row));
  }

  /**
   * @param theTargetCell
   */
  public void setMoveVector(final Cell theTargetCell) {
    moveCol = 0;
    moveRow = 0;
    moveWest = false;
    moveNord = false;
    moveEast = false;
    moveSourth = false;
    moving = true;
    if (theTargetCell.getCol() < col) {
      moveCol = -1;
      moveWest = true;
      return;
    }
    if (theTargetCell.getRow() < row) {
      moveRow = -1;
      moveNord = true;
      return;
    }
    if (theTargetCell.getCol() > col) {
      moveCol = 1;
      moveEast = true;
      return;
    }
    if (theTargetCell.getRow() > row) {
      moveRow = 1;
      moveSourth = true;
      return;
    }
    moveCol = col - theTargetCell.getCol();
  }

  public void setPosition(final Cell theTargetCell) {
    col = theTargetCell.getCol();
    row = theTargetCell.getRow();
  }

  public void stop() {
    // moveCol = 0;
    // moveRow = 0;
    moving = false;
  }
}
