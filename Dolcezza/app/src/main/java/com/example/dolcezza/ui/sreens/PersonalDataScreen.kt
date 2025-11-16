package com.example.dolcezza.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dolcezza.R
import com.example.dolcezza.ui.theme.BrownDolcezza
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// IMPORTAÇÃO NECESSÁRIA PARA SUPRIMIR O AVISO DE API EXPERIMENTAL
import androidx.compose.material3.ExperimentalMaterial3Api

// --- Cores Temáticas ---
val BackgroundBeige = Color(0xFFF5EFE9)
val AnimatedBeige = Color(0xFFEDE3DD)
val CardBeige = Color(0xFFFCF6F2)
val DarkBrownText = Color(0xFF42292A)
val PinkButton = Color(0xFFDA4F6B)

// ----------------------------------------------------------------------
//                        FUNÇÕES DE MÁSCARA
// ----------------------------------------------------------------------

fun cleanNumber(text: String): String {
    return text.filter { it.isDigit() }
}

fun formatCpf(text: String): String {
    val clean = cleanNumber(text).take(11)
    return when (clean.length) {
        in 1..3 -> clean
        in 4..6 -> "${clean.substring(0, 3)}.${clean.substring(3)}"
        in 7..9 -> "${clean.substring(0, 3)}.${clean.substring(3, 6)}.${clean.substring(6)}"
        in 10..11 -> "${clean.substring(0, 3)}.${clean.substring(3, 6)}.${clean.substring(6, 9)}-${clean.substring(9)}"
        else -> clean
    }
}

fun formatPhone(text: String): String {
    val clean = cleanNumber(text).take(11)
    return when (clean.length) {
        in 1..2 -> "(${clean}"
        in 3..7 -> "(${clean.substring(0, 2)}) ${clean.substring(2)}"
        in 8..10 -> "(${clean.substring(0, 2)}) ${clean.substring(2, 6)}-${clean.substring(6)}"
        in 11..11 -> "(${clean.substring(0, 2)}) ${clean.substring(2, 7)}-${clean.substring(7)}"
        else -> clean
    }
}


@Composable
fun PersonalDataScreen(navController: NavController) {
    // LÓGICA DE BACKEND (MANTIDA)
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // REMOVIDO: Contadores de mudança (changeCount) para o Nome

    // Contador específico para E-mail (0 = pode trocar, 1 = trocou)
    var emailChangeCount by remember { mutableStateOf(0) }

    var isLoading by remember { mutableStateOf(true) }

    // Estado para saber se o campo E-mail está temporariamente editável
    var isEmailEditable by remember { mutableStateOf(false) }

    // Dialogs
    // REMOVIDO: showDialog para limite de Nome
    var showEmailLimitDialog by remember { mutableStateOf(false) } // Para limite de Email

    // EFEITO: Animação de cor de fundo (MANTIDA)
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundTransition")
    val animatedBgColor by infiniteTransition.animateColor(
        initialValue = BackgroundBeige,
        targetValue = AnimatedBeige,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "animatedBgColor"
    )

    // Carregar dados existentes
    LaunchedEffect(Unit) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    name = document.getString("name") ?: ""
                    email = document.getString("email") ?: ""
                    cpf = document.getString("cpf") ?: ""
                    phone = document.getString("phone") ?: ""
                    gender = document.getString("gender") ?: ""
                    address = document.getString("address") ?: ""
                    // REMOVIDO: Carrega changeCount
                    emailChangeCount = document.getLong("emailChangeCount")?.toInt() ?: 0
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    if (isLoading) {
        ThematicLoadingScreen()
        return
    }

    // Formulário com Design Aprimorado
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = animatedBgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Título e Subtítulo (MANTIDOS)
            Spacer(Modifier.height(30.dp))
            Text(
                "Dados Pessoais",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = DarkBrownText
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Visualize e edite suas informações de perfil.",
                fontSize = 14.sp,
                color = DarkBrownText.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(30.dp))

            // Card Principal
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBeige),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Nome (AGORA PERMANENTEMENTE READ-ONLY)
                    ThematicDataField(
                        label = "Nome completo",
                        value = name,
                        readOnly = true, // Nome é sempre read-only
                        onChange = { /* Ignora a mudança */ },
                        onClickReadOnly = { Toast.makeText(context, "O nome não pode ser alterado por segurança.", Toast.LENGTH_SHORT).show() } // Feedback ao usuário
                    )

                    Spacer(Modifier.height(16.dp))

                    // E-mail (LÓGICA DE TROCA DE BOTÃO MANTIDA)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Campo de E-mail
                        ThematicDataField(
                            label = "E-mail",
                            value = email,
                            // E-mail é readOnly, a menos que o usuário o desbloqueie na sessão atual
                            readOnly = !isEmailEditable,
                            onChange = { email = it },
                            // Se o botão for visível, o campo é menor
                            modifier = if (emailChangeCount == 0 && !isEmailEditable) {
                                Modifier.weight(0.7f)
                            } else {
                                Modifier.fillMaxWidth()
                            },
                            // Mostra a notificação se tentar editar sem clicar no botão
                            onClickReadOnly = {
                                if (emailChangeCount == 0) {
                                    Toast.makeText(context, "Clique em 'Trocar' para editar o e-mail.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "O e-mail já foi alterado e não pode ser mais modificado.", Toast.LENGTH_LONG).show()
                                }
                            }
                        )

                        // Botão "Trocar" (aparece apenas se a contagem for 0 e ainda não estiver editável)
                        if (emailChangeCount == 0 && !isEmailEditable) {
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { showEmailLimitDialog = true }, // Abre o novo dialog
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PinkButton.copy(alpha = 0.8f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(0.3f)
                                    .height(55.dp) // Alinha com o TextField
                            ) {
                                Text("Trocar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // CPF (MANTIDO)
                    ThematicDataField(
                        label = "CPF",
                        value = formatCpf(cpf),
                        onChange = { newText -> cpf = cleanNumber(newText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(Modifier.height(16.dp))

                    // Telefone (MANTIDO)
                    ThematicDataField(
                        label = "Telefone",
                        value = formatPhone(phone),
                        onChange = { newText -> phone = cleanNumber(newText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(Modifier.height(16.dp))

                    // Gênero (MANTIDO)
                    GenderDropdown(
                        label = "Gênero",
                        selectedOption = gender,
                        onOptionSelected = { gender = it }
                    )
                    Spacer(Modifier.height(16.dp))

                    // Endereço (MANTIDO)
                    ThematicDataField(label = "Endereço completo", value = address, onChange = { address = it })

                    Spacer(Modifier.height(30.dp))

                    // Botão Salvar (LÓGICA ATUALIZADA)
                    Button(
                        onClick = {
                            val uid = user?.uid ?: return@Button

                            // Se o email foi desbloqueado e editado, incrementa o contador para 1.
                            // Nota: Não precisamos mais do changeCount (nome)
                            val newEmailChangeCount = if (isEmailEditable) 1 else emailChangeCount

                            val data = mutableMapOf(
                                "name" to name, "email" to email, "cpf" to cpf,
                                "phone" to phone, "gender" to gender, "address" to address,
                                // "changeCount" REMOVIDO
                                "emailChangeCount" to newEmailChangeCount // NOVO CAMPO SALVO
                            )

                            db.collection("users").document(uid)
                                .update(data as Map<String, Any>)
                                .addOnSuccessListener {
                                    // Atualiza o estado local do email
                                    emailChangeCount = newEmailChangeCount
                                    isEmailEditable = false

                                    Toast.makeText(context, "Dados atualizados!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Erro ao salvar: ${it.message}", Toast.LENGTH_LONG).show()
                                }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinkButton,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                    ) {
                        Text("Salvar Informações", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // REMOVIDO: Dialog para Limite de Nome (if (showDialog) { ... })

            // NOVO: Dialog para Limite de Troca de Email (Corrigido para usar CardBeige)
            if (showEmailLimitDialog) {
                AlertDialog(
                    onDismissRequest = { showEmailLimitDialog = false },
                    title = {
                        Text(
                            "Trocar E-mail",
                            color = DarkBrownText,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            "Você só pode alterar seu e-mail uma única vez. Após a troca e salvamento, ele será permanentemente bloqueado para futuras edições. Deseja continuar?",
                            color = DarkBrownText
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            isEmailEditable = true // Desbloqueia a edição
                            showEmailLimitDialog = false
                            Toast.makeText(context, "O campo E-mail foi desbloqueado. Por favor, edite e clique em 'Salvar Informações'.", Toast.LENGTH_LONG).show()
                        }) {
                            Text("SIM, TROCAR", color = PinkButton, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEmailLimitDialog = false }) {
                            Text("CANCELAR", color = DarkBrownText.copy(alpha = 0.8f))
                        }
                    },
                    // MUDANÇA: Definição da cor do container para CardBeige
                    containerColor = CardBeige
                )
            }
        }
    }
}

// ----------------------------------------------------------------------
//                        COMPONENTES DE UI (MANTIDOS)
// ----------------------------------------------------------------------

@Composable
fun ThematicDataField(
    label: String,
    value: String,
    readOnly: Boolean = false,
    onChange: (String) -> Unit,
    onClickReadOnly: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier.fillMaxWidth() // Modificador para controlar peso na Row
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (!readOnly) {
                onChange(it)
            } else {
                onClickReadOnly()
            }
        },
        readOnly = readOnly,
        placeholder = { Text(label, color = DarkBrownText.copy(alpha = 0.5f)) },
        label = { Text(label, color = DarkBrownText.copy(alpha = 0.8f)) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PinkButton,
            unfocusedBorderColor = DarkBrownText.copy(alpha = 0.3f),
            disabledBorderColor = DarkBrownText.copy(alpha = 0.1f),
            focusedContainerColor = CardBeige,
            unfocusedContainerColor = CardBeige,
            disabledContainerColor = CardBeige.copy(alpha = 0.5f),
            focusedLabelColor = PinkButton,
            unfocusedLabelColor = DarkBrownText.copy(alpha = 0.8f),
            focusedTextColor = DarkBrownText,
            unfocusedTextColor = DarkBrownText,
            disabledTextColor = DarkBrownText.copy(alpha = 0.8f) // Deixando o texto visível mesmo em disabled
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(
    label: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val genderOptions = listOf("Masculino", "Feminino", "Prefiro não dizer")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            label = { Text(label, color = DarkBrownText.copy(alpha = 0.8f)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkButton,
                unfocusedBorderColor = DarkBrownText.copy(alpha = 0.3f),
                focusedContainerColor = CardBeige,
                unfocusedContainerColor = CardBeige,
                focusedLabelColor = PinkButton,
                unfocusedLabelColor = DarkBrownText.copy(alpha = 0.8f),
                focusedTextColor = DarkBrownText,
                unfocusedTextColor = DarkBrownText,
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genderOptions.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, color = DarkBrownText) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun ThematicLoadingScreen() {
    val rotation by rememberInfiniteTransition("rotationTransition").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrownDolcezza)
            .wrapContentSize(Alignment.Center)
    ) {
        // Nota: Assumindo que R.drawable.logo ou R.drawable.mm é um recurso válido
        Image(
            painter = painterResource(id = R.drawable.mm),
            contentDescription = "Carregando...",
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer(rotationZ = rotation)
                .align(Alignment.CenterHorizontally),
            colorFilter = ColorFilter.tint(PinkButton)
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Preparando seus dados...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}