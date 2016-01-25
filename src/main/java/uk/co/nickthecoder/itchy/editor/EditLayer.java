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
        for (Layer layer : layout.layers) {
            // As this is used to test for duplicate names, don't return this layer.
            //if (layer == subject) {
            //    continue;
            //}
            if (layer.name.equals(name)) {
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

    private Container stagePropertiesContainer;
    private Container viewPropertiesContainer;
    private Container stageConstraintPropertiesContainer;
    private Notebook notebook;

    @Override
    protected Component createForm()
    {
        super.createForm();

        notebook = new Notebook();

        stagePropertiesContainer = new PlainContainer();
        viewPropertiesContainer = new PlainContainer();
        stageConstraintPropertiesContainer = new PlainContainer();

        notebook.addPage("Details", form.container);
        notebook.addPage("View Properties", viewPropertiesContainer);
        notebook.addPage("Stage Properties", stagePropertiesContainer);
        notebook.addPage("Stage Constraint", stageConstraintPropertiesContainer);

        createViewProperties();
        createStageProperties();
        createStageConstraintProperties();

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

    private PropertiesForm<View> viewPropertiesForm;

    private void createViewProperties()
    {
        viewPropertiesContainer.clear();

        View view = subject.getView();
        viewPropertiesForm = new PropertiesForm<View>(view, view.getProperties());

        viewPropertiesContainer.addChild(viewPropertiesForm.createForm());
    }

    private PropertiesForm<Stage> stagePropertiesForm;

    private void createStageProperties()
    {
        stagePropertiesForm = null;

        stagePropertiesContainer.clear();

        StageView stageView = subject.getStageView();
        boolean hasStage = stageView != null;

        if (hasStage) {

            Stage stage = stageView.getStage();
            if (stage != null) {

                stagePropertiesForm = new PropertiesForm<Stage>(stage, stage.getProperties());
                stagePropertiesContainer.addChild(stagePropertiesForm.createForm());

            }
        }

        form.getComponent("stageClassName").setVisible(hasStage);
        form.getComponent("stageConstraintClassName").setVisible(hasStage);
        notebook.getTab(2).setVisible(hasStage);
        notebook.getTab(3).setVisible(hasStage);

    }

    private PropertiesForm<StageConstraint> stageConstraintPropertiesForm;

    private void createStageConstraintProperties()
    {
        stageConstraintPropertiesForm = null;

        stageConstraintPropertiesContainer.clear();

        StageView stageView = subject.getStageView();

        if (stageView != null) {

            Stage stage = stageView.getStage();
            if (stage != null) {
                StageConstraint stageConstraint = stage.getStageConstraint();

                stageConstraintPropertiesForm = new PropertiesForm<StageConstraint>(stageConstraint,
                    stageConstraint.getProperties());
                stageConstraintPropertiesContainer.addChild(stageConstraintPropertiesForm.createForm());

            }
        }

    }

    @Override
    protected void rename()
    {
        // Do nothing because, unlike other resources, layers are not in a map keyed on their name.
    }

    @Override
    protected void onCancel()
    {
        stageConstraintPropertiesForm.revert();
        stagePropertiesForm.revert();
        viewPropertiesForm.revert();
        super.onCancel();
    }
}
