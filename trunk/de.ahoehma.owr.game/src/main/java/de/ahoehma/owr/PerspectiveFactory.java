package de.ahoehma.owr;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ahoehma.owr.views.BoardSelectorView;
import de.ahoehma.owr.views.BoardView;
import de.ahoehma.owr.views.GraphView;

public class PerspectiveFactory implements IPerspectiveFactory {

  @Override
  public void createInitialLayout(final IPageLayout layout) {
    layout.addView(BoardSelectorView.ID, IPageLayout.LEFT, 0.30f, layout.getEditorArea());
    final IFolderLayout bot = layout.createFolder("TOP", IPageLayout.TOP, 0.76f, layout.getEditorArea());
    bot.addView(BoardView.ID);
    bot.addView(GraphView.ID);
  }
}
