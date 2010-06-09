package de.ahoehma.owr.game.core;

public class Field {

  /**
   * @param size
   * @param fieldWalls
   * @param cellWalls
   *          the internal walls (for the cells inside the field)
   * @param targets
   * @return
   */
  public static Field f(final int size, final String fieldWalls, final Wall[] cellWalls, final Symbol[] targets) {
    final Field field = new Field(size);
    field.north = fieldWalls.contains("N");
    field.east = fieldWalls.contains("O");
    field.south = fieldWalls.contains("S");
    field.west = fieldWalls.contains("W");
    field.walls = cellWalls;
    field.targets = targets;
    field.name = fieldWalls;
    field.initCells();
    return field;
  }

  /**
   * @param fieldWalls
   * @param cellWalls
   *          the internal walls (for the cells inside the field)
   * @param targets
   * @return
   */
  public static Field f(final String fieldWalls, final Wall[] cellWalls, final Symbol[] targets) {
    return f(8, fieldWalls, cellWalls, targets);
  }

  /**
   * Rotate the given field clockwise.
   * 
   * @param aField
   * @return
   */
  public static Field rotate(final Field aField) {
    final Wall[] orgWalls = aField.walls;
    final Symbol[] orgTargets = aField.targets;
    final Wall[] newWalls = new Wall[orgWalls.length];
    final Symbol[] newTargets = new Symbol[orgTargets.length];
    for (int i = 0; i < orgWalls.length; i++) {
      final Wall wall = orgWalls[i];
      final int rotCol = aField.size - 1 - wall.getRow();
      final int rotRow = wall.getCol();
      String newWall = "";
      if (wall.isEast()) {
        newWall += "S";
      }
      if (wall.isSouth()) {
        newWall += "W";
      }
      if (wall.isWest()) {
        newWall += "N";
      }
      if (wall.isNorth()) {
        newWall += "O";
      }
      newWalls[i] = Wall.w(rotCol, rotRow, newWall);
    }
    for (int i = 0; i < orgTargets.length; i++) {
      final Symbol target = orgTargets[i];
      final int rotCol = aField.size - 1 - target.row;
      final int rotRow = target.col;
      newTargets[i] = Symbol.s(rotCol, rotRow, target.type | target.color);
    }
    String newBorders = "";
    if (aField.east) {
      newBorders += "S";
    }
    if (aField.south) {
      newBorders += "W";
    }
    if (aField.west) {
      newBorders += "N";
    }
    if (aField.north) {
      newBorders += "O";
    }
    return Field.f(aField.size, newBorders, newWalls, newTargets);
  }

  /**
   * Rotate the given field clockwise <code>i</code> times.
   * 
   * @param aField
   * @param i
   * @return
   */
  public static Field rotate(final Field aField, final int i) {
    Field result = aField;
    for (int j = 0; j < i; j++) {
      result = rotate(result);
    }
    return result;
  }

  public boolean north;
  public boolean east;
  public boolean west;
  public boolean south;
  private final int size;
  public String name;
  public Cell[] cells;
  public Wall[] walls;
  public Symbol[] targets;

  public Field(final int theSize) {
    size = theSize;
  }

  public String getBorder() {
    String result = "";
    if (south) {
      result += "S";
    }
    if (west) {
      result += "W";
    }
    if (north) {
      result += "N";
    }
    if (east) {
      result += "O";
    }
    return result;
  }

  /**
   * @param col
   *          0..size-1
   * @param row
   *          0..size-1
   * @return
   */
  public Cell getCell(final int col, final int row) {
    final int index = row * size + col;
    if (index > cells.length - 1) { throw new IllegalArgumentException(String.format(
        "Cell col=%d row=%d (index=%d) doesn't exists, max cells %d", col, row, index, cells.length)); }
    return cells[index];
  }

  private Symbol getTarget(final int col, final int row, final Symbol[] targets) {
    if (targets != null) {
      for (final Symbol target : targets) {
        if (target == null) {
          continue;
        }
        if ((target.col == col) && (target.row == row)) {
          // genau die zelle
          return target;
        }
      }
    }
    return null;
  }

  private void initCells() {
    cells = new Cell[size * size];
    for (int c = 0; c < cells.length; c++) {
      final int col = c % size;
      final int row = c / size;
      String cellWalls = "";
      if (isWallEast(col, row, walls, east)) {
        cellWalls += "O";
      }
      if (isSouthWall(col, row, walls, south)) {
        cellWalls += "S";
      }
      if (isWestWall(col, row, walls, west)) {
        cellWalls += "W";
      }
      if (isNorthWall(col, row, walls, north)) {
        cellWalls += "N";
      }
      cells[c] = Cell.c(col, row, cellWalls, getTarget(col, row, targets));
    }
  }

  private boolean isNorthWall(final int col, final int row, final Wall[] cellWalls, final boolean hasFieldNorthWall) {
    if ((row == 0) && hasFieldNorthWall) {
      // spielfeld begrenzung :: oben
      return true;
    }
    for (final Wall wall : cellWalls) {
      if ((wall.getCol() == col) && (wall.getRow() == row)) {
        // genau die zelle
        return wall.isNorth();
      }
      if ((wall.getCol() == col) && (wall.getRow() == row - 1)) {
        // wenn die zelle oberhalb eine sued wall hat, dann hat die
        // aktuelle zelle eine nordwand
        return wall.isSouth();
      }
    }
    return false;
  }

  private boolean isSouthWall(final int col, final int row, final Wall[] cellWalls, final boolean hasFieldSouthWall) {
    if ((row == size - 1) && hasFieldSouthWall) {
      // spielfeld begrenzung :: unten
      return true;
    }
    for (final Wall wall : cellWalls) {
      if ((wall.getCol() == col) && (wall.getRow() == row)) {
        // genau die zelle
        return wall.isSouth();
      }
      if ((wall.getCol() == col) && (wall.getRow() == row + 1)) {
        // die zelle unterhalb
        return wall.isNorth();
      }
    }
    return false;
  }

  private boolean isWallEast(final int col, final int row, final Wall[] cellWalls, final boolean hasFieldEastWall) {
    if ((col == size - 1) && hasFieldEastWall) {
      // spielfeld begrenzung :: rechts
      return true;
    }
    for (final Wall wall : cellWalls) {
      if ((wall.getCol() == col) && (wall.getRow() == row)) {
        // genau die zelle
        return wall.isEast();
      }
      if ((wall.getCol() == col + 1) && (wall.getRow() == row)) {
        // wenn die zelle rechts davon eine west wall hat, dann hat die
        // aktuelle
        // zelle eine ost
        return wall.isWest();
      }
    }
    return false;
  }

  private boolean isWestWall(final int col, final int row, final Wall[] cellWalls, final boolean hasFieldWestWall) {
    if ((col == 0) && hasFieldWestWall) {
      // spielfeld begrenzung :: links
      return true;
    }
    for (final Wall wall : cellWalls) {
      if ((wall.getCol() == col) && (wall.getRow() == row)) {
        // genau die zelle
        return wall.isWest();
      }
      if ((wall.getCol() == col - 1) && (wall.getRow() == row)) {
        // wenn die zelle links davon eine ost wall hat, dann hat die
        // aktuelle
        // zelle eine west
        return wall.isEast();
      }
    }
    return false;
  }

  public int size() {
    return size;
  }

  @Override
  public String toString() {
    final StringBuffer result = new StringBuffer();
    result.append(String.format("Field [north=%s,east=%s,south=%s,west=%s]\n", north, east, south, west));
    result.append("Walls:\n");
    for (int i = 0; i < walls.length; i++) {
      result.append(String.format("\tWall[%d] = %s\n", i, walls[i]));
    }
    result.append("Cells:\n");
    for (int col = 0; col < size; col++) {
      for (int row = 0; row < size; row++) {
        result.append(String.format("\tCell[%d.%d] = %s\n", col, row, getCell(col, row)));
      }
    }
    return result.toString();
  }
}