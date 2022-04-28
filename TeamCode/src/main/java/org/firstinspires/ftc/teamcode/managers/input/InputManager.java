package org.firstinspires.ftc.teamcode.managers.input;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.buttonhandles.*;
import org.firstinspires.ftc.teamcode.managers.input.nodes.InputManagerInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;

import java.util.Collection;
import java.util.HashMap;

/**
 * Handle input (button combos, keybinds, etc.) for gamepads.
 */
public class InputManager extends FeatureManager {
    public Gamepad gamepad;
    public Gamepad gamepad2;

    private static Gamepad gp2;

    public static void vibrategp2() {
        if(gp2 != null) {
            gp2.rumble(100);
        }
    }

    public InputOverlapResolutionMethod overlapResolutionMethod = InputOverlapResolutionMethod.BOTH_CHILDREN_CAN_SPEAK;

    private final HashMap<String, InputManagerInputNode> nodes;
    private final HashMap<String, Float> inputVars;

    /**
     * Make an InputManager with 2 gamepads
     * @param _gamepad the first gamepad
     * @param _gamepad2 the second gamepad
     */
    public InputManager(Gamepad _gamepad, Gamepad _gamepad2) {
        this.gamepad = _gamepad;
        this.gamepad2 = _gamepad2;
        gp2 = _gamepad2;
        nodes = new HashMap<>();
        inputVars = new HashMap<>();
    }

    /**
     * Register a new InputNode. The Manager takes care of updating it, ensuring overlaps resolve correctly, etc.
     * <h3>Usage Example</h3>
     * <pre><code>
     * input.registerInput("face button A", new ButtonNode("a"));
     * </code></pre>
     * @see #setOverlapResolutionMethod(InputOverlapResolutionMethod)
     * @param key The name to associate with the node
     * @param registerNodes Nodes to register. If you give multiple nodes, it will automatically make it into an array-ish.
     */
    public void registerInput(String key, InputManagerInputNode... registerNodes) {
        InputManagerInputNode node = null;
        if(registerNodes.length == 0) throw new IllegalArgumentException("Must have more than 0 args");
        else if(registerNodes.length == 1) node = registerNodes[0];
        else node = new MultiInputNode(registerNodes);

        node.setName(key);

        node.init(this);
        nodes.put(key.toLowerCase(), node);
        rebuildOverlaps();
        (new InputUpdateThread(node)).start();
    }

    public void setInputVariable(String name, float f) {
        inputVars.put(name, f);
    }

    public float getInputVariable(String name) {
        Float f = inputVars.get(name);
        if(f == null) return 0f;
        else return f;
    }

    /**
     * Get a given input node. Don't use this unless you know what you're doing!
     * @see #registerInput(String, InputManagerInputNode...)
     * @param key The name of the node, as registered before.
     * @return The pre-registered node, or `null` if there's no such node.
     */
    public InputManagerInputNode getInputNode(String key) {
        return nodes.get(key.toLowerCase());
    }

    /**
     * Get a ButtonHandle for a given button. The button handle can be reused to prevent lag!
     * If you're writing an {@link InputManagerInputNode InputNode}, please use this & save the result in a field!
     * Examples of valid button formats:
     * <ul>
     *     <li>gamepad 1 dpad up</li>
     *     <li>dpad up</li>
     *     <li>gamepad 2 X</li>
     *     <li>x</li>
     *     <li>y</li>
     *     <li>RightStickButton</li>
     *     <li>left_stick_y</li>
     *     <li>RIGHT_STICK_X</li>
     * </ul>
     * @see ButtonHandle#get()
     * @param key The name of the button, in any format. It's not picky.
     * @return A ButtonHandle that's been "bound" to that button.
     */
    public ButtonHandle getButtonHandle(String key) {
        String normalizedKey = key.toLowerCase().replaceAll("[^a-z0-9]", "");

        //remove 'gamepad1'
        if(normalizedKey.startsWith("gamepad1")) normalizedKey = normalizedKey.replace("gamepad1", "");

        switch(normalizedKey) {
            case "touchpadfinger1": return new TouchpadFinger1ButtonHandle(gamepad);
            case "touchpadfinger2": return new TouchpadFinger2ButtonHandle(gamepad);
            case "touchpadfinger1x": return new TouchpadFinger1XButtonHandle(gamepad);
            case "touchpadfinger1y": return new TouchpadFinger1YButtonHandle(gamepad);
            case "touchpadfinger2x": return new TouchpadFinger2XButtonHandle(gamepad);
            case "touchpadfinger2y": return new TouchpadFinger2YButtonHandle(gamepad);
            case "dpadup": return new DpadUpButtonHandle(gamepad);
            case "dpaddown": return new DpadDownButtonHandle(gamepad);
            case "dpadleft": return new DpadLeftButtonHandle(gamepad);
            case "dpadright": return new DpadRightButtonHandle(gamepad);
            case "a": return new AButtonHandle(gamepad);
            case "b": return new BButtonHandle(gamepad);
            case "x": return new XButtonHandle(gamepad);
            case "y": return new YButtonHandle(gamepad);
            case "guide":
            case "select": return new GuideButtonHandle(gamepad);
            case "start": return new StartButtonHandle(gamepad);
            case "back": return new BackButtonHandle(gamepad);
            case "leftbumper": return new LeftBumperButtonHandle(gamepad);
            case "rightbumper": return new RightBumperButtonHandle(gamepad);
            case "leftstickbutton": return new LeftStickButtonButtonHandle(gamepad);
            case "rightstickbutton": return new RightStickButtonButtonHandle(gamepad);
            case "circle": return new CircleButtonHandle(gamepad);
            case "cross": return new CrossButtonHandle(gamepad);
            case "triangle": return new TriangleButtonHandle(gamepad);
            case "square": return new SquareButtonHandle(gamepad);
            case "share": return new ShareButtonHandle(gamepad);
            case "options": return new OptionsButtonHandle(gamepad);
            case "touchpad": return new TouchpadButtonHandle(gamepad);
            case "ps": return new PsButtonHandle(gamepad);
            case "leftstickx": return new LeftStickXButtonHandle(gamepad);
            case "leftsticky": return new LeftStickYButtonHandle(gamepad);
            case "rightstickx": return new RightStickXButtonHandle(gamepad);
            case "rightsticky": return new RightStickYButtonHandle(gamepad);
            case "lefttrigger": return new LeftTriggerButtonHandle(gamepad);
            case "righttrigger": return new RightTriggerButtonHandle(gamepad);
            case "gamepad2dpadup": return new DpadUpButtonHandle(gamepad2);
            case "gamepad2dpaddown": return new DpadDownButtonHandle(gamepad2);
            case "gamepad2dpadleft": return new DpadLeftButtonHandle(gamepad2);
            case "gamepad2dpadright": return new DpadRightButtonHandle(gamepad2);
            case "gamepad2a": return new AButtonHandle(gamepad2);
            case "gamepad2b": return new BButtonHandle(gamepad2);
            case "gamepad2x": return new XButtonHandle(gamepad2);
            case "gamepad2y": return new YButtonHandle(gamepad2);
            case "gamepad2guide":
            case "gamepad2select": return new GuideButtonHandle(gamepad2);
            case "gamepad2start": return new StartButtonHandle(gamepad2);
            case "gamepad2back": return new BackButtonHandle(gamepad2);
            case "gamepad2leftbumper": return new LeftBumperButtonHandle(gamepad2);
            case "gamepad2rightbumper": return new RightBumperButtonHandle(gamepad2);
            case "gamepad2leftstickbutton": return new LeftStickButtonButtonHandle(gamepad2);
            case "gamepad2rightstickbutton": return new RightStickButtonButtonHandle(gamepad2);
            case "gamepad2circle": return new CircleButtonHandle(gamepad2);
            case "gamepad2cross": return new CrossButtonHandle(gamepad2);
            case "gamepad2triangle": return new TriangleButtonHandle(gamepad2);
            case "gamepad2square": return new SquareButtonHandle(gamepad2);
            case "gamepad2share": return new ShareButtonHandle(gamepad2);
            case "gamepad2options": return new OptionsButtonHandle(gamepad2);
            case "gamepad2touchpad": return new TouchpadButtonHandle(gamepad2);
            case "gamepad2ps": return new PsButtonHandle(gamepad2);
            case "gamepad2leftstickx": return new LeftStickXButtonHandle(gamepad2);
            case "gamepad2leftsticky": return new LeftStickYButtonHandle(gamepad2);
            case "gamepad2rightstickx": return new RightStickXButtonHandle(gamepad2);
            case "gamepad2rightsticky": return new RightStickYButtonHandle(gamepad2);
            case "gamepad2lefttrigger": return new LeftTriggerButtonHandle(gamepad2);
            case "gamepad2righttrigger": return new RightTriggerButtonHandle(gamepad2);
            case "gamepad2touchpadfinger1": return new TouchpadFinger1ButtonHandle(gamepad2);
            case "gamepad2touchpadfinger2": return new TouchpadFinger2ButtonHandle(gamepad2);
            case "gamepad2touchpadfinger1x": return new TouchpadFinger1XButtonHandle(gamepad2);
            case "gamepad2touchpadfinger1y": return new TouchpadFinger1YButtonHandle(gamepad2);
            case "gamepad2touchpadfinger2x": return new TouchpadFinger2XButtonHandle(gamepad2);
            case "gamepad2touchpadfinger2y": return new TouchpadFinger2YButtonHandle(gamepad2);
        }
        throw new IllegalArgumentException("Bad key " + key);
    }

    /**
     * Update the manager. Not used right now, but kept for compatibility.
     */
    public void update() {
    }

    /**
     * Grab the result of the given node, as a float array.
     * <h3>If the given node is an number, it returns a 1-element array holding that number</h3>
     * <br>
     * <h3>If the given node is a boolean, it returns [1] if it's true; otherwise, [0].</h3>
     *
     * @param key the registered node to check
     * @return the result (converted to a float array).
     */
    public float[] getFloatArrayOfInput(String key) {
        if(!nodes.containsKey(key.toLowerCase())) throw new IllegalArgumentException("No control `" + key.toLowerCase() + "` registered");
        return nodes.get(key.toLowerCase()).getOverlappedResult().getFloatArray();
    }

    /**
     * Grab the result of the given node, as a boolean.
     * <h3>If the given node is an array-like, it returns true if the first element IS NOT 0</h3>
     * <br>
     * <h3>If the given node is a number, it returns true if the number IS NOT 0</h3>
     *
     * @param key the registered node to check
     * @return the result (converted to a boolean).
     */
    public boolean getBool(String key) {
        if(!nodes.containsKey(key.toLowerCase())) throw new IllegalArgumentException("No control `" + key.toLowerCase() + "` registered");
        return nodes.get(key.toLowerCase()).getOverlappedResult().getBool();
    }

    /**
     * Grab the result of the given node, as a float.
     * <h3>If the given node is an array-like, it returns the first element</h3>
     * <br>
     * <h3>If the given node is a boolean, it returns 1 for true; 0 for false</h3>
     *
     * @param key the registered node to check
     * @return the result (converted to a float).
     */
    public float getFloat(String key) {
        if(!nodes.containsKey(key.toLowerCase())) throw new IllegalArgumentException("No control `" + key.toLowerCase() + "` registered");
        return nodes.get(key.toLowerCase()).getOverlappedResult().getFloat();
    }

    /**
     * Choose whether less-complicated nodes or more-complicated nodes are prioritised.
     * @see #registerInput(String, InputManagerInputNode...)
     * @param newMethod the new method to use.
     */
    public void setOverlapResolutionMethod(InputOverlapResolutionMethod newMethod) {
        this.overlapResolutionMethod = newMethod;
        rebuildOverlaps();
    }

    private void rebuildOverlaps() {
        InputManagerInputNode[] allRootNodes = nodes.values().toArray(new InputManagerInputNode[0]);
        for(InputManagerInputNode node : allRootNodes) {
            node.updateOverlaps(overlapResolutionMethod, allRootNodes);
        }
    }

    /**
     * Get all nodes that were registered
     * @see #registerInput(String, InputManagerInputNode...)
     * @return all nodes registered with this manager
     */
    public Collection<InputManagerInputNode> getNodes() {
        return nodes.values();
    }
}

