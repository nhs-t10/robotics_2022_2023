package org.firstinspires.ftc.teamcode.managers.telemetry.server;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

import java.util.WeakHashMap;

public class CommandHandler {
    public static String handle(String[] args, TelemetryManager dataSource, WeakHashMap<String, StreamHandler> streamRegistry) {
        if(dataSource == null) return HttpStatusCodeReplies.Bad_Gateway("No FeatureManager registered with the server");

        if(args.length == 0) return HttpStatusCodeReplies.Bad_Request("No arguments read!");
        if(args[0] == null) return HttpStatusCodeReplies.Bad_Request("args[0] is null!");
        if(args.length == 1) return HttpStatusCodeReplies.Unauthorized;

        String command = args[0].trim();
        String streamID = args[1];
        StreamHandler stream = streamRegistry.get(streamID);

        if(stream == null) return HttpStatusCodeReplies.Forbidden;

        switch(command) {
            case ControlCodes.DONT_SEND_YOUR_AUTOAUTO_PROGRAMS_ENTIRE_LIFE_STORY_IT_GETS_BORING:
                stream.programJsonSendingFlag = 0;
                return HttpStatusCodeReplies.No_Content;
            case ControlCodes.ACTUALLY_AUNT_AUTOAUTO_I_DO_WANT_TO_HEAR_YOUR_LIFE_STORY:
                stream.programJsonSendingFlag = 1;
                return HttpStatusCodeReplies.No_Content;
            case ControlCodes.COULD_I_GET_LIKE_A_QUICK_SUMMARY_OF_THE_AUTOAUTO_PROGRAMS_LIFE_PLEASE:
                stream.programJsonSendingFlag = 2;
                return HttpStatusCodeReplies.No_Content;
            case ControlCodes.PLEASE_SKIP_TO_THIS_AUTOAUTO_STATE:
                return moveToAutoautoState(args, dataSource);
            case ControlCodes.THERES_THIS_OPMODE_FIELD_COULD_YOU_SET_IT_PLEASE_TY:
                return setOpmodeField(args, dataSource);
            case ControlCodes.SET_PERSEC_STREAMS:
                return setPersec(args, dataSource, stream);
            default:
                return HttpStatusCodeReplies.Bad_Request("Unknown control code " + command);
        }
    }
    private static String setPersec(String[] args, TelemetryManager dataSource, StreamHandler stream) {
        if(args.length < 3) return HttpStatusCodeReplies.Bad_Request("Not enough arguments; must be command,streamid,persec.");
        try {
            stream.sendPerSecond = Float.parseFloat(args[2]);
            return HttpStatusCodeReplies.No_Content;
        } catch(NumberFormatException ignored) {
            return HttpStatusCodeReplies.Bad_Request( args[2] + " is not a valid number");
        }
    }
    private static String setOpmodeField(String[] args, TelemetryManager dataSource) {
        if(dataSource.opmodeFieldMonitor == null) return HttpStatusCodeReplies.Bad_Gateway;
        if(args.length < 4) return HttpStatusCodeReplies.Bad_Request("Must be command,streamid,field,value");
        if(!dataSource.opmodeFieldMonitor.hasKey(args[2])) return HttpStatusCodeReplies.Not_Found;

        dataSource.opmodeFieldMonitor.parseAndSet(args[2], args[3]);

        return HttpStatusCodeReplies.No_Content;
    }
    private static String moveToAutoautoState(String[] args, TelemetryManager dataSource) {
        //requires at least 2 extra args
        if(args.length < 4) return HttpStatusCodeReplies.Bad_Request("Must be command,streamid,statepath,statenum");
        //and the 2nd one has to be a number
        int state = -1;
        try { state = Integer.parseInt(args[3].trim()); } catch (NumberFormatException ignored) {
            return HttpStatusCodeReplies.Bad_Request(args[3] + " is not a valid number");
        }

        String statepathName = args[2].trim();

        FeatureManager.logger.log("debug: statepath " + statepathName + "; state " + state);

        //check that the requested statepath exists
        String lengthOfStatepathVariableName = AutoautoSystemVariableNames.STATE_COUNT_OF_PREFIX + statepathName;
        AutoautoPrimitive lengthOfStatepath = dataSource.autoauto.globalScope.get(lengthOfStatepathVariableName);
        if(lengthOfStatepath == null) return HttpStatusCodeReplies.Not_Found;

        //if it's not null, but also not a numeric value, that's the server's fault
        if(!(lengthOfStatepath instanceof AutoautoNumericValue)) return HttpStatusCodeReplies.Bad_Gateway("Non-numeric variable");

        //ensure that the client isn't asking for a nonexistent state (state >= stateArrayLength)
        if((int)((AutoautoNumericValue)lengthOfStatepath).getFloat() <= state) return HttpStatusCodeReplies.Not_Found("No such state " + state + " in statepath " + statepathName);

        //yay the checks are done and we can do the things!
        dataSource.autoauto.globalScope.systemSet(AutoautoSystemVariableNames.STATEPATH_NAME, new AutoautoString(statepathName));
        dataSource.autoauto.globalScope.systemSet(AutoautoSystemVariableNames.STATE_NUMBER, new AutoautoNumericValue(state));

        return HttpStatusCodeReplies.No_Content;
    }
}
