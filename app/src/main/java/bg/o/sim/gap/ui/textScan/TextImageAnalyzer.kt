package bg.o.sim.gap.ui.textScan

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import bg.o.sim.gap.ui.view.OverlayView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.lang.ref.WeakReference

class TextImageAnalyzer(overlay: OverlayView) : ImageAnalysis.Analyzer {

    private val overlayRef = WeakReference(overlay)

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to an ML Kit Vision API
            // ...

            recognizer.process(image)
                .addOnSuccessListener { result ->
                    val resultText = result.text
                    for (block in result.textBlocks) {
                        val blockText = block.text
                        val blockCornerPoints = block.cornerPoints
                        val blockFrame = block.boundingBox
                        for (line in block.lines) {
                            val lineText = line.text
                            val lineCornerPoints = line.cornerPoints
                            val lineFrame = line.boundingBox
                            for (element in line.elements) {
                                val elementText = element.text
                                val elementCornerPoints = element.cornerPoints
                                val elementFrame = element.boundingBox
                            }
                        }
                    }

                    overlayRef.get()?.setRectangles(result.textBlocks.mapNotNull { textBlock -> textBlock.boundingBox })
                }
                .addOnCompleteListener { imageProxy.close() }
        }
    }
}