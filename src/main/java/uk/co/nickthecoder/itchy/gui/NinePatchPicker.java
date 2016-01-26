package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.util.NinePatch;

public abstract class NinePatchPicker extends Window
{
    protected final Resources resources;

    private NinePatch defaultNinePatch;

    public NinePatchPicker( Resources resources )
    {
        this(resources, null);
    }

    public NinePatchPicker( Resources resources, NinePatch defaultNinePatch )
    {
        super("Pick a Nine Patch");
        this.resources = resources;
        this.defaultNinePatch = defaultNinePatch;

        this.clientArea.setLayout(new VerticalLayout());
        this.clientArea.setFill(true, false);

        PlainContainer container = new PlainContainer();
        container.setLayout(new VerticalLayout());
        VerticalScroll vs = new VerticalScroll(container);

        Component focus = this.createChoices(container);
        this.clientArea.addChild(vs);
        this.clientArea.addStyle("vScrolled");

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setLayout(new HorizontalLayout());
        buttons.setXAlignment(0.5f);

        GuiButton cancel = new GuiButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                NinePatchPicker.this.hide();
            }

        });
        buttons.addChild(cancel);
        this.clientArea.addChild(buttons);

        if (focus != null) {
            focus.focus();
        }
    }

    protected Component createChoices( Container parent )
    {
        PlainContainer container = new PlainContainer();
        parent.addChild(container);

        Component focus = null;

        GridLayout gridLayout = new GridLayout(container, 5);
        container.setLayout(gridLayout);
        container.addStyle("pickGrid");

        for (String name : this.resources.ninePatchNames()) {
            NinePatch ninePatch = this.resources.getNinePatch(name);

            AbstractComponent component = this.createNinePatchButton(ninePatch);
            if (ninePatch == this.defaultNinePatch) {
                focus = component;
            }

            gridLayout.addChild(component);
        }
        gridLayout.endRow();

        return focus;
    }

    private AbstractComponent createNinePatchButton( final NinePatch ninePatch )
    {
        PlainContainer container = new PlainContainer();

        container.setLayout(new VerticalLayout());
        container.setXAlignment(0.5f);

        ImageComponent img = new ImageComponent(); 
        if ( ninePatch != null) {
            img.setImage(ninePatch.getThumbnail());
        }
        GuiButton button = new GuiButton(img);
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                NinePatchPicker.this.hide();
                NinePatchPicker.this.pick(ninePatch);
            }
        });

        Label label = new Label(ninePatch == null ? "<none>" : ninePatch.getName());

        container.addChild(button);
        container.addChild(label);

        return container;
    }

    public abstract void pick( NinePatch ninePatch );

}
