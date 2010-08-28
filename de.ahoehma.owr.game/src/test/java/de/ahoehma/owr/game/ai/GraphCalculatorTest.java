package de.ahoehma.owr.game.ai;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.testng.annotations.Test;

import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Cell;
import de.ahoehma.owr.game.core.Field;
import de.ahoehma.owr.game.core.Symbol;
import de.ahoehma.owr.game.core.Wall;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class GraphCalculatorTest {

  final static Wall[] walls = new Wall[]{Wall.w(1, 0, "O"), Wall.w(6, 1, "NO"), Wall.w(3, 2, "NO"), Wall.w(0, 5, "S"),
      Wall.w(4, 5, "WN"), Wall.w(6, 5, "SO"), Wall.w(6, 7, "W")};

  public static void main(final String[] args) throws FileNotFoundException {
    final Board b = new Board(Field.f("WNOS", walls, null));
    b.addRobot(0, 0, Symbol.BLUE);
    b.setSourceCell(0, 0);
    b.setTargetCell(7, 7);
    final GraphCalculator calculator = new GraphCalculator(b);
    calculator.start();
    final Layout<Cell, RobotMove> layout = new StaticLayout<Cell, RobotMove>(calculator.getGraph(),
        new Transformer<Cell, Point2D>() {
          @Override
          public Point2D transform(final Cell c) {
            return new Point(40 + c.getCol() * 60, 40 + c.getRow() * 60);
          }
        });
    final BasicVisualizationServer<Cell, RobotMove> vv = new BasicVisualizationServer<Cell, RobotMove>(layout);
    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Cell>() {
      @Override
      public String transform(final Cell v) {
        return String.format("%d,%d", v.getCol(), v.getRow());
      }
    });
    vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<RobotMove>() {
      @Override
      public String transform(final RobotMove v) {
        return String.format("%d", v.distance());
      }
    });
    vv.setPreferredSize(new Dimension(500, 500));
    final JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(vv);
    frame.pack();
    frame.setVisible(true);
  }

  @Test
  public void testAlgorithm() {
    final Board b = new Board(Field.f("WNOS", walls, null));
    b.addRobot(0, 0, Symbol.BLUE);
    b.setSourceCell(0, 0);
    b.setTargetCell(7, 7);
    final GraphCalculator calculator = new GraphCalculator(b);
    calculator.start();
  }
}