package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class NormalDriveOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "left_front");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "right_front");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "left_back");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "right_back");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double forwardPower = -gamepad1.left_stick_y;
            double turnPower = gamepad1.right_stick_x;
            double strafePower = gamepad1.left_stick_x;

            double[] motorPowers = new double[] {
                    forwardPower + turnPower + strafePower, // Left Front
                    forwardPower - turnPower - strafePower, // Right Front
                    forwardPower + turnPower - strafePower, // Left Back
                    forwardPower - turnPower + strafePower // Right Back
            };

            // Normalization
            double maxRawPower =
                    Math.max(Math.max(Math.abs(motorPowers[0]),
                            Math.abs(motorPowers[1])),
                            Math.max(Math.abs(motorPowers[2]),
                                    Math.max(Math.abs(motorPowers[3]), 1)));

            for (int i = 0; i < 4; i++) {
                motorPowers[i] /= maxRawPower;
            }

            leftFront.setPower(motorPowers[0]);
            rightFront.setPower(motorPowers[1]);
            leftBack.setPower(motorPowers[2]);
            rightBack.setPower(motorPowers[3]);

            telemetry.addLine("Running");
            telemetry.update();
        }

    }
}
