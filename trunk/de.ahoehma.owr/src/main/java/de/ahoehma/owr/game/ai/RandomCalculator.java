package de.ahoehma.owr.game.ai;

import java.util.List;
import java.util.Random;

import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.BoardUtils;
import de.ahoehma.owr.game.core.Cell;
import de.ahoehma.owr.game.core.Robot;

/**
 * @author andreas
 * @since 1.0.0
 */
public class RandomCalculator implements BoardCalculator {

  private final Board board;
  private final Random random = new Random(System.currentTimeMillis());
  private boolean pause;

  public RandomCalculator(final Board theBoard) {
    board = theBoard;
  }

  public void pause(final boolean flag) {
    pause = flag;
  }

  public void start() {
    final Cell sourceCell = board.getSourceCell();
    final Robot r = board.getRobot(sourceCell);
    if (r == null) {
      return;
    }
    final Cell targetCell = board.getTargetCell();
    pause = false;
    int i = 0;
    while (true) {
      if (!pause) {
        System.out.println("Calculate iteration " + i++ + "...");
        for (final Robot robot : board.getRobots()) {
          if (!robot.isMoving()) {
            pushRobot(board, robot);
          }
          board.moveRobot(board, robot);
          if (r.getCol() == targetCell.getCol() && r.getRow() == targetCell.getRow()) {
            System.out.println("Found solution in iteration " + i + "...");
            return;
          }
        }
      }
    }
  }

  void pushRobot(final Board board, final Robot robot) {
    final int col = robot.getCol();
    final int row = robot.getRow();
    final Cell robotCell = board.getCell(col, row);
    final List<Cell> cells = BoardUtils.getRobotFreeCells(board, board.getMoveableCells(robotCell));
    if (cells.isEmpty()) {
      return;
    }
    // einen weg per zufalls auswaehlen
    // am besten einen "neuen" weg auswaehlen um ping-pong zu vermeiden
    final int nextInt = random.nextInt(cells.size());
    final Cell c = cells.get(nextInt);
    robot.setMoveVector(c);
  }
}