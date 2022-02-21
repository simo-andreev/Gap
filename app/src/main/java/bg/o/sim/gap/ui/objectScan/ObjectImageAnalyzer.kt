package bg.o.sim.gap.ui.objectScan

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import bg.o.sim.gap.ui.view.OverlayView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.lang.ref.WeakReference

class ObjectImageAnalyzer(overlay: OverlayView) : ImageAnalysis.Analyzer {

    private val overlayRef = WeakReference(overlay)

    private val objectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .build()
    )


    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val result = objectDetector.process(image)
                .addOnCompleteListener {
                    imageProxy.close()
                }
                .addOnSuccessListener { detectedObjects ->
                    overlayRef.get()?.setRectangles(detectedObjects.mapNotNull { it?.boundingBox })
                }
        }
    }

}