package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.RobotFunctionLoader;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.encapsulation.AutoautoModule;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

/**
 * I don't know how to make Autoauto generate its own Javadocs :'( so this is a stopgap.
 * See {@link org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope#initBuiltinFunctions(AutoautoModule, RobotFunctionLoader) initBuiltinFunctions()} for the actual implementation.
 *
 * @see org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope#initBuiltinFunctions(AutoautoModule, RobotFunctionLoader)
 * @author https://github.com/chlohal
 */
public abstract class UselessClassForNativeJavadoc {
    /**
     * Add some values to the log. If Autoauto is running on a robot, the logged values will be sent to the driver station ASAP.
     *
     * @param s the value(s) to log
     */
    @AutoautoNativeJavadoc(name = "log")
    public abstract void log(AutoautoValue ...s);


    /**
     * "Delegate" to another file. For every loop spent in the State, one loop will be called on the other file.
     * <h3>Calling multiple {@code delegate()}s in the same state is NOT recommended, for performance reasons.</h3>
     *
     * Make sure that the other file is <em>relative</em> to this one. For example, see the following file structure:
     *
     * <pre><code>
     * 📁
     * ├─ 📁 folder1
     * │  └ helpful-module.autoauto
     * ├─ 📁 folder2
     * │  └ b.autoauto
     * └ a.autoauto
     *</code></pre>
     * If {@code delegate("helpful-module.autoauto")} is called in {@code a.autoauto}, it will cause an error. You need to use {@code delegate("folder1/helpful-module.autoauto")}. <br>
     * Meanwhile, {@code b.autoauto} can NOT have access to {@code helpful-module.autoauto}. Be careful with your directory structures! There's no way to go "up" the folder structure.
     *
     * @param id a file address, relative to the current file
     * @param params zero or more extra parameters to the module. These are put into the {@code module_args} variable of the child file.
     * @return the value that the other file {@link #provide(AutoautoValue) provide}d, or {@code undefined} if it didn't.
     * @see #provide(AutoautoValue) 
     */
    @AutoautoNativeJavadoc(name = "delegate")
    public abstract AutoautoValue delegate(AutoautoString id, AutoautoValue... params);

    /**
     *
     * @param value The value to provide to its parent. If this file isn't being used as a module, {@code provide} does nothing.
     * @see #delegate(AutoautoString, AutoautoValue...)
     */
    @AutoautoNativeJavadoc(name = "provide")
    public abstract void provide(AutoautoValue value);

    /**
     * Clips a value, such that it won't be **Greater** than **Maximum** or **Lesser** than **Minimum**
     * @param minimum smallest value that the return can be
     * @param maximum largest value that the return can be.
     */
    @AutoautoNativeJavadoc(name = "provide")
    public abstract void clip(AutoautoNumericValue minimum, AutoautoNumericValue maximum);


    @interface AutoautoNativeJavadoc {
        String name();
    }
}
