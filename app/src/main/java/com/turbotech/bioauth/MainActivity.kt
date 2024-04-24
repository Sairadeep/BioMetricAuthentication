package com.turbotech.bioauth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.turbotech.bioauth.ui.theme.BioAuthTheme

class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BioAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BioMetricDemo()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun BioMetricDemo() {
    val context = LocalContext.current
    val bioMetric = BioMetric(context)
    val title = remember { mutableStateOf("Unlock with Biometric") }
    val subTitle = remember { mutableStateOf("BioMetric") }
    val negativeButtonText = remember { mutableStateOf("CANCEL") }
    val bioMetricPopUp = context as FragmentActivity
    val displayStatusText = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Initially redirect for setting fingerprint on the device
        if (bioMetric.isBioMetricAvailable() == BioAuthAvailabilityStatus.AVAILABLE_BUT_NOT_SET) {
            val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
            context.startActivity(intent)
        }else{
            Button(onClick = {
                bioMetric.biometricPrompt(
                    title = title.value,
                    subtitle = subTitle.value,
                    negativeButtonText = negativeButtonText.value,
                    fragmentActivity = bioMetricPopUp,
                    onSuccess = {
                        displayStatusText.value =
                            "BioMetric Success: ${BioAuthAvailabilityStatus.READY.statusCode}"
                    },
                    onFailed = {
                        displayStatusText.value = "Verification Failed"
                    },
                    onError = { _: Int, errorMessage ->
                        displayStatusText.value = errorMessage
                    }
                )
            }) {
                Icon(Icons.Default.Lock, contentDescription = "BioMetric")
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = displayStatusText.value, fontSize = 24.sp, textAlign = TextAlign.Center)
        }
    }
}
