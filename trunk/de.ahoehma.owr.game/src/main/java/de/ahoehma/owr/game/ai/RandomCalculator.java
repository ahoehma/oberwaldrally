package de.ahoehma.owr.game.ai;

import java.util.ArrayList;
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
public class RandomCalculator extends AbstractCalculator {

  private final Random random = new Random(System.currentTimeMillis());

  public RandomCalculator(final Board aBoard) {
    super(aBoard);
  }

  @Override
  public List<RobotMove> calculate() {
    return moveRobots();
  }

  private List<RobotMove> moveRobots() {
    final List<RobotMove> moves = new ArrayList<RobotMove>();
    while (true) {
      for (final Robot robot : board.getRobots()) {
        if (!robot.isMoving()) {
          pushRobot(board, robot);
        }
        final Cell cellBeforeMove = board.getCell(robot.getCol(), robot.getRow());
        board.moveRobot(board, robot);
        final Cell cellAfterMove = board.getCell(robot.getCol(), robot.getRow());
        moves.add(new RobotMove(robot, cellBeforeMove, cellAfterMove));
        if (foundSolution()) { return moves; }
      }
    }
  }

  void pushRobot(final Board board, final Robot robot) {
    final int col = robot.getCol();
    final int row = robot.getRow();
    final Cell robotCell = board.getCell(col, row);
    final List<Cell> cells = BoardUtils.getRobotFreeCells(board, board.getMoveableCells(robotCell));
    if (cells.isEmpty()) { return; }
    final int nextInt = random.nextInt(cells.size());
    final Cell c = cells.get(nextInt);
    robot.setMoveVector(c);
  }
}