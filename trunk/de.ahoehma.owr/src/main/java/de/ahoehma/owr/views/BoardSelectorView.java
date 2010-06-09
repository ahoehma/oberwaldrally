package de.ahoehma.owr.views;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ahoehma.owr.data.BoardProvider;

/**
 * @author andreas
 * @since 1.0.0
 */
public class BoardSelectorView extends ViewPart {

  class NameSorter extends ViewerSorter {}

  class ViewContentProvider implements IStructuredContentProvider {
    public void dispose() {}

    public Object[] getElements(final Object parent) {
      return BoardProvider.INSTANCE.getAvailableBoards().toArray();
    }

    public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {}
  }

  class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
    public Image getColumnImage(final Object obj, final int index) {
      return getImage(obj);
    }

    public String getColumnText(final Object obj, final int index) {
      return getText(obj);
    }

    @Override
    public Image getImage(final Object obj) {
      return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
    }
  }

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "de.ahoehma.owr.views.BoardSelectorView";

  private TableViewer viewer;

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(final Composite parent) {
    viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new ViewLabelProvider());
    viewer.setSorter(new NameSorter());
    viewer.setInput(getViewSite());
    viewer.addDoubleClickListener(new IDoubleClickListener() {
      public void doubleClick(final DoubleClickEvent event) {
        final StructuredSelection selection = (StructuredSelection) viewer.getSelection();
        final String boardName = (String) selection.getFirstElement();
        BoardProvider.INSTANCE.loadBoard(boardName);
      }
    });
    getSite().setSelectionProvider(viewer);
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus() {
    viewer.getControl().setFocus();
  }
}