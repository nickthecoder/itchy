package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;

public class CompoundAnimation extends AbstractAnimation
{
    public List<Animation> children = new ArrayList<Animation>();

    public boolean sequence;

    public SequenceOrParallel sop;

    public int loops;

    private int loopsRemaining;

    public CompoundAnimation( boolean sequence )
    {
        this.sequence = sequence;
        this.loops = 1;
    }

    @Override
    public String getName()
    {
        return this.sequence ? "Sequence" : "Parallel";
    }

    public void addAnimation( Animation child )
    {
        this.children.add(child);
    }

    public void removeAnimation( Animation child )
    {
        this.children.remove(child);
    }

    public void moveAnimationUp( Animation child )
    {
        int index = this.children.indexOf(child);
        assert (index > 0);
        Animation other = this.children.get(index - 1);
        this.children.set(index, other);
        this.children.set(index - 1, child);
    }

    @Override
    public void start( Actor actor )
    {
        this.loopsRemaining = this.loops == 0 ? 1 : this.loops;

        if (this.sequence) {
            this.sop = new Sequence(this, actor);
        } else {
            this.sop = new Parallel(this, actor);
        }
        this.sop.start(actor);

    }

    @Override
    public void tick( Actor actor )
    {
        this.sop.tick(actor);

        if (this.sop.isFinished()) {
            if (this.loops != 0) {
                this.loopsRemaining--;
            }
            if (this.loopsRemaining > 0) {
                this.sop.start(actor);
            }
        }
        super.tick(actor);
    }

    @Override
    public Animation clone() throws CloneNotSupportedException
    {
        assert (this.sop == null); // Can only clone an animation which hasn't started

        CompoundAnimation result = (CompoundAnimation) super.clone();

        result.children = new ArrayList<Animation>();
        for (Animation child : this.children) {
            result.children.add(child.copy());
        }

        return result;
    }

    @Override
    public boolean isFinished()
    {
        return this.sop.isFinished();
    }

}
