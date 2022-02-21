package bg.o.sim.gap.ui.objectScan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import bg.o.sim.gap.databinding.FragmentObjectScanBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ObjectScanFragment : Fragment() {
    private lateinit var viewBinding: FragmentObjectScanBinding
    private lateinit var permissionRequest: ActivityResultLauncher<Array<String>>;

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentObjectScanBinding.inflate(inflater, container, false)

        permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results: Map<String, Boolean> ->
            if (results.all { it.value })
                startCamera()
            else Snackbar.make(requireView(), "Camera permission required.", Snackbar.LENGTH_INDEFINITE).setAction("Grant") {
                permissionRequest.launch(REQUIRED_PERMISSIONS)
            }.show()
        }

        // Request camera permissions
        if (allPermissionsGranted())
            startCamera()
        else
            permissionRequest.launch(REQUIRED_PERMISSIONS)

        cameraExecutor = Executors.newSingleThreadExecutor()



        return viewBinding.root
    }

    private fun takePhoto() {}

    private fun captureVideo() {}

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(
            {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .apply {
                        setAnalyzer(ContextCompat.getMainExecutor(requireContext()), ObjectImageAnalyzer(viewBinding.overlay))
                    }

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }.toTypedArray()
    }
}