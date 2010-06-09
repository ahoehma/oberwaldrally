package de.ahoehma.owr.game.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author andreas
 * @since 1.0.0
 */
public final class BoardUtils {

  public static Symbol[] getMergedTargets(final Field fieldWN, final Field fieldNO, final Field fieldWS,
      final Field fieldSO, final Integer fieldSize) {
    final List<Symbol> symbols = new ArrayList<Symbol>();
    final Symbol[] wn = fieldWN.targets;
    for (int i = 0; i < wn.length; i++) {
      final Symbol s = wn[i];
      symbols.add(Symbol.s(s.col, s.row, s.type | s.color));
    }
    final Symbol[] no = fieldNO.targets;
    for (int i = 0; i < no.length; i++) {
      final Symbol s = no[i];
      symbols.add(Symbol.s(s.col + fieldSize, s.row, s.type | s.color));
    }
    final Symbol[] ws = fieldWS.targets;
    for (int i = 0; i < ws.length; i++) {
      final Symbol s = ws[i];
      symbols.add(Symbol.s(s.col, s.row + fieldSize, s.type | s.color));
    }
    final Symbol[] so = fieldSO.targets;
    for (int i = 0; i < so.length; i++) {
      final Symbol s = so[i];
      symbols.add(Symbol.s(s.col + fieldSize, s.row + fieldSize, s.type | s.color));
    }
    return symbols.toArray(new Symbol[symbols.size()]);
  }

  public static Wall[] getMergedWalls(final Field fieldWN, final Field fieldNO, final Field fieldWS,
      final Field fieldSO, final Integer fieldSize) {
    final List<Wall> walls = new ArrayList<Wall>();
    final Wall[] wallsWN = fieldWN.walls;
    for (int i = 0; i < wallsWN.length; i++) {
      final Wall w = wallsWN[i];
      walls.add(Wall.w(w.getCol(), w.getRow(), w.getBorder()));
    }
    final Wall[] wallsNO = fieldNO.walls;
    for (int i = 0; i < wallsNO.length; i++) {
      final Wall w = wallsNO[i];
      walls.add(Wall.w(w.getCol() + fieldSize, w.getRow(), w.getBorder()));
    }
    final Wall[] wallsWS = fieldWS.walls;
    for (int i = 0; i < wallsWS.length; i++) {
      final Wall w = wallsWS[i];
      walls.add(Wall.w(w.getCol(), w.getRow() + fieldSize, w.getBorder()));
    }
    final Wall[] wallsSO = fieldSO.walls;
    for (int i = 0; i < wallsSO.length; i++) {
      final Wall w = wallsSO[i];
      walls.add(Wall.w(w.getCol() + fieldSize, w.getRow() + fieldSize, w.getBorder()));
    }
    return walls.toArray(new Wall[walls.size()]);
  }

  public static List<Cell> getRobotFreeCells(final Board board, final List<Cell> cells) {
    final List<Cell> freeCells = new ArrayList<Cell>();
    for (final Cell cell : cells) {
      if (!board.isRobot(cell)) {
        freeCells.add(cell);
      }
    }
    return freeCells;
  }

  private BoardUtils() {}

}
