package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeFeatures;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ClassNameBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.PropertiesForm;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.property.CostumeProperty;

public class EditCostume extends EditNamedSubject<Costume>
{
    private Notebook notebook;

    private PlainContainer propertiesContainer;


    public EditCostume(Resources resources, ListSubjects<Costume> listSubjects, Costume subject, boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
    }

    @Override
    protected String getSubjectName()
    {
        return "Costume";
    }

    @Override
    protected Costume getSubjectByName(String name)
    {
        return resources.getCostume(name);
    }

    @Override
    protected void add()
    {
        resources.addCostume(subject);
    }

    @Override
    protected void rename()
    {
        resources.renameCostume(subject);
    }

    @Override
    protected Component createForm()
    {
        form.add( new CostumeProperty<Costume>("extendedFrom").hint("Extends From").allowNull(true) );

        super.createForm();

        final ClassNameBox cnb = (ClassNameBox) form.getComponent("roleClassName");
        cnb.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                if (cnb.getClassName().isValid(resources.scriptManager)) {
                    subject.roleClassName = cnb.getClassName();
                    createPropertiesGrid();
                }
            }
        });



        notebook = new Notebook();

        PlainContainer eventsPage = new PlainContainer();
        PlainContainer propertiesPage = new PlainContainer();

        notebook.addPage("Details", form.container);
        notebook.addPage("Events", eventsPage);
        notebook.addPage("Properties", propertiesPage);

        propertiesPage.setLayout(new VerticalLayout());

        propertiesContainer = new PlainContainer();
        propertiesPage.addChild(propertiesContainer);
        createPropertiesGrid();

        ListEvents listEvents = new ListEvents(resources, subject);
        listEvents.buttonsBelow = false;
        eventsPage.addChild(listEvents.createPage());
        
        return notebook;
    }

    private PropertiesForm<CostumeFeatures> initialCostumeFeaturesForm;
    
    private void createPropertiesGrid()
    {
        this.propertiesContainer.clear();
        
        CostumeFeatures costumeFeatures = subject.getCostumeFeatures();

        PropertiesForm<CostumeFeatures> form = new PropertiesForm<CostumeFeatures>(
            costumeFeatures, costumeFeatures.getProperties());
        form.autoUpdate = true;

        this.propertiesContainer.addChild( form.createForm() );
        
        // Remember the FIRST form that was created, as this will have the values needed to revert
        // the costume features back to their original values. See onCancel.
        if ( initialCostumeFeaturesForm == null ) {
            initialCostumeFeaturesForm = form;
        }
    }

    protected void onCancel()
    {
        super.onCancel();
        this.initialCostumeFeaturesForm.revert(subject.getCostumeFeatures());
    }
}
