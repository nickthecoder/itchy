package uk.co.nickthecoder.itchy.collision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.util.Filter;

/**
 * Wraps around another CollisionStrategy, returning the results ordered with the nearest first.
 * <p>
 * There is a subtle weirdness with regard to the maxResults parameter of
 * {@link #collisions(Actor, String[], int, Filter)}. If we want the 2 nearest collisions, and there are actually 10
 * Actors touching, then we need to find all 10, sort them and then pick the top 2. So maxResults is set to 2, but a
 * <b>different</b> maximum is sent to the unordered CollisionStrategy. The maximum number of unordered results is
 * specified in the constructor {@link #NearestCollisionStrategy(int)}.
 * <p>
 * Note. If your game has very frequent collisions, then using this CollisionStrategy will be inefficient for
 * {@link AbstractRole#collided(String...)}, because multiple collisions will be found for no reason. This isn't a
 * problem for most games.
 */
public class NearestCollisionStrategy implements CollisionStrategy
{
    /**
     * The CollisionStrategy which returns unordered results
     */
    private CollisionStrategy unorderedCollisionStrategy;

    /**
     * The maximum unorderedResults.
     */
    private int maximumUnordered;

    public NearestCollisionStrategy(int minimumUnordered)
    {
        this.maximumUnordered = minimumUnordered;
    }

    @Override
    public void update()
    {
        unorderedCollisionStrategy.update();
    }

    @Override
    public void remove()
    {
        unorderedCollisionStrategy.remove();
    }

    @Override
    public List<Role> collisions(final Actor actor, String[] tags, int maxResults, Filter<Role> filter)
    {
        int max = maximumUnordered > maxResults ? maximumUnordered : maxResults;

        List<Role> result = unorderedCollisionStrategy.collisions(actor, tags, max, filter);
        if (result.size() == 0) {
            return result;
        }

        Comparator<Role> comparator = new Comparator<Role>()
        {
            @Override
            public int compare(Role o1, Role o2)
            {
                return Double.compare(distance(o2.getActor()), distance(o1.getActor()));
            }

            private double distance(Actor other)
            {
                return other.getPosition().distance(actor.getPosition());
            }
        };

        Collections.sort(result, comparator);

        if (maxResults < result.size()) {
            List<Role> subset = new ArrayList<Role>(maxResults);
            for (int i = 0; i < maxResults; i++) {
                subset.add(result.get(i));
            }
            return subset;
        } else {
            return result;
        }
    }

}
