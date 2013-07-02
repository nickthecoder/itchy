package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class Notebook extends Container
{
    private final Container tabs;

    private final Container pages;

    public Notebook()
    {
        super();

        this.tabs = new Container();
        this.tabs.type = "notebookTabs";

        this.pages = new Container();
        this.pages.setLayout(new OverlappingLayout());
        this.pages.type = "notebookPages";
        this.pages.setFill(true, true);

        this.layout = new VerticalLayout();
        this.addChild(this.tabs);
        this.addChild(this.pages);

        this.pages.setExpansion(1);
        this.type = "notebook";
    }

    public void addPage( Component label, Component page )
    {
        Tab tab = new Tab(label, page, this.tabs.getChildren().size());
        this.tabs.addChild(tab);
        this.pages.addChild(page);

        boolean selected = (this.tabs.getChildren().size() == 1);
        tab.setSelected(selected);
        page.setVisible(selected);

    }

    /**
     * Reverses the normal order of rendering, so that the tabs are blitted after the pages. This
     * allows the tabs to slightly overlap the pages, if the notebook has a negative spacing.
     */
    @Override
    public void render( GraphicsContext gc )
    {
        this.renderBackground(gc);

        Rect rect = new Rect(this.pages.getX(), this.pages.getY(), this.pages.getWidth(),
                this.pages.getHeight());
        this.pages.render(gc.window(rect));

        rect = new Rect(this.tabs.getX(), this.tabs.getY(), this.tabs.getWidth(),
                this.tabs.getHeight());
        this.tabs.render(gc.window(rect));

    }

    public void selectPage( int pageIndex )
    {
        int index = 0;
        for (Component page : this.pages.getChildren()) {
            page.setVisible(index == pageIndex);
            index++;
        }
        index = 0;
        for (Component child : this.tabs.getChildren()) {
            Tab tab = (Tab) child;
            tab.setSelected(index == pageIndex);
            index++;
        }
    }

    class Tab extends ClickableContainer
    {
        private final Component page;

        private boolean selected;

        private final int pageNumber;

        Tab( Component label, Component page, int pageNumber )
        {
            this.addChild(label);
            this.page = page;
            this.type = "tab";
            this.focusable = true;
            this.pageNumber = pageNumber;
        }

        public void setSelected( boolean value )
        {
            this.selected = value;
            if (this.selected) {
                this.addStyle("selected");
            } else {
                this.removeStyle("selected");
            }
            this.invalidate();
        }

        public Component getPage()
        {
            return this.page;
        }

        public boolean isSelected()
        {
            return this.selected;
        }

        @Override
        public void onClick( MouseButtonEvent e )
        {
            Notebook.this.selectPage(this.pageNumber);
        }
    }

}
