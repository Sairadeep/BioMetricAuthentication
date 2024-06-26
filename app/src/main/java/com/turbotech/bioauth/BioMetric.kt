package com.turbotech.bioauth

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.fragment.app.FragmentActivity

class BioMetric(appContext: Context) {

    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
     val bioMetricManager = BiometricManager.from(appContext.applicationContext)
    private lateinit var biometricsPrompt: androidx.biometric.BiometricPrompt

      fun isBioMetricAvailable(): BioAuthAvailabilityStatus {
//        BIOMETRIC_WEAK or BIOMETRIC_STRONG OR DEVICE_CREDENTIAL
        return when (bioMetricManager.canAuthenticate(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BioAuthAvailabilityStatus.READY
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BioAuthAvailabilityStatus.TEMPORARILY_NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BioAuthAvailabilityStatus.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BioAuthAvailabilityStatus.AVAILABLE_BUT_NOT_SET
            else -> BioAuthAvailabilityStatus.NOT_AVAILABLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun biometricPrompt(
        title: String,
        subtitle: String,
        negativeButtonText: String,
        fragmentActivity: FragmentActivity,
        onSuccess: (result: androidx.biometric.BiometricPrompt.AuthenticationResult) -> Unit,
        onFailed: () -> Unit,
        onError: (errorCode: Int, errorMessage: String) -> Unit
    ) {
        when (isBioMetricAvailable()) {
            BioAuthAvailabilityStatus.NOT_AVAILABLE -> {
                onError(
                    BioAuthAvailabilityStatus.NOT_AVAILABLE.statusCode,
                    "Pin or Fingerprint or Face not available for the device"
                )
                return
            }

            BioAuthAvailabilityStatus.TEMPORARILY_NOT_AVAILABLE -> {
                onError(
                    BioAuthAvailabilityStatus.TEMPORARILY_NOT_AVAILABLE.statusCode,
                    "Not available at this moment"
                )
                return
            }

            BioAuthAvailabilityStatus.AVAILABLE_BUT_NOT_SET -> {
                onError(
                    BioAuthAvailabilityStatus.AVAILABLE_BUT_NOT_SET.statusCode,
                    "Please set a security on the device"
                )
                return
            }

            else -> {}
        }

        biometricsPrompt = androidx.biometric.BiometricPrompt(
            fragmentActivity,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }

            }
        )

        promptInfo = when (bioMetricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setAllowedAuthenticators(BIOMETRIC_WEAK or BIOMETRIC_STRONG)
                    .setNegativeButtonText(negativeButtonText)
                    .build()
            }

            else -> {
                androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setAllowedAuthenticators(DEVICE_CREDENTIAL)
                    .build()
            }
        }

        biometricsPrompt.authenticate(promptInfo)
    }


}