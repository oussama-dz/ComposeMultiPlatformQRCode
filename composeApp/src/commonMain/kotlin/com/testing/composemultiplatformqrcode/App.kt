package com.testing.composemultiplatformqrcode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import composemultiplatformqrcode.composeapp.generated.resources.Res
import composemultiplatformqrcode.composeapp.generated.resources.compose_multiplatform
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import io.github.alexzhirkevich.qrose.QrData
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrCodeShape
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogo
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.hexagon
import io.github.alexzhirkevich.qrose.options.horizontalLines
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.options.square
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.launch
import org.ncgroup.kscan.BarcodeFormats
import org.ncgroup.kscan.BarcodeResult
import org.ncgroup.kscan.ScannerView

@Composable
@Preview
fun App() {
    MaterialTheme {

        val factory = rememberPermissionsControllerFactory()
        val controller = remember(factory) { factory.createPermissionsController() }
        val scope = rememberCoroutineScope()
        BindEffect(controller)

        var showScanner by remember { mutableStateOf(false) }

        var qrCodeData by remember { mutableStateOf("") }
        var scannedData by remember { mutableStateOf("") }

        val painter = rememberQrCodePainter(
            data = qrCodeData,
            shapes = QrShapes(
                code = QrCodeShape.hexagon(),
                darkPixel = QrPixelShape.circle(),
                lightPixel = QrPixelShape.circle(),
                ball = QrBallShape.circle(),
                frame = QrFrameShape.circle(),
            ),
            colors = QrColors(
                dark = QrBrush.solid(Color.Red),
                light = QrBrush.solid(Color.Transparent),
                ball = QrBrush.solid(Color.Black),
                frame = QrBrush.solid(Color.Black)
            ),
            logo = QrLogo(
                painter = painterResource(Res.drawable.compose_multiplatform),
                size = .1f,
                padding = QrLogoPadding.Natural(0.1f)
            ),
            errorCorrectionLevel = QrErrorCorrectionLevel.High,
            fourEyed = true
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                TextField(
                    value = qrCodeData,
                    onValueChange = { qrCodeData = it }
                )
                Image(
                    painter = painter,
                    contentDescription = "QR code referring to the example.com website",
                    modifier = Modifier
                        .size(200.dp)
                )
                Text("Qr Code Date: $qrCodeData")

                Button(
                    onClick = {
                        //check the camera permission
                        scope.launch {
                            runCatching {
                                controller.providePermission(Permission.CAMERA)
                            }.onFailure {
                                //handle the exception cases
                            }

                            //we have the camera permission and we can continue
                            showScanner = true
                        }
                    }
                ) {
                    Text("Scan QR Code")
                }

                Text("Scanned Data: $scannedData")
            }

            if (showScanner) {
                ScannerView(
                    codeTypes = listOf(
                        BarcodeFormats.FORMAT_QR_CODE,
                    )
                ) { result ->
                    when (result) {
                        is BarcodeResult.OnSuccess -> {
                            scannedData = result.barcode.data
                            showScanner = false
                        }
                        is BarcodeResult.OnFailed -> {
                            result.exception.printStackTrace()
                            showScanner = false
                        }
                        BarcodeResult.OnCanceled -> {
                            showScanner = false
                        }
                    }
                }
            }
        }

    }
}