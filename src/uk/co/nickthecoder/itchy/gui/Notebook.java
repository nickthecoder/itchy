/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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

    public Tab addPage( String label, Component page )
    {
        return addPage(new Label(label), page);
    }

    public Tab addPage( Component label, Component page )
    {
        Tab tab = new Tab(label, page, this.tabs.getChildren().size());
        this.tabs.addChild(tab);
        this.pages.addChild(page);

        boolean selected = (this.tabs.getChildren().size() == 1);
        tab.setSelected(selected);
        page.setVisible(selected);

        return tab;
    }

    public int size()
    {
        return this.pages.getChildren().size();
    }

    public Button getTab( int pageIndex )
    {
        return (Tab) this.tabs.getChildren().get(pageIndex);
    }

    public Component getPage( int pageIndex )
    {
        return this.pages.getChildren().get(pageIndex);
    }

    @Override
    public void ensureVisible( Component component )
    {
        for (Container parent = component.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getParent() == this.pages) {

                for (Component atab : this.tabs.getChildren()) {
                    Tab tab = (Tab) atab;
                    if (tab.getPage() == parent) {
                        tab.select();
                        return;
                    }
                }

            }
        }
    }

    /**
     * Reverses the normal order of rendering, so that the tabs are blitted after the pages. This allows the tabs to slightly overlap the
     * pages, if the notebook has a negative spacing.
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

    public class Tab extends Button
    {
        private final Component page;

        private boolean selected;

        private final int pageNumber;

        Tab( Component label, Component page, int pageNumber )
        {
            this.addChild(label);
            // this.tooltip = "Page " + pageNumber; // TODO REMOVE
            this.page = page;
            this.type = "tab";
            this.focusable = true;
            this.pageNumber = pageNumber;
        }

        public void select()
        {
            Notebook.this.selectPage(this.pageNumber);
        }

        void setSelected( boolean value )
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
        public void onClick( MouseButtonEvent event )
        {
            Notebook.this.selectPage(this.pageNumber);
            super.onClick(event);
            getPage().focus();
        }
    }

}
