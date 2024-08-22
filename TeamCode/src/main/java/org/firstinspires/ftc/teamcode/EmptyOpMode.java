package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class EmptyOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Add initialization steps here

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart(); // Wait for start of opmode

        while (opModeIsActive()) {
            // OpMode loop, runs until program is stopped

            telemetry.addLine("Running");
            telemetry.update();
        }
    }
}
