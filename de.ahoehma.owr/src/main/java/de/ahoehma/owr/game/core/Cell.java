package de.ahoehma.owr.game.core;

public class Cell {

  /**
   * @param symbol
   * @param size
   * @param fieldWalls
   * @param cellWalls
   *          the internal walls (for the cells inside the field)
   * @param targets
   * @return
   */
  public static Cell c(final int col, final int row, final String walls, final Symbol target) {
    final Cell cell = new Cell();
    cell.north = walls.contains("N");
    cell.east = walls.contains("O");
    cell.south = walls.contains("S");
    cell.west = walls.contains("W");
    cell.col = col;
    cell.row = row;
    cell.target = target;
    return cell;
  }

  private int col, row;
  private boolean north;
  private boolean east;
  private boolean west;
  private boolean south;
  int flags;
  public Symbol target;

  private Cell() {}

  private String getBorder() {
    final StringBuilder builder = new StringBuilder();
    if (north) {
      builder.append("N");
    }
    if (east) {
      builder.append("O");
    }
    if (south) {
      builder.append("S");
    }
    if (west) {
      builder.append("W");
    }
    return builder.toString();
  }

  public int getCol() {
    return col;
  }

  public int getRow() {
    return row;
  }

  public boolean isCenter() {
    if (target == null) { return false; }
    return target.type == Symbol.CENTER;
  }

  public boolean isEastWall() {
    return east;
  }

  public boolean isNorthWall() {
    return north;
  }

  public boolean isSouthWall() {
    return south;
  }

  public boolean isWestWall() {
    return west;
  }

  @Override
  public String toString() {
    return String.format("Cell[col=%d,row=%d,border=%s]", col, row, getBorder());
  }

}