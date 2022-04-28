package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

    import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
    import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
    import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
    import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
    import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
    
    public class GetBytesFunction extends NativeFunction {
        @Override
        public String[] getArgNames() {
            return new String[0];
        }
    
        @Override
        public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
            byte[] b = thisArg.getString().getBytes();
            AutoautoNumericValue[] n = new AutoautoNumericValue[b.length];

            for(int i = 0; i < b.length; i++) n[i] = new AutoautoNumericValue(b[i]);

            return new AutoautoTable(n);
        }
    }
    