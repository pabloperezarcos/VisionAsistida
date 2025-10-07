package com.example.visionasistida

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.compose.ComposeNavigator
import org.junit.Rule
import org.junit.Test

class LoginScreenUiTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun setLoginContent() {
        val context = rule.activity
        val navController = TestNavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
        rule.setContent {
            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("register") { Text("Pantalla registro") }
                composable("forgot_password") { Text("Pantalla recuperar") }
                composable("home") { Text("Pantalla home") }
            }
        }
    }

    @Test
    fun invalidInput_showsErrorMessages() {
        setLoginContent()

        // Clic sin ingresar datos
        rule.onNodeWithText("Iniciar sesión").performClick()

        // Debe mostrar ambos mensajes de error
        rule.onNodeWithText("Ingresa un correo válido").assertExists()
        rule.onNodeWithText("La contraseña no puede estar vacía").assertExists()
    }

    @Test
    fun navigate_toRegister_whenClickCrearCuenta() {
        setLoginContent()

        rule.onNodeWithText("Crear cuenta").performClick()

        // Verificamos que cargó la pantalla Register
        rule.onNodeWithText("Pantalla registro").assertExists()
    }

    @Test
    fun navigate_toForgot_whenClickRecuperar() {
        setLoginContent()

        rule.onNodeWithText("Recuperar contraseña").performClick()

        // Verificamos que cargó la pantalla Forgot
        rule.onNodeWithText("Pantalla recuperar").assertExists()
    }

    @Test
    fun toggle_password_visibility_changes_iconDescription() {
        setLoginContent()

        // Icono de "Mostrar contraseña" debe existir al inicio
        rule.onNodeWithContentDescription("Mostrar contraseña").assertExists().performClick()

        // Tras el click, debe cambiar a "Ocultar contraseña"
        rule.onNodeWithContentDescription("Ocultar contraseña").assertExists()
    }
}
