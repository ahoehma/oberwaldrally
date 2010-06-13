package de.ahoehma.owr.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ahoehma.owr.Activator;
import de.ahoehma.owr.game.ai.BoardCalculator;
import de.ahoehma.owr.game.ai.RandomCalculator;
import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Cell;
import de.ahoehma.owr.game.core.Symbol;
import de.ahoehma.owr.game.ui.BoardPainter;

public class BoardView extends ViewPart implements PropertyChangeListener {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "de.ahoehma.owr.views.BoardView";

  private Action play;
  private Action pause;
  private Action reset;
  private boolean running = false;
  private boolean calculate = false;

  protected Board board;
  protected BoardPainter boardPainter;
  protected BoardCalculator calculator;
  protected ExecutorService executorService;

  public BoardView() {
    super();
  }

  private void contributeToActionBars() {
    fillLocalToolBar(getViewSite().getActionBars().getToolBarManager());
  }

  protected BoardCalculator createCalculator(final Board board) {
    return new RandomCalculator(board);
  }

  @Override
  public void createPartControl(final Composite parent) {
    parent.setLayout(new FillLayout());
    boardPainter = new BoardPainter(parent);
    makeActions();
    contributeToActionBars();
  }

  @Override
  public void dispose() {
    super.dispose();
    if (executorService != null) {
      executorService.shutdownNow();
    }
  }

  private void fillLocalToolBar(final IToolBarManager manager) {
    manager.add(play);
    manager.add(pause);
    manager.add(reset);
  }

  protected void initBoard(final Board aBoard) {
    System.out.println("Init board ... " + this);
    final int size = aBoard.size() - 1;
    aBoard.getRobots().clear();
    aBoard.addRobot(0, 0, Symbol.BLUE);
    aBoard.addRobot(size, 0, Symbol.GREEN);
    aBoard.addRobot(0, size, Symbol.YELLOW);
    aBoard.addRobot(size, size, Symbol.RED);
    final Random r = new Random(System.currentTimeMillis());
    final int robot = r.nextInt(4);
    List<Cell> targets = new ArrayList<Cell>();
    if (robot == 0) {
      // find blue target
      targets = board.getTargets(Symbol.BLUE);
      aBoard.setSourceCell(0, 0);
    }
    else if (robot == 1) {
      // find green target
      targets = board.getTargets(Symbol.GREEN);
      aBoard.setSourceCell(size, 0);
    }
    else if (robot == 2) {
      // find yellow target
      targets = board.getTargets(Symbol.YELLOW);
      aBoard.setSourceCell(0, size);
    }
    else if (robot == 3) {
      // find red target
      targets = board.getTargets(Symbol.RED);
      aBoard.setSourceCell(size, size);
    }
    if (!targets.isEmpty()) {
      final Cell targetCell = targets.get(r.nextInt(targets.size()));
      aBoard.setTargetCell(targetCell.getCol(), targetCell.getRow());
    }
    else {
      aBoard.setTargetCell(r.nextInt(size), r.nextInt(size));
    }
  }

  private void makeActions() {
    play = new Action() {
      @Override
      public void run() {
        if (!running) {
          play.setEnabled(false);
          executorService = Executors.newFixedThreadPool(2);
          running = true;
          calculate = true;
          executorService.execute(new Runnable() {
            public void run() {
              if (calculator != null) {
                System.out.println("Calculator '" + calculator + "' started ...");
                calculator.start();
                System.out.println("Calculator '" + calculator + "' finished ...");
                play.setEnabled(true);
                running = false;
                calculate = false;
                onCalculatorFinished();
              }
            }
          });
        }
      }
    };
    play.setText("Start");
    play.setImageDescriptor(Activator.getImageDescriptor("icons/play.png"));

    pause = new Action() {
      @Override
      public void run() {
        if (!running) { return; }
        if (calculate) {
          if (calculator != null) {
            calculator.pause(true);
          }
        }
        else {
          if (calculator != null) {
            calculator.pause(false);
          }
        }
        calculate = !calculate;
      }
    };
    pause.setText("Pause");
    pause.setImageDescriptor(Activator.getImageDescriptor("icons/pause.png"));

    reset = new Action() {
      @Override
      public void run() {
        if (executorService != null) {
          executorService.shutdownNow();
        }
        resetBoard();
        updateBoardPainter();
        play.setEnabled(true);
      }

    };
    reset.setText("Reset");
    reset.setImageDescriptor(Activator.getImageDescriptor("icons/reset.png"));
  }

  protected void onCalculatorFinished() {
  // nop
  }

  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    // single step
    if (Board.ROBOT_STEP.equals(evt.getPropertyName())) {
      updateBoardPainter();
    }
  }

  private void resetBoard() {
    calculate = false;
    running = false;
    initBoard(board);
    calculator = createCalculator(board);
    boardPainter.setBoard(board);
  }

  protected void setBoard(final Board aBoard) {
    if (aBoard != null) {
      if (board != null) {
        board.removePropertyChangeListener(this);
      }
      board = aBoard;
      resetBoard();
      board.addPropertyChangeListener(this);
    }
  }

  @Override
  public void setFocus() {
  // nop
  }

  protected void updateBoardPainter() {
    final Display display = PlatformUI.getWorkbench().getDisplay();
    if (display.isDisposed()) { return; }
    display.syncExec(new Runnable() {
      public void run() {
        if (boardPainter.isDisposed()) { return; }
        boardPainter.redraw();
      }
    });
  }
}