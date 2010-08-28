package de.ahoehma.owr.game.ui;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Cell;
import de.ahoehma.owr.game.core.Robot;
import de.ahoehma.owr.game.core.Symbol;

/**
 * The {@link BoardPainter} is a {@link Canvas} which can render a {@link Board}.
 * 
 * <p>
 * The following elements will be rendered in the given order:
 * <ul>
 * <li>the cells</li>
 * <li>the source cell</li>
 * <li>the target cell</li>
 * <li>the robots</li>
 * <li>the robots history</li>
 * <li>the walls</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This {@link BoardPainter} support the images from "ricochet robots".
 * </p>
 * 
 * <p>
 * The {@link Board board} could be changed during {@link BoardPainter}'s lifetime ({@link #setBoard(Board)}).
 * </p>
 * 
 * @author andreas
 * @since 1.0.0
 */
public class BoardPainter extends Canvas {

  private static final int IMAGE_SIZE = 20;
  private final static int BORDER_SIZE = 1;
  private final static int CELL_SIZE = 40;
  private final int cellSize = CELL_SIZE;
  private Board board;

  /**
   * Robot images 20x20.
   */
  private final Map<String, Image> robotImages = loadRobotImages();

  /**
   * Symbol images 20x20.
   */
  private final Map<String, Image> symbolImages = loadSymbolImages();

  /**
   * @param parent
   */
  public BoardPainter(final Composite parent) {
    this(parent, null);
  }

  /**
   * @param parent
   * @param theBoard
   */
  public BoardPainter(final Composite parent, final Board theBoard) {
    super(parent, SWT.DOUBLE_BUFFERED);
    addPaintListener(new PaintListener() {
      public void paintControl(final PaintEvent event) {
        paint(0, 0, event.gc);
      }
    });
    setBoard(theBoard);
    setVisible(true);
  }

  public int getCellSize() {
    return cellSize;
  }

  private String getColor(final int theColor) {
    switch (theColor) {
    case Symbol.BLUE:
      return "blue";
    case Symbol.RED:
      return "red";
    case Symbol.YELLOW:
      return "yellow";
    case Symbol.GREEN:
      return "green";
    }
    return null;
  }

  private String getForm(final int theForm) {
    switch (theForm) {
    case Symbol.CIRCLE:
      return "circle";
    case Symbol.SATURN:
      return "saturn";
    case Symbol.STAR:
      return "star";
    case Symbol.TRIANGLE:
      return "triangle";
    case Symbol.CENTER:
      return "center";
    case Symbol.SPECIAL:
      return "special";
    }
    return null;
  }

  private int getSWTColor(final int theColor) {
    switch (theColor) {
    case Symbol.BLUE:
      return SWT.COLOR_BLUE;
    case Symbol.RED:
      return SWT.COLOR_RED;
    case Symbol.YELLOW:
      return SWT.COLOR_YELLOW;
    case Symbol.GREEN:
      return SWT.COLOR_GREEN;
    }
    return -1;
  }

  private Image getSymbolImage(final Symbol theTarget) {
    if (theTarget.type == Symbol.SPECIAL) { return symbolImages.get("special"); }
    return symbolImages.get(getForm(theTarget.type) + "_" + getColor(theTarget.color));
  }

  private Map<String, Image> loadRobotImages() {
    final Map<String, Image> result = new HashMap<String, Image>();
    final Display display = PlatformUI.getWorkbench().getDisplay();
    if (display.isDisposed()) { return result; }
    for (final String color : Arrays.asList("blue", "green", "yellow", "red")) {
      final InputStream resourceAsStream = BoardPainter.class.getResourceAsStream("robot_" + color + ".png");
      if (resourceAsStream != null) {
        result.put(color, new Image(display, resourceAsStream));
      }
    }
    return result;
  }

  private Map<String, Image> loadSymbolImages() {
    final Map<String, Image> result = new HashMap<String, Image>();
    final Display display = PlatformUI.getWorkbench().getDisplay();
    if (display.isDisposed()) { return result; }
    for (final String form : Arrays.asList("circle", "saturn", "star", "triangle")) {
      for (final String color : Arrays.asList("blue", "green", "yellow", "red")) {
        final String name = form + "_" + color;
        final InputStream resourceAsStream = BoardPainter.class.getResourceAsStream(name + ".png");
        if (resourceAsStream != null) {
          result.put(name, new Image(display, resourceAsStream));
        }
      }
    }
    final InputStream resourceAsStream = BoardPainter.class.getResourceAsStream("special.png");
    if (resourceAsStream != null) {
      result.put("special", new Image(display, resourceAsStream));
    }
    return result;
  }

  public void paint(final GC gc) {
    paint(0, 0, gc);
  }

  public void paint(final int xorg, final int yorg, final GC gc) {
    if (board != null) {
      paintBoard(xorg, yorg, gc);
      paintSource(xorg, yorg, gc);
      paintTarget(xorg, yorg, gc);
      paintRobots(xorg, yorg, gc);
      paintRobotHistory(xorg, yorg, gc);
      paintWalls(xorg, yorg, gc);
    }
  }

  private void paintBoard(final int xorg, final int yorg, final GC gc) {
    gc.setLineStyle(SWT.LINE_SOLID);
    gc.setLineWidth(1);
    gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
    final int imageOffset = (cellSize - IMAGE_SIZE) / 2;
    for (final Cell cell : board.getCells()) {
      final int x = xorg + cell.getCol() * cellSize;
      final int y = yorg + cell.getRow() * cellSize;
      gc.drawRectangle(x, y, cellSize, cellSize);
      if (cell.target != null) {
        final Image image = getSymbolImage(cell.target);
        if (image != null) {
          gc.drawImage(image, x + imageOffset, y + imageOffset);
        }
      }
    }
  }

  private void paintRobotHistory(final int xorg, final int yorg, final GC gc) {
    final List<Robot> robots = board.getRobots();
    int i = 0;
    for (final Robot robot : robots) {
      final Set<Cell> set = robot.getCellHistory();
      gc.setLineWidth(1);
      gc.setLineStyle(SWT.LINE_SOLID);
      for (final Cell cell : set) {
        final int x = xorg + cell.getCol() * cellSize;
        final int y = yorg + cell.getRow() * cellSize;
        gc.setBackground(Display.getCurrent().getSystemColor(getSWTColor(robot.getColor())));
        gc.fillRectangle(x + 1 + i / 2 * 3, y + 1 + i % 2 * 3, 3, 3);
      }
      i++;
    }
  }

  private void paintRobots(final int xorg, final int yorg, final GC gc) {
    final List<Robot> robots = board.getRobots();
    final int imageOffset = (cellSize - IMAGE_SIZE) / 2;
    for (final Robot robot : robots) {
      final int x = xorg + robot.getCol() * cellSize;
      final int y = yorg + robot.getRow() * cellSize;
      gc.drawImage(robotImages.get(getColor(robot.getColor())), x + imageOffset, y + imageOffset);
    }
  }

  private void paintSource(final int xorg, final int yorg, final GC gc) {
    final Cell sourceCell = board.getSourceCell();
    if (sourceCell != null) {
      gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
      final int x = xorg + sourceCell.getCol() * cellSize;
      final int y = yorg + sourceCell.getRow() * cellSize;
      gc.fillRectangle(x + 5, y + 5, cellSize - 10, cellSize - 10);
    }
  }

  private void paintTarget(final int xorg, final int yorg, final GC gc) {
    final Cell targetCell = board.getTargetCell();
    if (targetCell != null) {
      gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
      final int x = xorg + targetCell.getCol() * cellSize;
      final int y = yorg + targetCell.getRow() * cellSize;
      gc.fillRectangle(x + 5, y + 5, cellSize - 10, cellSize - 10);
    }
  }

  private void paintWalls(final int xorg, final int yorg, final GC gc) {
    gc.setLineStyle(SWT.LINE_SOLID);
    gc.setLineWidth(1);
    gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
    for (final Cell cell : board.getCells()) {
      final int x = xorg + cell.getCol() * cellSize;
      final int y = yorg + cell.getRow() * cellSize;
      if (cell.isNorthWall()) {
        gc.setLineWidth(2);
        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        gc.fillRectangle(x, y, cellSize, BORDER_SIZE);
      }
      if (cell.isSouthWall()) {
        gc.setLineWidth(2);
        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        gc.fillRectangle(x, y + cellSize - BORDER_SIZE, cellSize, BORDER_SIZE);
      }
      if (cell.isEastWall()) {
        gc.setLineWidth(2);
        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        gc.fillRectangle(x + cellSize - BORDER_SIZE, y, BORDER_SIZE, cellSize);
      }
      if (cell.isWestWall()) {
        gc.setLineWidth(2);
        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        gc.fillRectangle(x, y, BORDER_SIZE, cellSize);
      }
    }
  }

  /**
   * @param theBoard
   */
  public void setBoard(final Board theBoard) {
    if (theBoard != null) {
      board = theBoard;
      final int canvasSize = (1 + board.size()) * cellSize;
      setSize(canvasSize, canvasSize);
    }
  }
}