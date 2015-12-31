/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.List;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;

public class StyleRule
{
    public static final int NOT_SET = Integer.MAX_VALUE;
    public static final Renderable NO_BACKGROUND = new NullRenderer();

    private final List<StyleCriteria> criteria;

    public int marginTop;
    public int marginRight;
    public int marginBottom;
    public int marginLeft;

    public int paddingTop;
    public int paddingRight;
    public int paddingBottom;
    public int paddingLeft;

    public int minimumWidth;
    public int minimumHeight;

    public int maximumWidth;
    public int maximumHeight;

    public Font font;
    public int fontSize;
    public RGBA color;
    public Renderable background;
    public Surface image;

    public int xSpacing;
    public int ySpacing;

    public StyleRule( List<StyleCriteria> criteria )
    {
        this.criteria = criteria;

        this.marginTop = NOT_SET;
        this.marginRight = NOT_SET;
        this.marginBottom = NOT_SET;
        this.marginLeft = NOT_SET;

        this.paddingTop = NOT_SET;
        this.paddingRight = NOT_SET;
        this.paddingBottom = NOT_SET;
        this.paddingLeft = NOT_SET;

        this.maximumHeight = NOT_SET;
        this.maximumWidth = NOT_SET;

        this.minimumHeight = NOT_SET;
        this.minimumWidth = NOT_SET;

        this.xSpacing = NOT_SET;
        this.ySpacing = NOT_SET;

        this.font = null;
        this.fontSize = NOT_SET;
        this.background = null;
        this.image = null;
    }

    public boolean matches( Component component )
    {
        return this.matches(component, this.criteria.size() - 1);
    }

    private boolean matches( Component component, int index )
    {
        for (int i = index; i >= 0; i--) {
            StyleCriteria criteria = this.criteria.get(i);

            if (component == null) {
                return false;
            }

            if (criteria.wildcard) {

                for (Component parent = component; parent != null; parent = parent.getParent()) {
                    if (this.matches(parent, i - 1)) {
                        return true;
                    }
                }
                return false;

            } else {
                if ((criteria.style != null) && (!component.getStyles().contains(criteria.style))) {
                    return false;
                }
                if ((criteria.type != null) && (!criteria.type.equals(component.getType()))) {
                    return false;
                }
            }
            component = component.getParent();
        }

        return true;
    }

    public void apply( AbstractComponent component )
    {
        if (this.marginTop != NOT_SET) {
            component.setMarginTop(this.marginTop);
        }
        if (this.marginRight != NOT_SET) {
            component.setMarginRight(this.marginRight);
        }
        if (this.marginBottom != NOT_SET) {
            component.setMarginBottom(this.marginBottom);
        }
        if (this.marginLeft != NOT_SET) {
            component.setMarginLeft(this.marginLeft);
        }

        if (this.minimumWidth != NOT_SET) {
            component.setMinimumWidth(this.minimumWidth);
        }
        if (this.minimumHeight != NOT_SET) {
            component.setMinimumHeight(this.minimumHeight);
        }
        if (this.maximumWidth != NOT_SET) {
            component.setMaximumWidth(this.maximumWidth);
        }
        if (this.maximumHeight != NOT_SET) {
            component.setMaximumHeight(this.maximumHeight);
        }

        if (component instanceof PlainContainer) {
            Container container = (Container) component;

            if (this.xSpacing != NOT_SET) {
                container.setXSpacing(this.xSpacing);
            }
            if (this.ySpacing != NOT_SET) {
                container.setYSpacing(this.ySpacing);
            }

            if (this.paddingTop != NOT_SET) {
                container.setPaddingTop(this.paddingTop);
            }
            if (this.paddingRight != NOT_SET) {
                container.setPaddingRight(this.paddingRight);
            }
            if (this.paddingBottom != NOT_SET) {
                container.setPaddingBottom(this.paddingBottom);
            }
            if (this.paddingLeft != NOT_SET) {
                container.setPaddingLeft(this.paddingLeft);
            }

        }

        if (component instanceof ImageComponent) {
            if (this.image != null) {
                ((ImageComponent) component).setImage(this.image);
            }
        }

        if (this.font != null) {
            component.setFontFromRule(this.font);
        }

        if (this.fontSize != NOT_SET) {
            component.setFontSize(this.fontSize);
        }

        component.setColor(this.color);

        if (this.background == NO_BACKGROUND) {
            component.setBackground(null);
        } else {
            component.setBackground(this.background);
        }

    }

    @Override
    public String toString()
    {
        return "Criteria " + this.criteria;
    }

    public void merge( StyleRule other )
    {
        if (other.marginTop != NOT_SET) {
            this.marginTop = other.marginTop;
        }
        if (other.marginRight != NOT_SET) {
            this.marginRight = other.marginRight;
        }
        if (other.marginBottom != NOT_SET) {
            this.marginBottom = other.marginBottom;
        }
        if (other.marginLeft != NOT_SET) {
            this.marginLeft = other.marginLeft;
        }

        if (other.paddingTop != NOT_SET) {
            this.paddingTop = other.paddingTop;
        }
        if (other.paddingRight != NOT_SET) {
            this.paddingRight = other.paddingRight;
        }
        if (other.paddingBottom != NOT_SET) {
            this.paddingBottom = other.paddingBottom;
        }
        if (other.paddingLeft != NOT_SET) {
            this.paddingLeft = other.paddingLeft;
        }

        if (other.minimumWidth != NOT_SET) {
            this.minimumWidth = other.minimumWidth;
        }
        if (other.minimumHeight != NOT_SET) {
            this.minimumHeight = other.minimumHeight;
        }
        if (other.maximumWidth != NOT_SET) {
            this.maximumWidth = other.maximumWidth;
        }
        if (other.maximumHeight != NOT_SET) {
            this.maximumHeight = other.maximumHeight;
        }

        if (other.xSpacing != NOT_SET) {
            this.xSpacing = other.xSpacing;
        }
        if (other.ySpacing != NOT_SET) {
            this.ySpacing = other.ySpacing;
        }

        if (other.font != null) {
            this.font = other.font;
        }

        if (other.fontSize != NOT_SET) {
            this.fontSize = other.fontSize;
        }

        if (other.color != null) {
            this.color = other.color;
        }

        if (other.background != null) {
            this.background = other.background;
        }

        if (other.image != null) {
            this.image = other.image;
        }

    }

}
