package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Layer;
import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Stage;
import uk.co.nickthecoder.itchy.StageConstraint;
import uk.co.nickthecoder.itchy.StageView;
import uk.co.nickthecoder.itchy.View;
import uk.co.nickthecoder.itchy.gui.ClassNameBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.PropertiesForm;

public class EditLayer extends EditNamedSubject<Layer>
{

    private Layout layout;


    private Container stagePropertiesContainer;
    private Container viewPropertiesContainer;
    private Container stageConstraintPropertiesContainer;
    private Notebook notebook;

    private PropertiesForm<View> initialViewPropertiesForm = null;
    private PropertiesForm<Stage> initialStagePropertiesForm = null;
    private PropertiesForm<StageConstraint> initialStageConstraintPropertiesForm = null;

    /**
     * The old name - used to keep track of renamed layers.
     */
    private String oldName;
    
    public EditLayer(Resources resources, ListSubjects<Layer> listSubjects, Layout layout, Layer subject, boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
        this.layout = layout;
    }

    @Override
    protected String getSubjectName()
    {
        return "Layer";
    }

    @Override
    protected Layer getSubjectByName(String name)
    {
        for (Layer layer : layout.getLayers()) {
            // As this is used to test for duplicate names, don't return this layer.
            // if (layer == subject) {
            // continue;
            // }
            if (layer.getName().equals(name)) {
                return layer;
            }
        }
        return null;
    }

    @Override
    protected void add()
    {
        layout.addLayer(subject);
    }

    @Override
    protected Component createForm()
    {
        super.createForm();

        this.oldName = this.subject.getName();
        notebook = new Notebook();

        stagePropertiesContainer = new PlainContainer();
        viewPropertiesContainer = new PlainContainer();
        stageConstraintPropertiesContainer = new PlainContainer();

        notebook.addPage("Details", form.container);
        notebook.addPage("View Properties", viewPropertiesContainer);
        notebook.addPage("Stage Properties", stagePropertiesContainer);
        notebook.addPage("Stage Constraint", stageConstraintPropertiesContainer);

        initialViewPropertiesForm = createViewProperties();
        initialStagePropertiesForm = createStageProperties();
        initialStageConstraintPropertiesForm = createStageConstraintProperties();

        final ClassNameBox viewClassNameBox = (ClassNameBox) form.getComponent("viewClassName");
        viewClassNameBox.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                if (viewClassNameBox.isValid()) {
                    createViewProperties();
                    createStageProperties();
                    createStageConstraintProperties();
                }
            }
        });

        final ClassNameBox stageClassNameBox = (ClassNameBox) form.getComponent("stageClassName");
        stageClassNameBox.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                if (stageClassNameBox.isValid()) {
                    createStageProperties();
                    createStageConstraintProperties();
                }
            }
        });

        final ClassNameBox stageConstraintClassNameBox = (ClassNameBox) form.getComponent("stageConstraintClassName");
        stageConstraintClassNameBox.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                if (stageConstraintClassNameBox.isValid()) {
                    createStageConstraintProperties();
                }
            }
        });

        return notebook;
    }

    private PropertiesForm<View> createViewProperties()
    {
        viewPropertiesContainer.clear();

        View view = subject.getView();
        PropertiesForm<View> viewForm = new PropertiesForm<View>(view, view.getProperties());
        viewForm.autoUpdate = true;

        viewPropertiesContainer.addChild(viewForm.createForm());
        
        return viewForm;
    }

    private PropertiesForm<Stage> createStageProperties()
    {
        PropertiesForm<Stage> stageForm = null;
        
        stagePropertiesContainer.clear();

        StageView stageView = subject.getStageView();
        boolean hasStage = stageView != null;

        if (hasStage) {

            Stage stage = stageView.getStage();
            if (stage != null) {

                stageForm = new PropertiesForm<Stage>(stage, stage.getProperties());
                stageForm.autoUpdate = true;
                stagePropertiesContainer.addChild(stageForm.createForm());

            }
        }

        form.getComponent("stageClassName").setVisible(hasStage);
        form.getComponent("stageConstraintClassName").setVisible(hasStage);
        notebook.getTab(2).setVisible(hasStage);
        notebook.getTab(3).setVisible(hasStage);
        
        return stageForm;
    }


    private PropertiesForm<StageConstraint> createStageConstraintProperties()
    {
        stageConstraintPropertiesContainer.clear();

        StageView stageView = subject.getStageView();

        if (stageView != null) {

            Stage stage = stageView.getStage();
            if (stage != null) {
                StageConstraint stageConstraint = stage.getStageConstraint();

                PropertiesForm<StageConstraint> stageConstraintForm =
                    new PropertiesForm<StageConstraint>(stageConstraint, stageConstraint.getProperties());
                
                stageConstraintForm.autoUpdate = true;
                stageConstraintPropertiesContainer.addChild(stageConstraintForm.createForm());

                return stageConstraintForm;
            }
        }

        return null;
    }

    @Override
    protected void rename()
    {
        this.layout.renameLayer(this.subject, this.oldName );
        this.oldName = this.subject.getName();
    }

    @Override
    protected void onCancel()
    {
        super.onCancel();

        // Revert the other forms to the values they had when they were FIRST created.
        initialViewPropertiesForm.revert(subject.getView());

        if (initialStageConstraintPropertiesForm != null) {
            initialStageConstraintPropertiesForm.revert( subject.getStage().getStageConstraint());
        }
        
        if ( initialStagePropertiesForm != null) {
            initialStagePropertiesForm.revert(subject.getStage());
        }
        
    }
    
    public void hideDetails()
    {
        // Completely hide the details page and tab. Clear contents so it doesn't take up space.
        notebook.getTab(0).setVisible(false);
        notebook.getPage(0).setVisible(false);
        ((Container) notebook.getPage(0)).clear();
        notebook.forceLayout();
        notebook.invalidate();

        notebook.selectPage(1);
        
        // If there is only one tab visible (the View tab), then hide the tab.
        if (!notebook.getTab(2).isVisible()) {
            notebook.getTab(1).setVisible(false);
        }
    }
}
