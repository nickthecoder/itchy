package uk.co.nickthecoder.itchy;


/**
 * PoseResources that are generated on-the-fly, not loaded by ResourcesReader.
 */
public class DynamicPoseResource extends PoseResource
{
    public DynamicPoseResource(String name, ImagePose pose)
    {
        super( name);
        this.pose = pose;
    }

    public String toString()
    {
        return "Dynamic" + super.toString();
    }
}
