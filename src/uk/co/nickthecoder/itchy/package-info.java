/**
 * Welcome to Itchy API documentation.
 * <p>
 * {@link uk.co.nickthecoder.itchy.Game}, as its name implies, is the heart of every game.
 * But that's not where all the good stuff hangs out, so don't check it out just yet.
 * A game is made up of a set of
 * {@link uk.co.nickthecoder.itchy.Stage}s, and a stage is populated by
 * {@link uk.co.nickthecoder.itchy.Actor}s.
 * An actor is one of your game's characters. If we were to write pac-man, then each ghost would be an actor,
 * so would Pac-Man, as well as each pill.
 * Their behaviour doesn't live within the Actor class though, its in {@link uk.co.nickthecoder.itchy.Role}.
 * Most of your game's code will be a type of role, in fact almost all of you code will be a subclass of
 * {@link uk.co.nickthecoder.itchy.AbstractRole}.
 * </p>
 * <p>
 * As your game becomes more complex, you will also create subclasses of
 * {@link uk.co.nickthecoder.itchy.Director} and
 * {@link uk.co.nickthecoder.itchy.SceneDirector}.
 * </p>
 * <p>
 * SceneDirector, is in charge of just one scene. For example, in pac-man the scene director will count the pills
 * and when there are none left, it knows that level is complete, and will start the next one.
 * </p>
 * <p>
 * Director looks at the big picture, but for most games it does surprisingly little.
 * </p>
 * <p>
 * The other classes you should know about are
 * {@link uk.co.nickthecoder.itchy.Costume}, {@link uk.co.nickthecoder.itchy.Appearance} and
 * {@link uk.co.nickthecoder.itchy.StageView}.
 */
package uk.co.nickthecoder.itchy;

