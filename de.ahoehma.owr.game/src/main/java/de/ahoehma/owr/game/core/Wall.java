package de.ahoehma.owr.game.core;

/**
 * @author andreas
 * @since 1.0.0
 */
public class Wall {

  public static Wall w(final int col, final int row, final String border) {
    final Wall wall = new Wall();
    wall.col = col;
    wall.row = row;
    wall.north = border.contains("N");
    wall.east = border.contains("O");
    wall.south = border.contains("S");
    wall.west = border.contains("W");
    return wall;
  }

  public static Wall w(final String col, final String row, final String border) {
    return w(Integer.valueOf(col), Integer.valueOf(row), border);
  }

  private int col, row;
  private boolean north, east, south, west;

  private Wall() {}

  public String getBorder() {
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

  public boolean isEast() {
    return east;
  }

  public boolean isNorth() {
    return north;
  }

  public boolean isSouth() {
    return south;
  }

  public boolean isWest() {
    return west;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Wall [col=");
    builder.append(col);
    builder.append(", row=");
    builder.append(row);
    builder.append(", walls=");
    builder.append(getBorder());
    builder.append("]");
    return builder.toString();
  }
}