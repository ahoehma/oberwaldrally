package de.ahoehma.owr.game.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;

/**
 * A {@link Board} is the playground for our game. The board defines the rules of the game, i.e. where are {@link Wall
 * walls}, where are {@link Robot robots}.
 * 
 * @author andreas
 * @since 1.0.0
 */
public class Board {

  public static final String ROBOT_STEP = "robot_step";
  public static final String ROBOT_SINGLE_STEP = "robot_single_step";

  static Wall[] walls1 = new Wall[] { Wall.w(1, 0, "O"), Wall.w(3, 1, "WN"), Wall.w(6, 3, "OS"), Wall.w(1, 4, "WS"),
      Wall.w(4, 6, "NO"), Wall.w(0, 7, "N"), Wall.w(7, 7, "NW") };
  static Symbol[] targets1 = new Symbol[] { Symbol.s(3, 1, Symbol.GREEN | Symbol.TRIANGLE),
      Symbol.s(6, 3, Symbol.YELLOW | Symbol.STAR), Symbol.s(1, 4, Symbol.RED | Symbol.CIRCLE),
      Symbol.s(4, 6, Symbol.BLUE | Symbol.SATURN), Symbol.s(7, 7, Symbol.CENTER) };

  static Wall[] walls2 = new Wall[] { Wall.w(5, 0, "O"), Wall.w(3, 2, "WN"), Wall.w(0, 3, "S"), Wall.w(5, 3, "WS"),
      Wall.w(2, 4, "NO"), Wall.w(4, 5, "OS"), Wall.w(7, 7, "NW") };
  static Symbol[] targets2 = new Symbol[] { Symbol.s(3, 2, Symbol.YELLOW | Symbol.CIRCLE),
      Symbol.s(5, 3, Symbol.BLUE | Symbol.TRIANGLE), Symbol.s(2, 4, Symbol.RED | Symbol.SATURN),
      Symbol.s(4, 5, Symbol.GREEN | Symbol.STAR), Symbol.s(7, 7, Symbol.CENTER) };

  static Wall[] walls3 = new Wall[] { Wall.w(1, 0, "O"), Wall.w(4, 1, "NO"), Wall.w(1, 3, "WS"), Wall.w(0, 5, "S"),
      Wall.w(5, 5, "NW"), Wall.w(3, 6, "SO"), Wall.w(7, 7, "NW") };
  static Symbol[] targets3 = new Symbol[] { Symbol.s(4, 1, Symbol.GREEN | Symbol.CIRCLE),
      Symbol.s(1, 3, Symbol.RED | Symbol.TRIANGLE), Symbol.s(5, 5, Symbol.YELLOW | Symbol.SATURN),
      Symbol.s(3, 6, Symbol.BLUE | Symbol.STAR), Symbol.s(7, 7, Symbol.CENTER) };

  static Wall[] walls4 = new Wall[] { Wall.w(2, 0, "O"), Wall.w(5, 1, "WS"), Wall.w(7, 2, "SO"), Wall.w(0, 3, "S"),
      Wall.w(3, 4, "SO"), Wall.w(6, 5, "WN"), Wall.w(1, 6, "NO"), Wall.w(7, 7, "NW") };
  static Symbol[] targets4 = new Symbol[] { Symbol.s(5, 1, Symbol.BLUE | Symbol.CIRCLE),
      Symbol.s(7, 2, Symbol.SPECIAL), Symbol.s(3, 4, Symbol.RED | Symbol.STAR),
      Symbol.s(6, 5, Symbol.GREEN | Symbol.SATURN), Symbol.s(1, 6, Symbol.YELLOW | Symbol.TRIANGLE),
      Symbol.s(7, 7, Symbol.CENTER) };

  public static Field DEFAULT_FIELD_WN = Field.f("WN", walls1, targets1);
  public static Field DEFAULT_FIELD_NO = Field.rotate(Field.f("WN", walls2, targets2));
  public static Field DEFAULT_FIELD_WS = Field.rotate(Field.f("WN", walls3, targets3), 3);
  public static Field DEFAULT_FIELD_SO = Field.rotate(Field.f("WN", walls4, targets4), 2);

  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  private final Field field;

  private final List<Robot> robots = new ArrayList<Robot>();

  private Cell targetCell;
  private Cell sourceCell;
  public static Board DEFAULT_BOARD = Board.b(DEFAULT_FIELD_WN, DEFAULT_FIELD_NO, DEFAULT_FIELD_WS, DEFAULT_FIELD_SO);

  /**
   * Builder to create new {@link Board} from the given 4 {@link Field fields}.
   * 
   * <pre>
   * |---------------|
   * |       |       |
   * |  WN   |  NO   |
   * |       |       |
   * |---------------|
   * |       |       |
   * |  WS   |  SO   |
   * |       |       |
   * |---------------|
   * </pre>
   * 
   * @param fieldWN
   * @param fieldNO
   * @param fieldWS
   * @param fieldSO
   * @throws IllegalArgumentException
   *           if the given fields doesn't have the same size and the correct orientations
   */
  public static Board b(final Field fieldWN, final Field fieldNO, final Field fieldWS, final Field fieldSO)
      throws IllegalArgumentException {
    final Set<Integer> fieldSizeCheck = new HashSet<Integer>();
    fieldSizeCheck.add(fieldWN.size());
    fieldSizeCheck.add(fieldNO.size());
    fieldSizeCheck.add(fieldWS.size());
    fieldSizeCheck.add(fieldSO.size());
    if (fieldSizeCheck.size() != 1) { throw new IllegalArgumentException("Given fields doesn't have the same size "
        + fieldSizeCheck); }
    final Integer fieldSize = (Integer) fieldSizeCheck.toArray()[0];
    final int centerPosition = fieldSize.intValue() - 1; // 0..size-1
    if (!fieldWN.getCell(centerPosition, centerPosition).isCenter()) { throw new IllegalArgumentException(String
        .format("Given WN field is not in correct orientation, cell %d,%d has to be the center cell", centerPosition,
            centerPosition)); }
    if (!fieldNO.getCell(0, centerPosition).isCenter()) { throw new IllegalArgumentException(String.format(
        "Given NO field is not in correct orientation, cell 0,%d has to be the center cell", centerPosition)); }
    if (!fieldWS.getCell(centerPosition, 0).isCenter()) { throw new IllegalArgumentException(String.format(
        "Given WS field is not in correct orientation, cell %d,0 has to be the center cell", centerPosition)); }
    if (!fieldSO.getCell(0, 0).isCenter()) { throw new IllegalArgumentException(String
        .format("Given SO field is not in correct orientation, cell 0,0 has to be the center cell")); }

    final Wall[] walls = BoardUtils.getMergedWalls(fieldWN, fieldNO, fieldWS, fieldSO, fieldSize);
    final Symbol[] targets = BoardUtils.getMergedTargets(fieldWN, fieldNO, fieldWS, fieldSO, fieldSize);
    return new Board(Field.f(fieldSize * 2, "WNOS", walls, targets));
  }

  /**
   * Builder to create new {@link Board} from the given {@link Field fields}.
   * 
   * @param fields
   *          The number of fields must be 1 or 4.
   * @return
   */
  public static Board b(final List<Field> fields) {
    if (fields.size() == 1) { return new Board(fields.get(0)); }
    return Board.b(fields.get(0), fields.get(1), fields.get(2), fields.get(3));
  }

  public Board(final Field aField) {
    field = aField;
  }

  public void addPropertyChangeListener(final PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void addRobot(final int col, final int row, final int color) {
    addRobot(new Robot(col, row, color));
  }

  public void addRobot(final Robot robot) {
    robots.add(robot);
  }

  public Cell getCell(final int col, final int row) {
    return field.getCell(col, row);
  }

  public List<Cell> getCells() {
    return Arrays.asList(field.cells);
  }

  public Field getField() {
    return field;
  }

  /**
   * Return a list of neighbor cells starting from the given cell where it's possible to move to. Imagine the given cell
   * is the starting point, then you can move to any of the returned cells without pushing a wall.
   * 
   * <p>
   * Examples: The given cell <b>X</b> is the start point
   * 
   * <pre>
   *      I.                II.               III.              IV.                    
   * |-----------|     |-----------|     |-----------|     |-----------|        
   * |   |   |   |     |   |   |   |     |   |   |   |     |   |   |   |      
   * |   | 1 |   |     |   | 1 |   |     |   | 1 |   |     |   | 1 |   |      
   * |   |   |   |     |   |   |   |     |   |   |   |     |   |   |   |      
   * |-----------|     |---*****---|     |---*****---|     |-----------|      
   * |   |   |   |     |   *   *   |     |   |   |   |     |   *   *   |      
   * | 2 | X | 3 |     | 2 * X * 3 |     | 2 | X | 3 |     | 2 * X * 3 |      
   * |   |   |   |     |   *   *   |     |   |   |   |     |   *   *   |      
   * |-----------|     |---*****---|     |---*****---|     |-----------|      
   * |   |   |   |     |   |   |   |     |   |   |   |     |   |   |   |      
   * |   | 4 |   |     |   | 4 |   |     |   | 4 |   |     |   | 4 |   |      
   * |   |   |   |     |   |   |   |     |   |   |   |     |   |   |   |      
   * |-----------|     |-----------|     |-----------|     |-----------|
   * </pre>
   * 
   * <ul>
   * <li>I. cell X have no walls - all 4 neighbor cells returned</li>
   * <li>II. cell X have wall on each side - no neighbor cells returned</li>
   * <li>III. cell X have walls on north and south - neighbor cells 2 and 3 returned</li>
   * <li>IV. cell X have walls on west and east - neighbor cells 1 and 4 returned</li>
   * </ul>
   * 
   * You can imagine by yourself all other combinations :)
   * 
   * </p>
   * 
   * <p>
   * This method doesn't check if the target cell is occupied by a other Robot
   * </p>
   * 
   * @param cell
   * @param field
   * 
   * @return a list of possible cells, the list is maybe empty
   */
  public List<Cell> getMoveableCells(final Cell cell) {
    final int col = cell.getCol();
    final int row = cell.getRow();
    final List<Cell> possibleCells = new ArrayList<Cell>();
    if (!cell.isNorthWall()) {
      // no wall in the north direction
      if (row > 0) {
        // and there is at least one cell available in this direction
        possibleCells.add(getCell(col, row - 1));
      }
    }
    if (!cell.isSouthWall()) {
      // no wall in the south direction
      if (row < size() - 1) {
        // and there is at least one cell available in this direction
        possibleCells.add(getCell(col, row + 1));
      }
    }
    if (!cell.isWestWall()) {
      // no wall in the west direction
      if (col > 0) {
        // and there is at least one cell available in this direction
        possibleCells.add(getCell(col - 1, row));
      }
    }
    if (!cell.isEastWall()) {
      // no wall in the east direction
      if (col < size() - 1) {
        // and there is at least one cell available in this direction
        possibleCells.add(getCell(col + 1, row));
      }
    }
    return possibleCells;
  }

  /**
   * @param aCell
   * @return a {@link Robot robot} or <code>null</code>
   */
  public Robot getRobot(final Cell aCell) {
    if (aCell != null) {
      for (final Robot robot : getRobots()) {
        if (robot.getCol() == aCell.getCol() && robot.getRow() == aCell.getRow()) { return robot; }
      }
    }
    return null;
  }

  /**
   * TODO maybe we should change this to a "robot-id" instead a "robot-color", so we can simulate other scenarios.
   * 
   * @param theColor
   * @return the {@link Robot robot} for the given color or <code>null</code>
   */
  public Robot getRobot(final int theColor) {
    Robot r = null;
    for (final Robot robot : getRobots()) {
      if (robot.getColor() == theColor) {
        r = robot;
        break;
      }
    }
    return r;
  }

  public List<Robot> getRobots() {
    return robots;
  }

  public Cell getSourceCell() {
    return sourceCell;
  }

  public Cell getTargetCell() {
    return targetCell;
  }

  public List<Cell> getTargets() {
    final List<Cell> result = new ArrayList<Cell>();
    for (final Symbol s : field.targets) {
      result.add(getCell(s.col, s.row));
    }
    return result;
  }

  public List<Cell> getTargets(final int theColor) {
    final List<Cell> result = new ArrayList<Cell>();
    if (field.targets != null) {
      for (final Symbol s : field.targets) {
        if (s.color == theColor) {
          result.add(getCell(s.col, s.row));
        }
      }
    }
    return result;
  }

  /**
   * @param cell
   * @return <code>true</code> if the given {@link Cell} contains a robot else <code>false</code>
   */
  public boolean isRobot(final Cell cell) {
    return isRobot(cell.getCol(), cell.getRow());
  }

  /**
   * @param col
   * @param row
   * @return <code>true</code> if the {@link Cell} with coordinates col, row contains a robot else <code>false</code>
   */
  private boolean isRobot(final int col, final int row) {
    final Collection<Robot> r = CollectionUtils.synchronizedCollection(robots);
    for (final Robot robot : r) {
      if (robot.getCol() == col && robot.getRow() == row) { return true; }
    }
    return false;
  }

  /**
   * Move the given {@link Robot theRobot} on the given {@link Board theBoard} until he is blocked by a {@link Wall
   * wall} or a other {@link Robot robot}.
   * 
   * @param theBoard
   * @param theRobot
   * @return a list of {@link Cell cells} which the robot has passed
   */
  public List<Cell> moveRobot(final Board theBoard, final Robot theRobot) {
    final List<Cell> movedCells = new ArrayList<Cell>();
    while (theRobot.isMoving()) {
      movedCells.add(moveRobotOneCell(theBoard, theRobot));
    }
    propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, ROBOT_STEP, null, theRobot));
    return movedCells;
  }

  /**
   * @param board
   * @param theRobot
   * @return
   */
  public Cell moveRobotOneCell(final Board board, final Robot theRobot) {
    final Cell robotCell = board.getCell(theRobot.getCol(), theRobot.getRow());
    if (!theRobot.isMoving()) { return robotCell; }
    final List<Cell> cells = getMoveableCells(robotCell);
    if (!cells.isEmpty()) {
      final int col = theRobot.getCol() + theRobot.getMoveCol();
      final int row = theRobot.getRow() + theRobot.getMoveRow();
      for (final Cell cell : cells) {
        if (isRobot(cell)) {
          continue;
        }
        if (cell.getCol() == col && cell.getRow() == row) {
          theRobot.move(cell);
          propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, ROBOT_SINGLE_STEP, null, theRobot));
          return cell;
        }
      }
    }
    theRobot.stop();
    return robotCell;
  }

  public void removePropertyChangeListener(final PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  public void setSourceCell(final int theCol, final int theRow) {
    sourceCell = getCell(theCol, theRow);
  }

  public void setTargetCell(final int theCol, final int theRow) {
    targetCell = getCell(theCol, theRow);
  }

  public int size() {
    return field.size();
  }
}