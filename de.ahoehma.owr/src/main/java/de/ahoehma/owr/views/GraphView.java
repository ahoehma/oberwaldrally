package de.ahoehma.owr.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections15.Transformer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import de.ahoehma.owr.data.BoardProvider;
import de.ahoehma.owr.game.ai.BoardCalculator;
import de.ahoehma.owr.game.ai.GraphCalculator;
import de.ahoehma.owr.game.ai.RobotMove;
import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Cell;
import de.ahoehma.owr.game.core.Symbol;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GraphView extends BoardView {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "de.ahoehma.owr.views.GraphView";

  private Canvas graphCanvas;

  private Image graphImage;

  public GraphView() {
    super();
  }
  private static Image makeSWTImage(final Display display, final java.awt.Image ai) throws Exception {
    final int width = ai.getWidth(null);
    final int height = ai.getHeight(null);
    final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    final Graphics2D g2d = bufferedImage.createGraphics();
    g2d.drawImage(ai, 0, 0, null);
    g2d.dispose();
    final int[] data = ((DataBufferInt) bufferedImage.getData().getDataBuffer()).getData();
    final ImageData imageData = new ImageData(width, height, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
    imageData.setPixels(0, 0, data.length, data, 0);
    final Image swtImage = new Image(display, imageData);
    return swtImage;
  }

  @Override
  public void createPartControl(final Composite parent) {
    super.createPartControl(parent);
    graphCanvas = new Canvas(parent, SWT.NONE);
    graphCanvas.addListener(SWT.Paint, new Listener() {
      public void handleEvent(final Event e) {
        final GC gc = e.gc;
        if (graphImage != null) {
          gc.drawImage(graphImage, 0, 0);
        }
      }
    });
  }

  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    // super.propertyChange(evt);
    if (BoardProvider.BOARD.equals(evt.getPropertyName())) {
      setBoard(BoardProvider.INSTANCE.getBoard());
      updateBoardPainter();
      if (graphImage != null) {
        graphImage.dispose();
      }
      graphImage = null;
    }
    // only complete steps
    if (Board.ROBOT_STEP.equals(evt.getPropertyName())) {
      // createGraphImage(null);
      // updateBoardPainter();
    }
  }

  @Override
  protected BoardCalculator createCalculator(final Board board) {
    return new GraphCalculator(board);
  }

  @Override
  protected void initBoard(final Board aBoard) {
    System.out.println("Init board ... " + this);
    final int size = aBoard.size();
    aBoard.getRobots().clear();
    final Random r = new Random(System.currentTimeMillis());
    int col = r.nextInt(size - 1);
    int row = r.nextInt(size - 1);
    aBoard.addRobot(col, row, Symbol.BLUE);
    aBoard.setSourceCell(col, row);
    col = r.nextInt(size - 1);
    row = r.nextInt(size - 1);
    aBoard.setTargetCell(col, row);
  }

  @Override
  protected void onCalculatorFinished() {
    final GraphCalculator c = (GraphCalculator) calculator;
    final List<RobotMove> path = c.getPath();
    createGraphImage(path);
  }

  private void createGraphImage(final List<RobotMove> thePath) {
    final int cellSize = boardPainter.getCellSize();
    final Dimension preferredSize = new Dimension(cellSize * (board.size() + 1), cellSize * (board.size() + 1));
    final Graph<Cell, RobotMove> graph = ((GraphCalculator) calculator).getGraph();
    final Layout<Cell, RobotMove> layout = new StaticLayout<Cell, RobotMove>(graph, new Transformer<Cell, Point2D>() {
      @Override
      public Point2D transform(final Cell c) {
        return new Point(cellSize / 2 + c.getCol() * cellSize, cellSize / 2 + c.getRow() * cellSize);
      }
    });
    final VisualizationImageServer<Cell, RobotMove> vv = new VisualizationImageServer<Cell, RobotMove>(layout,
        preferredSize);
    if (thePath != null) {
      vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Cell, Paint>() {
        @Override
        public Paint transform(final Cell cell) {
          return isPathCell(thePath, cell) ? Color.GREEN : Color.WHITE;
        }
      });
      vv.getRenderContext().setEdgeDrawPaintTransformer(new Transformer<RobotMove, Paint>() {
        @Override
        public Paint transform(final RobotMove move) {
          return thePath.contains(move) ? Color.GREEN : Color.BLACK;
        }
      });
    }
    vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
    final Display display = PlatformUI.getWorkbench().getDisplay();
    if (display.isDisposed()) {
      return;
    }
    display.syncExec(new Runnable() {
      public void run() {
        try {
          if (graphImage != null) {
            graphImage.dispose();
            graphImage = null;
          }
          final java.awt.Image image = vv.getImage(new Point(0, 0), preferredSize);
          if (image != null) {
            graphImage = makeSWTImage(display, image);
            graphCanvas.redraw();
          }
        } catch (final Exception e) {
          System.err.println("Can't make image " + e);
        }
      }
    });
  }

  private boolean isPathCell(final List<RobotMove> thePath, final Cell cell) {
    for (final RobotMove robotMove : thePath) {
      if (robotMove.contains(cell)) {
        return true;
      }
    }
    return false;
  }
}