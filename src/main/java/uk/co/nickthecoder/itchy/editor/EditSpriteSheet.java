package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SpriteSheet;
import uk.co.nickthecoder.itchy.gui.Component;

public class EditSpriteSheet extends EditNamedSubject<SpriteSheet>
{

    public EditSpriteSheet(Resources resources, ListSubjects<SpriteSheet> listSubjects, SpriteSheet subject,
        boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
    }

    @Override
    protected String getSubjectName()
    {
        return "Sprite Sheet";
    }

    @Override
    protected SpriteSheet getSubjectByName(String name)
    {
        return this.resources.getSpriteSheet(name);
    }

    @Override
    protected void add()
    {
        resources.addSpriteSheet(subject);
    }

    @Override
    protected void rename()
    {
        resources.renameSpriteSheet(subject);
    }

    @Override
    protected Component createForm()
    {
        super.createForm();
        ListSprites listSprites = new ListSprites(this.resources,this.subject);
        listSprites.buttonsBelow = false;
        
        this.form.grid.addRow("Sprites", listSprites.createPage());
        
        return this.form.container;
    }
}
