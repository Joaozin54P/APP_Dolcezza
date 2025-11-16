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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.dolcezza.ui.theme.BrownDolcezza
import com.example.dolcezza.ui.theme.MarrowAcizentado
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavHostController) {

    val pinkDolcezza = Color(0xffffaaaa)
    val context = LocalContext.current

    var isProcessing by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // CAMPOS
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    // Firebase
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // O Box externo define o fundo de gradiente
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
        // A Column interna é quem lida com a rolagem e o teclado
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
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
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(95.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Crie sua conta",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrownDolcezza
                    )

                    Text(
                        text = "É rápido e fácil!",
                        fontSize = 16.sp,
                        color = BrownDolcezza.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ModernInputField( // <-- O componente é acessado
                        value = nome,
                        onValueChange = { nome = it },
                        label = "Nome completo",
                        icon = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ModernInputField( // <-- O componente é acessado
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        icon = Icons.Default.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ModernInputField( // <-- O componente é acessado
                        value = senha,
                        onValueChange = { senha = it },
                        label = "Senha",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ModernInputField( // <-- O componente é acessado
                        value = confirmarSenha,
                        onValueChange = { confirmarSenha = it },
                        label = "Confirmar senha",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Button(
                        onClick = {

                            // 1. VALIDAÇÃO DE CAMPOS VAZIOS
                            if (nome.isBlank() || email.isBlank() || senha.isBlank() || confirmarSenha.isBlank()) {
                                Toast.makeText(context, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            // 2. VALIDAÇÃO DE SENHAS
                            if (senha != confirmarSenha) {
                                Toast.makeText(context, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isProcessing = true

                            auth.createUserWithEmailAndPassword(email, senha)
                                .addOnSuccessListener { result ->

                                    val uid = result.user?.uid ?: return@addOnSuccessListener

                                    val userData = hashMapOf(
                                        "uid" to uid,
                                        "name" to nome,
                                        "email" to email,
                                        "createdAt" to System.currentTimeMillis()
                                    )

                                    db.collection("users").document(uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            navController.navigate("loading") {
                                                popUpTo("register") { inclusive = true }
                                            }
                                        }
                                        .addOnFailureListener {
                                            isProcessing = false
                                            Toast.makeText(context, "Erro ao salvar no Firestore.", Toast.LENGTH_LONG).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    isProcessing = false
                                    Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
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
                        Icon(
                            painter = painterResource(id = R.drawable.icon__coberturaa),
                            contentDescription = null,
                            tint = BrownDolcezza,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Criar Conta",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrownDolcezza
                        )
                    }

                    // Spacer maior no final para garantir que o botão suba acima do teclado
                    Spacer(modifier = Modifier.height(26.dp))

                    Row {
                        Text(text = "Já tem uma conta?", color = BrownDolcezza.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Fazer Login",
                            color = BrownDolcezza,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("login")
                            }
                        )
                    }
                }
            }

            // Spacer no final
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun ModernInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Corrigindo a referência BrownDolcezza para que seja acessível
    val BrownDolcezza = Color(0xFF42292A) // Use a definição da sua cor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .background(Color(0xFFF7F2EF), RoundedCornerShape(16.dp))
            .border(
                width = 2.dp,
                color = BrownDolcezza,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(label, color = BrownDolcezza.copy(alpha = 0.7f))
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrownDolcezza
                )
            },
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = BrownDolcezza
                        )
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = BrownDolcezza,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = BrownDolcezza,
                unfocusedTextColor = BrownDolcezza
            ),
            visualTransformation = when {
                isPassword && !passwordVisible -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}