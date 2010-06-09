package de.ahoehma.owr.game.core;

public class Symbol {

  public int col, row;
  public int color;
  public int type;

  public static final int RED = 0x1;
  public static final int BLUE = 0x2;
  public static final int GREEN = 0x4;
  public static final int YELLOW = 0x8;

  public static final int CIRCLE = 0x10;
  public static final int TRIANGLE = 0x20;
  public static final int SATURN = 0x40;
  public static final int STAR = 0x80;

  public static final int SPECIAL = 0x100;
  public static final int CENTER = 0x200;

  public static Symbol s(final int col, final int row, final int flag) {
    final Symbol symbol = new Symbol();
    symbol.col = col;
    symbol.row = row;
    if ((flag & RED) != 0) {
      symbol.color = RED;
    }
    if ((flag & GREEN) != 0) {
      symbol.color = GREEN;
    }
    if ((flag & BLUE) != 0) {
      symbol.color = BLUE;
    }
    if ((flag & YELLOW) != 0) {
      symbol.color = YELLOW;
    }
    if ((flag & CIRCLE) != 0) {
      symbol.type = CIRCLE;
    }
    if ((flag & TRIANGLE) != 0) {
      symbol.type = TRIANGLE;
    }
    if ((flag & SATURN) != 0) {
      symbol.type = SATURN;
    }
    if ((flag & STAR) != 0) {
      symbol.type = STAR;
    }
    if ((flag & SPECIAL) != 0) {
      symbol.type = SPECIAL;
      symbol.color = SPECIAL;
    }
    if ((flag & CENTER) != 0) {
      symbol.type = CENTER;
      symbol.color = CENTER;
    }
    return symbol;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Symbol [col=");
    builder.append(col);
    builder.append(", color=");
    builder.append(color);
    builder.append(", row=");
    builder.append(row);
    builder.append(", type=");
    builder.append(type);
    builder.append("]");
    return builder.toString();
  }
}
