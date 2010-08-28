package de.ahoehma.owr.game.io;

import org.testng.annotations.Test;

import de.ahoehma.owr.game.core.Board;

public class BoardWriterTest {
  @Test
    public void testWrite() {
      new BoardWriter(Board.DEFAULT_BOARD).write(System.out);
    }
}
