package de.ahoehma.owr.game.xml;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class JAXBTest {

  @Test
  public void testMarshall() throws JAXBException, IOException, ParserConfigurationException, SAXException {
    final JAXBContext jc = JAXBContext.newInstance("de.ahoehma.owr.game.xml");
    final ObjectFactory factory = new ObjectFactory();
    final BoardType board = factory.createBoardType();
    board.setFields(factory.createBoardTypeFields());
    final FieldType field = factory.createFieldType();
    field.setBorder("NO");
    field.setSize(BigInteger.valueOf(8));
    final WallType wall1 = factory.createWallType();
    wall1.setCol("1");
    wall1.setRow("1");
    wall1.setBorder("OS");
    field.getWall().add(wall1);
    board.getFields().getField().add(field);
    final Marshaller m = jc.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    final File file = File.createTempFile("board", ".xml");
    file.deleteOnExit();
    m.marshal(factory.createBoard(board), file);
    m.marshal(factory.createBoard(board), System.out);

    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    final DocumentBuilder db = dbf.newDocumentBuilder();
    final Document doc = db.parse(file);
    final Unmarshaller um = jc.createUnmarshaller();
    final JAXBElement<BoardType> unmarshal = um.unmarshal(doc.getDocumentElement(), BoardType.class);
    final BoardType umBoard = unmarshal.getValue();

    Assert.assertEquals(umBoard.getFields().getField().size(), 1);
    Assert.assertEquals(umBoard.getFields().getField().get(0).getBorder(), "NO");
  }
}