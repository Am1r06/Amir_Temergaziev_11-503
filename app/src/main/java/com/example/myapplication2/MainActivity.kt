package com.example.myapplication2

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
fun generateRandomPassword(): String {
    val letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val digits = "0123456789"
    val allChars = letters + digits

    var result = ""
    result += letters.random()
    result += digits.random()

    repeat(6) {
        result += allChars.random()
    }

    return result.toList().shuffled().joinToString("")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val generatedPassword = remember { generateRandomPassword() }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToResult = { enteredEmail, enteredPassword ->
                    navController.navigate("result/$enteredPassword")
                }
            )
        }

        composable("result/{enteredPassword}") { backStackEntry ->
            val enteredPassword = backStackEntry.arguments?.getString("enteredPassword") ?: ""
            ResultScreen(
                enteredPassword = enteredPassword,
                generatedPassword = generatedPassword,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun LoginScreen(onNavigateToResult: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val attemptLogin = {
        var isValid = true

        if (email.isBlank()) {
            emailError = "Поле не может быть пустым"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Неверный формат email адреса"
            isValid = false
        }

        val hasLatin = password.any { it in 'a'..'z' || it in 'A'..'Z' }
        val hasDigit = password.any { it.isDigit() }

        if (password.isBlank()) {
            passwordError = "Поле не может быть пустым"
            isValid = false
        } else if (password.length < 8 || !hasLatin || !hasDigit) {
            passwordError = "Пароль должен быть от 8 символов, содержать латиницу и цифры"
            isValid = false
        }

        if (isValid) {
            keyboardController?.hide()
            onNavigateToResult(email, password)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = {
                if (emailError != null) Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),

            keyboardActions = KeyboardActions(
                onDone = { attemptLogin() }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = attemptLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти")
        }
    }
}

@Composable
fun ResultScreen(
    enteredPassword: String,
    generatedPassword: String,
    onNavigateBack: () -> Unit
) {
    val isPasswordMatch = (enteredPassword == generatedPassword)

    val successImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSXvYKjcdoXpWIm1n3V_93pP9wP03bYchqqRqup5UbVMw&s=10"
    val failImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT3lTYrjl_nEblssj_kFt-IQ89JbcIllCdoe3DIrJQVlg&s=10"

    var isPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            model = if (isPasswordMatch) successImageUrl else failImageUrl,
            contentDescription = "Result Image",
            modifier = Modifier
                .size(250.dp)
                .padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = generatedPassword,
                onValueChange = {},
                readOnly = true,
                label = { Text("Сгенерированный пароль") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Text(if (isPasswordVisible) "Скрыть" else "Показать")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Password", generatedPassword)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Пароль скопирован", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Скопировать пароль")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад на первый экран")
        }
    }
}