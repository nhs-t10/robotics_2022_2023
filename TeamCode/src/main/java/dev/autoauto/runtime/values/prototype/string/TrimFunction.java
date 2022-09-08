package dev.autoauto.runtime.values.prototype.string;

    import dev.autoauto.runtime.values.AutoautoValue;
    import dev.autoauto.runtime.values.AutoautoString;
    import dev.autoauto.runtime.NativeFunction;

public class TrimFunction extends NativeFunction {
        @Override
        public String[] getArgNames() {
            return new String[0];
        }
    
        @Override
        public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
            return new AutoautoString(thisArg.getString().trim());
        }
    }
    