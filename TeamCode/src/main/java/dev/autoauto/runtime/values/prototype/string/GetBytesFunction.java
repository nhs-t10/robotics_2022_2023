package dev.autoauto.runtime.values.prototype.string;

    import dev.autoauto.runtime.values.AutoautoNumericValue;
    import dev.autoauto.runtime.values.AutoautoValue;
    import dev.autoauto.runtime.values.AutoautoTable;
    import dev.autoauto.runtime.NativeFunction;

public class GetBytesFunction extends NativeFunction {
        @Override
        public String[] getArgNames() {
            return new String[0];
        }
    
        @Override
        public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
            byte[] b = thisArg.getString().getBytes();
            AutoautoNumericValue[] n = new AutoautoNumericValue[b.length];

            for(int i = 0; i < b.length; i++) n[i] = new AutoautoNumericValue(b[i]);

            return new AutoautoTable(n);
        }
    }
    