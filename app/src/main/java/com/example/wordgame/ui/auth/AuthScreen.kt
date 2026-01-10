package com.example.wordgame.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import com.example.wordgame.ui.components.GlassSurface
import com.example.wordgame.ui.components.LiquidGlassBackground
import com.example.wordgame.ui.theme.LocalAppTheme
import com.example.wordgame.ui.theme.ThemeStyle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthScreen(
    state: AuthUiState,
    onGoogleToken: (String) -> Unit,
    onGoogleError: (String) -> Unit
) {
    val context = LocalContext.current
    val webClientId = remember {
        val resId = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        if (resId != 0) context.getString(resId) else null
    }
    val googleClient = remember(webClientId) {
        webClientId?.takeIf { it.isNotBlank() }?.let { clientId ->
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build()
            GoogleSignIn.getClient(context, options)
        }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            onGoogleError("Google sign-in was canceled.")
            return@rememberLauncherForActivityResult
        }
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val token = try {
            task.getResult(ApiException::class.java)?.idToken
        } catch (_: ApiException) {
            null
        }
        if (token.isNullOrBlank()) {
            onGoogleError("Google sign-in failed. Try again.")
        } else {
            onGoogleToken(token)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidGlassBackground()
        val appTheme = LocalAppTheme.current
        val isGlass = appTheme.style == ThemeStyle.Glass
        val buttonBackground = if (isGlass) {
            Color.White.copy(alpha = 0.85f)
        } else {
            MaterialTheme.colorScheme.primary
        }
        val buttonContent = if (isGlass) {
            Color(0xFF102A43)
        } else {
            MaterialTheme.colorScheme.onPrimary
        }
        GlassSurface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth()
                .widthIn(max = 520.dp),
            cornerRadius = 32.dp
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Sign in with Google to access your dashboard and synced stats.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(
                    onClick = {
                        if (googleClient == null) {
                            onGoogleError("Google sign-in is not configured.")
                        } else {
                            launcher.launch(googleClient.signInIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBackground,
                        contentColor = buttonContent
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = buttonContent
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GoogleLogoIcon(modifier = Modifier.size(18.dp))
                            Text(text = "Continue with Google")
                        }
                    }
                }
                if (!state.error.isNullOrBlank()) {
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    val blue = Color(0xFF4285F4)
    val red = Color(0xFFDB4437)
    val yellow = Color(0xFFF4B400)
    val green = Color(0xFF0F9D58)
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.2f
        val inset = strokeWidth / 2f
        val arcSize = androidx.compose.ui.geometry.Size(
            size.width - strokeWidth,
            size.height - strokeWidth
        )
        val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        drawArc(
            color = red,
            startAngle = -40f,
            sweepAngle = 80f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke
        )
        drawArc(
            color = yellow,
            startAngle = 40f,
            sweepAngle = 80f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke
        )
        drawArc(
            color = green,
            startAngle = 120f,
            sweepAngle = 80f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke
        )
        drawArc(
            color = blue,
            startAngle = 200f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke
        )
        val barStart = androidx.compose.ui.geometry.Offset(
            x = size.width * 0.52f,
            y = size.height * 0.5f
        )
        val barEnd = androidx.compose.ui.geometry.Offset(
            x = size.width * 0.92f,
            y = size.height * 0.5f
        )
        drawLine(
            color = blue,
            start = barStart,
            end = barEnd,
            strokeWidth = strokeWidth * 0.85f,
            cap = StrokeCap.Round
        )
    }
}
