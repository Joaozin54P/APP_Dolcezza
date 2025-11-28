package com.example.dolcezza

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.dolcezza.ui.theme.BrownDolcezza
import com.example.dolcezza.ui.theme.MarrowAcizentado
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
    val pinkDolcezza = Color(0xffffaaaa)
    val context = LocalContext.current
    val scroll = rememberScrollState()

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        pinkDolcezza.copy(alpha = 0.70f),
                        MarrowAcizentado
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .imePadding()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(30.dp))
                    .background(Color.White, RoundedCornerShape(30.dp))
                    .padding(24.dp)
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Box(
                        modifier = Modifier
                            .size(115.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logoata),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(95.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Entrar na sua conta",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrownDolcezza
                    )

                    Text(
                        text = "Bem-vindo(a) de volta!",
                        fontSize = 16.sp,
                        color = BrownDolcezza.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ModernInputField(
                        value = email,
                        onValueChange = { email = it },
                        label = "E-mail",
                        icon = Icons.Default.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ModernInputField(
                        value = senha,
                        onValueChange = { senha = it },
                        label = "Senha",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Esqueceu a senha?",
                            color = BrownDolcezza.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "Função não implementada", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || senha.isBlank()) {
                                Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isProcessing = true
                            auth.signInWithEmailAndPassword(email, senha)
                                .addOnSuccessListener {
                                    navController.navigate("loading") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                                .addOnFailureListener {
                                    isProcessing = false
                                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                        },
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = pinkDolcezza,
                            contentColor = BrownDolcezza,
                            disabledContainerColor = pinkDolcezza.copy(alpha = 0.4f),
                            disabledContentColor = BrownDolcezza.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .shadow(10.dp, RoundedCornerShape(20.dp))
                            .border(2.dp, BrownDolcezza, RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            text = "Entrar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrownDolcezza
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    Row {
                        Text(text = "Não tem uma conta?", color = BrownDolcezza.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cadasatre-se",
                            color = BrownDolcezza,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("register")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}