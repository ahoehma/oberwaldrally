package de.ahoehma.owr.game.io;

import java.io.OutputStream;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Wall;
import de.ahoehma.owr.game.xml.BoardType;
import de.ahoehma.owr.game.xml.FieldType;
import de.ahoehma.owr.game.xml.ObjectFactory;
import de.ahoehma.owr.game.xml.WallType;

public class BoardWriter {

  private final Board board;

  public BoardWriter(final Board aBoard) {
    board = aBoard;
  }

  public void write(final OutputStream stream) {
    try {
      final ObjectFactory factory = new ObjectFactory();
      final BoardType boardType = factory.createBoardType();
      boardType.setFields(factory.createBoardTypeFields());
      final FieldType fieldType = factory.createFieldType();
      fieldType.setBorder(board.getField().getBorder());
      fieldType.setSize(BigInteger.valueOf(board.getField().size()));
      for (final Wall wall : board.getField().walls) {
        final WallType xmlWall = factory.createWallType();
        xmlWall.setCol(Integer.toString(wall.getCol()));
        xmlWall.setRow(Integer.toString(wall.getRow()));
        xmlWall.setBorder(wall.getBorder());
        fieldType.getWall().add(xmlWall);
      }
      boardType.getFields().getField().add(fieldType);
      final JAXBContext jc = JAXBContext.newInstance("de.ahoehma.owr.game.xml");
      final Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(factory.createBoard(boardType), stream);
    }
    catch (final JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}
