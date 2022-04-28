package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp
public class RockTeleop extends OpMode {
    DcMotor fl;
    DcMotor fr;
    DcMotor br;
    DcMotor bl;
    DcMotor Carousel;
    DcMotor ClawMotor;
    DcMotor noodle;
    DcMotor intake;
    Servo nateClaw;
    Servo rampLeft;
    Servo rampRight;

    @Override
    public void init() {
        fl = hardwareMap.get(DcMotor.class, "fl");
        fr = hardwareMap.get(DcMotor.class, "fr");
        br = hardwareMap.get(DcMotor.class, "br");
        bl = hardwareMap.get(DcMotor.class, "bl");
        Carousel = hardwareMap.get(DcMotor.class, "Carousel");
        ClawMotor = hardwareMap.get(DcMotor.class, "ClawMotor");
        noodle = hardwareMap.get(DcMotor.class, "noodle");
        intake = hardwareMap.get(DcMotor.class, "intake");

        nateClaw = hardwareMap.get(Servo.class, "nateClaw");
        rampLeft = hardwareMap.get(Servo.class, "rampLeft");
        rampRight = hardwareMap.get(Servo.class, "rampRight");

        ClawMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        ClawMotor.setDirection(DcMotor.Direction.REVERSE);

    }



    @Override
    public void loop() {
        float scale = 0.5f;
        float scale2 = 0.25f;
        //THIS IS OMNI CODE
        {
            float v = scale*gamepad1.left_stick_y;
            float h = scale*gamepad1.left_stick_x;
            float r = scale*gamepad1.right_stick_x;

            float[] vertical = {-v, -v, -v, -v};
            float[] horizontal = {h, -h, h, -h};
            float[] rotational = {r, -r, -r, r};

//            float[] sum = new float[4];
//            for (int i = 0; i < 4; i++) {
//                sum[i] = vertical[i] + horizontal[i] + rotational[i];
//            }
            //This makes sure that no value is greater than 1 by dividing all of them by the maximum
//            float highest = -1000000;
//            for (int i = 0; i < 4; i++) {
//                if (Math.abs(sum[i]) > highest) {
//                    highest = Math.abs(sum[i]);
//                }
//            }
//            if (highest > 1) {
//                for (int i = 0; i < 4; i++) {
//                    sum[i] = sum[i] / highest;
//                }
//            }

//            fl.setPower(scale * sum[0]);
//            fr.setPower(scale * sum[1]);
//            br.setPower(scale * sum[2]);
//            bl.setPower(-scale * sum[3]);

            //THIS IS OMNI CODE Gamepad2
            float v2 = scale2*gamepad2.left_stick_y;
            float h2 = scale2*gamepad2.left_stick_x;
            float r2 = scale2*gamepad2.right_stick_x;

            float[] vertical2 = {-v2, -v2, -v2, -v2};
            float[] horizontal2 = {h2, -h2, h2, -h2};
            float[] rotational2 = {r2, -r2, -r2, r2};

            float[] sum2 = new float[4];
            for (int i = 0; i < 4; i++) {
                sum2[i] =  vertical[i] + vertical2[i] +  horizontal[i] + horizontal2[i] + rotational[i] + rotational2[i];
            }
            //This makes sure that no value is greater than 1 by dividing all of them by the maximum
            float highest = -1000000;
            for (int i = 0; i < 4; i++) if (Math.abs(sum2[i]) > highest) highest = Math.abs(sum2[i]);
            if (highest > 1) for (int i = 0; i < 4; i++) sum2[i] = sum2[i] / highest;


            fl.setPower(sum2[0]);
            fr.setPower(sum2[1]);
            br.setPower(sum2[2]);
            bl.setPower(-sum2[3]);
        }

        if (gamepad1.y) Carousel.setPower(0.75);
        else if (gamepad1.a) Carousel.setPower(-0.75);
        else Carousel.setPower(0);

        //INTAKE CODE
        if (gamepad1.right_trigger > 0 || gamepad2.right_trigger > 0) intake.setPower(1);
        else if (gamepad1.left_trigger > 0 || gamepad2.left_trigger > 0) intake.setPower(-.5);
        else intake.setPower(0);

        if (gamepad1.right_trigger > 0 || gamepad2.right_trigger > 0) noodle.setPower(0.9);
        else if (gamepad1.left_trigger > 0 || gamepad2.left_trigger > 0) noodle.setPower(-.5);
        else noodle.setPower(0);

        if (gamepad1.right_trigger > 0 || gamepad2.right_trigger > 0) {rampLeft.setPosition(0.5); rampRight.setPosition(0);}
        else if (gamepad1.left_trigger > 0 || gamepad2.left_trigger > 0) {rampLeft.setPosition(0.5); rampRight.setPosition(0);}
        else {rampLeft.setPosition(0); rampRight.setPosition(0.35);}


        //CLAW MOVING
        if(gamepad2.x){
            ClawMotor.setTargetPosition(2537);
            //ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            ClawMotor.setPower(0.75);

            //mode running variable THIS IS A TEST DELETE ME IF IM BAD
            if(!ClawMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)){
                ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        } else if(gamepad2.y){
            ClawMotor.setTargetPosition(-1337);
            //ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            ClawMotor.setPower(0.75);
            //mode running variable THIS IS A TEST DELETE ME IF IM BAD
            if(!ClawMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)){
                ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        } else if(gamepad2.b){
            ClawMotor.setTargetPosition(-3024);
            //ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            ClawMotor.setPower(0.75);
            //mode running variable THIS IS A TEST DELETE ME IF IM BAD
            if(!ClawMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)){
                ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        } else if(gamepad2.a){
            ClawMotor.setTargetPosition(-4679);
            //ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            ClawMotor.setPower(0.75);
            //mode running variable THIS IS A TEST DELETE ME IF IM BAD
            if(!ClawMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)){
                ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        } else if(gamepad2.dpad_up){
            ClawMotor.setTargetPosition(ClawMotor.getCurrentPosition()+100);
            //ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            ClawMotor.setPower(0.5);
            //mode running variable THIS IS A TEST DELETE ME IF IM BAD
            if(!ClawMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)){
                ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        } else if(gamepad2.dpad_down){
            ClawMotor.setTargetPosition(ClawMotor.getCurrentPosition()-100);
            //ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            ClawMotor.setPower(0.5);
            //mode running variable THIS IS A TEST DELETE ME IF IM BAD
            if(!ClawMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)){
                ClawMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        } else {
            //ClawMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //we dont need this ((I think))
            ClawMotor.setPower(0);
        }

        if(gamepad2.left_bumper){
            if(nateClaw.getPosition()==0) nateClaw.setPosition(1);
            else nateClaw.setPosition(0);
        }

        telemetry.addData("Claw Motor Position: ", ClawMotor.getCurrentPosition());
        telemetry.addData("Claw Motor Target: ", ClawMotor.getTargetPosition());
        telemetry.addData("Claw Motor Power: ", ClawMotor.getPower());
        telemetry.addData("Claw Motor Mode: ", ClawMotor.getMode());





    }


}
