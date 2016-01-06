package uk.co.nickthecoder.itchy;


/**
 * PoseResources that are generated on-the-fly, not loaded by ResourcesReader.
 */
public class DynamicPoseResource extends PoseResource
{
    public DynamicPoseResource(Resources resources, String name, ImagePose pose)
    {
        super(resources, name);
        this.pose = pose;
    }

}
