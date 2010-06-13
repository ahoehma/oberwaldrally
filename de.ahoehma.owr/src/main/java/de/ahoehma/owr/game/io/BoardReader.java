package de.ahoehma.owr.game.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.core.Field;
import de.ahoehma.owr.game.core.Wall;
import de.ahoehma.owr.game.xml.BoardType;
import de.ahoehma.owr.game.xml.FieldType;
import de.ahoehma.owr.game.xml.WallType;

/**
 * @author andreas
 * @since 1.0.0
 */
public class BoardReader {

  private Document parse(final InputSource source) {
    try {
      final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      final DocumentBuilder db = dbf.newDocumentBuilder();
      final Document document = db.parse(source);
      return document;
    }
    catch (final ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
    catch (final SAXException e) {
      throw new RuntimeException(e);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param stream
   * @return
   */
  public Board read(final InputStream stream) {
    final Document document = parse(new InputSource(stream));
    try {
      final JAXBContext jc = JAXBContext.newInstance("de.ahoehma.owr.game.xml");
      final Unmarshaller um = jc.createUnmarshaller();
      final Element root = document.getDocumentElement();
      final JAXBElement<BoardType> unmarshal = um.unmarshal(root, BoardType.class);
      final BoardType umBoard = unmarshal.getValue();
      final List<Field> fields = new ArrayList<Field>();
      final List<FieldType> xmlFields = umBoard.getFields().getField();
      for (final FieldType fieldType : xmlFields) {
        final List<Wall> fieldWalls = new ArrayList<Wall>();
        final List<WallType> xmlWalls = fieldType.getWall();
        for (final WallType wallType : xmlWalls) {
          fieldWalls.add(Wall.w(wallType.getCol(), wallType.getRow(), wallType.getBorder()));
        }
        fields.add(Field.f(fieldType.getSize().intValue(), fieldType.getBorder(), fieldWalls
            .toArray(new Wall[fieldWalls.size()]), null));
      }
      return Board.b(fields);
    }
    catch (final JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}