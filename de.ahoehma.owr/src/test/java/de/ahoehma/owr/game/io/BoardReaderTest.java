package de.ahoehma.owr.game.io;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.ahoehma.owr.game.core.Board;

public class BoardReaderTest {

  @Test
    public void testRead() {
      final Board board = new BoardReader().read(getClass().getResourceAsStream("loadertest-1.0.0.xml"));
      Assert.assertTrue(board.getField().north);
      Assert.assertTrue(board.getField().east);
      Assert.assertEquals(board.getField().size(), 8);
      Assert.assertTrue(board.getField().getCell(1, 1).isEastWall());
      Assert.assertTrue(board.getField().getCell(1, 1).isSouthWall());
    }

}
