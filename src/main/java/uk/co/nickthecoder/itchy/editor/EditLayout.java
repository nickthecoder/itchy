package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.Component;

public class EditLayout extends EditNamedSubject<Layout>
{

    public EditLayout(Resources resources, ListSubjects<Layout> listSubjects, Layout subject, boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
    }

    @Override
    protected String getSubjectName()
    {
        return "Layout";
    }

    @Override
    protected Layout getSubjectByName(String name)
    {
        return resources.getLayout(name);
    }

    @Override
    protected void add()
    {
        resources.addLayout(subject);
    }

    @Override
    protected void rename()
    {
        resources.renameLayout(subject);
    }

    @Override
    protected Component createForm()
    {
        super.createForm();
        ListLayers listLayers = new ListLayers(this.resources,this.subject);
        listLayers.buttonsBelow = false;
        
        this.form.grid.addRow("Layers", listLayers.createPage());
        
        return this.form.container;
    }
}
