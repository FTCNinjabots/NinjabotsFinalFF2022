package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class Lifter {
    public enum LiftState{
        IDLE,
        LIFTING,
        LIFTED,
        DROPPING,
        INIT
    }

    private final DcMotor motor;
    private LiftState state;
    private TouchSensor touch;
    private final int targetLiftPos = 445; // 430 was old
    private int targetDropPos = -55;
    private final double LiftPow = 0.65;
    private final double DropPow = -0.35;
    private int currentTargetPos;

    public Lifter(HardwareMap hardwareMap){
        this.motor = hardwareMap.get(DcMotor.class, "lifter");
        this.motor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.state = LiftState.INIT;
        this.touch = hardwareMap.get(TouchSensor.class, "touch");

        currentTargetPos = 0;
    }

    public void liftToTop(){
        this.state = LiftState.LIFTING;
        currentTargetPos = targetLiftPos;
        this.motor.setTargetPosition(currentTargetPos);
        this.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.motor.setPower(LiftPow);
    }

    public void dropDown(){
        this.state = LiftState.DROPPING;
        currentTargetPos = targetDropPos;
        this.motor.setTargetPosition(currentTargetPos);
        this.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.motor.setPower(DropPow);
    }

    private boolean isTouched()
    {
        return (this.touch.getValue() == 1.0);
    }

    public void update(){
        if(state == LiftState.LIFTING){
            if(getEncoderCount() >= currentTargetPos){
                state = LiftState.LIFTED;
            }
            /*
            else{
                this.motor.setTargetPosition(currentTargetPos);
                this.motor.setPower(LiftPow);
            }
             */
        }
        else if(state == LiftState.DROPPING){
            if(isTouched())
            {
                state = LiftState.IDLE;
            }
            /*
            else{
                this.motor.setTargetPosition(currentTargetPos);
                motor.setPower(DropPow);
            }
             */
        }
        else if (state == LiftState.IDLE){
            motor.setTargetPosition(currentTargetPos);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setPower(-0.02);
        }
        else if (state == LiftState.INIT){
            motor.setTargetPosition(-500);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setPower(-0.3);
            //stall();
            if(isTouched()){
                targetDropPos = (motor.getCurrentPosition() - 100);
                currentTargetPos = targetDropPos;
                state = LiftState.IDLE;
            }
        }
        else if (state == LiftState.LIFTED){
            if(getEncoderCount() != currentTargetPos){
                this.motor.setTargetPosition(currentTargetPos);
                this.motor.setPower(LiftPow);
            }
        }
    }

    public int getEncoderCount(){return motor.getCurrentPosition();}
    public LiftState getState(){return state;}
}
