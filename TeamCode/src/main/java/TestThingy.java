import org.firstinspires.ftc.teamcode.__compiledautoauto.teamcode.opmodes.auto.Everything____AutoautoProgram;
import org.firstinspires.ftc.teamcode.__compiledautoauto.teamcode.opmodes.auto.bccomp.Eratosthenes____AutoautoProgram;
import org.firstinspires.ftc.teamcode.__compiledautoauto.teamcode.opmodes.auto.bccomp.Funcdef____AutoautoProgram;
import org.firstinspires.ftc.teamcode.__compiledautoauto.teamcode.opmodes.auto.bccomp.Tabletest____AutoautoProgram;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.AutoautoProgram;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.LogFunction;

public class TestThingy {
    public static void main(String[] args) {
        AutoautoProgram p = new Eratosthenes____AutoautoProgram();


        p.getScope().systemSet("log", new LogFunction());

        p.init();
        p.stepInit();

        while(true) p.loop();
    }
}
