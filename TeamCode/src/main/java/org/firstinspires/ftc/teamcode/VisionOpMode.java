package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import android.util.Size;

@TeleOp
public class VisionOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        WebcamName camera = hardwareMap.get(WebcamName.class, "camera");
        ElementDetectionProcessor processor = new ElementDetectionProcessor();
        VisionPortal portal =
                new VisionPortal.Builder().setCamera(camera)
                        .addProcessors(processor)
                        .setCameraResolution(new Size(800, 600))
                        .build();
        portal.stopLiveView();
        portal.setProcessorEnabled(processor, true);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Element Position", processor.getPropPosition());
            telemetry.update();
        }
    }
}
