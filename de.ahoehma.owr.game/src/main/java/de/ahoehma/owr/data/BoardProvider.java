package de.ahoehma.owr.data;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.ahoehma.owr.Activator;
import de.ahoehma.owr.game.core.Board;
import de.ahoehma.owr.game.io.BoardReader;

/**
 * PDE based BoardProvider. Access to shipped boards. Load all *.xml from plugin "boards" directory.
 * 
 * TODO add extension point for other board providers
 * 
 * TODO add extension point for board reader/writer
 * 
 * @author andreas
 * @since 1.0.0
 */
public class BoardProvider {

  public static final String BOARD = "board";
  public static final BoardProvider INSTANCE = new BoardProvider();
  private final List<URL> availableBoards = new ArrayList<URL>();

  private BoardProvider() {
    final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
    @SuppressWarnings("unchecked")
    final Enumeration<URL> allBoards = bundle.findEntries("boards", "*.xml", true);
    while (allBoards.hasMoreElements()) {
      availableBoards.add(allBoards.nextElement());
    }
  }

  public List<String> getAvailableBoards() {
    final ArrayList<String> result = new ArrayList<String>();
    for (final URL boardURL : availableBoards) {
      result.add(boardURL.getFile());
    }
    return result;
  }

  public Board loadBoard(final String boardName) {
    final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
    final URL resource = bundle.getResource(boardName);
    if (resource != null) {
      try {
        return new BoardReader().read(resource.openStream());
        // propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, BOARD, null, board));
      }
      catch (final IOException e) {
        System.err.println("Could not load board." + e);
      }
    }
    return null;
  }
}