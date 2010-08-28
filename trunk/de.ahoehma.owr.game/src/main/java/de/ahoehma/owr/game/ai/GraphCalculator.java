package de.ahoehma.owr.game.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Cell;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author andreas
 * @since 1.0.0
 */
public class GraphCalculator extends AbstractCalculator {

  private final Graph<Cell, RobotMove> g = new DirectedSparseGraph<Cell, RobotMove>();
  private final List<RobotMove> path = new ArrayList<RobotMove>();

  public GraphCalculator(final Board aBoard) {
    super(aBoard);
  }

  /**
   * Build a graph of possible {@link RobotMove moves} starting a the given {@link Cell}. This method will move the
   * robot from the {@link Cell source-cell} to all reachable {@link Cell cells}. During this a graph will be create. If
   * the method return the graph could be used to calculate the shortes path etc.
   * 
   * @param sourceCell
   *          to start
   */
  private void buildGraph(final Cell sourceCell) {
    final Stack<Cell> cellStack = new Stack<Cell>();
    g.addVertex(sourceCell);
    cellStack.push(sourceCell);
    while (!cellStack.isEmpty()) {
      final Cell c = cellStack.pop();
      robot.setPosition(c);
      final List<Cell> cells = board.getMoveableCells(c);
      if (cells.isEmpty()) {
        continue;
      }
      for (final Cell cell : cells) {
        if (board.isRobot(cell)) {
          continue;
        }
        robot.setPosition(c);
        robot.setMoveVector(cell);
        final List<Cell> visitedCells = board.moveRobot(board, robot);
        if (visitedCells.size() == 1) {
          continue;
        }
        final Cell stopCell = board.getCell(robot.getCol(), robot.getRow());
        if (!g.containsVertex(stopCell)) {
          cellStack.push(stopCell);
          g.addVertex(stopCell);
        }
        g.addEdge(new RobotMove(robot, c, stopCell), c, stopCell);
      }
    }
  }

  @Override
  public List<RobotMove> calculate() {
    final Cell sourceCell = board.getSourceCell();
    final Cell targetCell = board.getTargetCell();
    buildGraph(sourceCell);
    if (g.containsVertex(targetCell)) {
      final DijkstraShortestPath<Cell, RobotMove> alg = new DijkstraShortestPath<Cell, RobotMove>(g);
      path.addAll(alg.getPath(sourceCell, targetCell));
    }
    else {
      System.out.println(String.format("No path from %s to %s", sourceCell, targetCell));
    }
    return path;
  }

  public Graph<Cell, RobotMove> getGraph() {
    return g;
  }

  public List<RobotMove> getPath() {
    return path;
  }
}