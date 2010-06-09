package de.ahoehma.owr.game.ai;

import java.util.List;
import java.util.Stack;

import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Cell;
import de.ahoehma.owr.game.core.Robot;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author andreas
 * @since 1.0.0
 */
public class GraphCalculator implements BoardCalculator {

  private final Board board;
  private Graph<Cell, RobotMove> g;
  private List<RobotMove> path;

  public GraphCalculator(final Board aBoard) {
    board = aBoard;
  }

  public Graph<Cell, RobotMove> getGraph() {
    return g;
  }

  public List<RobotMove> getPath() {
    return path;
  }

  @Override
  public void pause(final boolean flag) {
  }

  public void start() {
    final Cell sourceCell = board.getSourceCell();
    final Robot r = board.getRobot(sourceCell);
    if (r == null) {
      return;
    }
    final Cell targetCell = board.getTargetCell();
    final Stack<Cell> cellStack = new Stack<Cell>();
    g = new DirectedSparseGraph<Cell, RobotMove>();
    g.addVertex(sourceCell);
    cellStack.push(sourceCell);
    while (!cellStack.isEmpty()) {
      final Cell c = cellStack.pop();
      r.setPosition(c);
      final List<Cell> cells = board.getMoveableCells(c);
      if (cells.isEmpty()) {
        continue;
      }
      for (final Cell cell : cells) {
        if (board.isRobot(cell)) {
          continue;
        }
        r.setPosition(c);
        r.setMoveVector(cell);
        final List<Cell> visitedCells = board.moveRobot(board, r);
        if (visitedCells.size() == 1) {
          continue;
        }
        final Cell stopCell = board.getCell(r.getCol(), r.getRow());
        if (!g.containsVertex(stopCell)) {
          cellStack.push(stopCell);
          g.addVertex(stopCell);
        }
        g.addEdge(new RobotMove(c, stopCell), c, stopCell);
      }
    }
    if (g.containsVertex(targetCell)) {
      final DijkstraShortestPath<Cell, RobotMove> alg = new DijkstraShortestPath<Cell, RobotMove>(g);
      path = alg.getPath(sourceCell, targetCell);
    } else {
      path = null;
      System.out.println(String.format("No path from %s to %s", sourceCell, targetCell));
    }
  }
}