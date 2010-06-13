/**
 * 
 */
package de.ahoehma.owr.game.ai;

import java.util.List;

import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Cell;
import de.ahoehma.owr.game.core.Robot;

/**
 * All {@link BoardCalculator}'s have to extend this class.
 * 
 * <p>
 * A {@link BoardCalculator} have the following life cycle:
 * <ol>
 * <li>Creation : The {@link #AbstractCalculator(Board) constructor} will be called with a {@link Board board}. The
 * given board have to define a {@link Cell source-cell} and a {@link Cell target-cell}. The source-cell must contain a
 * {@link Robot robot}.</li>
 * <li>Found solution : The {@link #start()} method will be called to start the algorithm. Each implementation can do
 * what's necessary to find a way for the robot.</li>
 * <li></li>
 * </ol>
 * </p>
 * 
 * @author andreas
 * @since 1.0.0
 */
public abstract class AbstractCalculator implements BoardCalculator {

  private boolean stop;
  private boolean pause;
  protected final Robot robot;
  protected final Board board;

  /**
   * @param aBoard
   * @throws IllegalArgumentException
   */
  public AbstractCalculator(final Board aBoard) throws IllegalArgumentException {
    board = aBoard;
    final Cell sourceCell = board.getSourceCell();
    if (sourceCell == null) { throw new IllegalArgumentException("The given board doesn't contain a source-cell."); }
    robot = board.getRobot(sourceCell);
    if (robot == null) { throw new IllegalArgumentException(String.format(
        "The given board doesn't have a robot on source-cell '%s'", sourceCell)); }
  }

  /**
   * @return a list of {@link RobotMove moves} which solves the given problem, maybe the list is empty if no solution is
   *         know or maybe can't be found in a given time
   */
  abstract List<RobotMove> calculate();

  /**
   * @return <code>true</code> if the {@link Robot robot} from the {@link Cell source-cell} reach the {@link Cell
   *         target-cell} else <code>false</code>
   */
  protected boolean foundSolution() {
    return robot.getCol() == board.getTargetCell().getCol() && robot.getRow() == board.getTargetCell().getRow();
  }

  public final void pause(final boolean flag) {
    pause = flag;
  }

  @Override
  public final void start() {
    pause = false;
    calculate();
  }

  @Override
  public final void stop() {
    stop = true;
  }
}