package com.example.dolcezza.ui.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Importei todos os ícones default para garantir acesso ao KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dolcezza.ui.theme.BrownDolcezza
import com.example.dolcezza.ui.theme.PinkDolcezza
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun ProfileScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser // Objeto User (pode ser null)
    val db = FirebaseFirestore.getInstance()

    // Se o user for null → Apenas mostrar a tela de opções
    if (user == null) {
        AuthOptionsScreen(navController)
        return
    }

    var name by remember { mutableStateOf("Carregando...") }
    val initialEmail = user?.email ?: "" // Use o operador de segurança aqui também, só para garantir.

    // Buscar dados
    // MUDANÇA CRÍTICA AQUI: Usamos o operador de navegação segura (?.) e tratamos o nulo.
    LaunchedEffect(user?.uid) {
        val uid = user?.uid ?: return@LaunchedEffect // Se user for null, sai do LaunchedEffect imediatamente.

        db.collection("users").document(uid).get()
            .addOnSuccessListener {
                name = it.getString("name") ?: "Usuário"
            }
    }

    Scaffold(
        // Fundo bege suave para um visual mais acolhedor
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(30.dp))

            // 1. HEADER - CARD DE INFORMAÇÕES DO USUÁRIO
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBeige),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Círculo para simular Foto de Perfil (Usando a primeira letra do nome)
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(PinkDolcezza.copy(alpha = 0.3f), shape = androidx.compose.foundation.shape.CircleShape)
                            .wrapContentSize(Alignment.Center)
                    ) {
                        Text(
                            text = name.take(1).uppercase(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = BrownDolcezza
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrownDolcezza,
                    )

                    Text(
                        text = initialEmail,
                        fontSize = 14.sp,
                        color = BrownDolcezza.copy(alpha = 0.7f),
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            // 2. ITENS DE MENU (Refinados)

            // Título sutil para a seção de conta
            Text(
                text = "MINHA CONTA",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrownDolcezza.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBeige, RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp)
            ) {

                ProfileItem("Dados Pessoais", Icons.Default.Home) {
                    navController.navigate("personalData")
                }

                ProfileItem("Meus Pedidos", Icons.Default.ShoppingBag) {
                    navController.navigate("order")
                }

                ProfileItem("Favoritos", Icons.Default.Favorite) {
                    navController.navigate("favorites")
                }

                ProfileItem("Métodos de Pagamento", Icons.Default.Payment) {
                    navController.navigate("payments")
                }

                // Ultimo item sem divisor
                ProfileItemNoDivider("Minhas Avaliações", Icons.Default.Star) {
                    navController.navigate("reviews")
                }
                ProfileItemNoDivider("Sobre Nós", Icons.Default.People) {
                    navController.navigate("aboutUs")
                }
            }

            Spacer(Modifier.height(20.dp))

            // 3. LOGOUT (Em destaque, fora do card principal)
            Button(
                onClick = {
                    auth.signOut()

                    // CORREÇÃO FINAL NA NAVEGAÇÃO
                    // 1. Navega para a tela de opções de autenticação.
                    navController.navigate("home") {
                        // 2. popUpTo(navController.graph.id) limpa TODAS as telas da pilha de navegação.
                        popUpTo(navController.graph.id) { inclusive = true }
                        // 3. Garante que se já estiver em AuthOptions, a instância seja única.
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinkDolcezza,
                    contentColor = BrownDolcezza
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Sair", Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sair", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

// ----------------------------------------------------------------------
//                        COMPONENTES DE UI APRIMORADOS
// ----------------------------------------------------------------------

@Composable
fun AuthOptionsScreen(navController: NavController) {

    // 1. Cria a transição infinita para animação em loop
    val infiniteTransition = rememberInfiniteTransition(label = "FloatingAnimation")

    // 2. Define o valor animado para a translação Y (movimento vertical)
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f, // Move 30 pixels para cima
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = EaseInOutSine), // 2.5s para ir/voltar
            repeatMode = RepeatMode.Reverse // Inverte a direção no final do ciclo
        ),
        label = "FloatingOffsetY"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(horizontal = 30.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // 3. SEÇÃO DO TÍTULO E ANIMAÇÃO
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Bem-vindo(a) à Dolcezza!",
                color = BrownDolcezza,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Text(
                text = "Seu paraíso de doces favoritos. Entre e peça já!",
                color = BrownDolcezza.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // 4. ELEMENTO ANIMADO (Usando um ícone de Bolo/Doce)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cake, // Ícone de bolo (ou use o ícone de logo, se for adequado)
                    contentDescription = "Animação de Boas-Vindas",
                    tint = PinkDolcezza,
                    modifier = Modifier
                        .size(150.dp)
                        // Aplica a animação de movimento vertical (flutuante)
                        .graphicsLayer {
                            translationY = offsetY
                        }
                )
            }

            Spacer(Modifier.height(40.dp)) // Espaço entre a animação e o texto abaixo

            Text(
                text = "Para continuar, entre ou crie uma conta",
                color = BrownDolcezza,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }


        // 5. BOTÕES
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(PinkDolcezza),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Fazer Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("register") },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                border = BorderStroke(2.dp, BrownDolcezza),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Criar Conta", color = BrownDolcezza, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}


@Composable
fun ProfileItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    // Row clicável, mais limpa e sem Card. A cor de fundo vem do container (CardBeige)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Ícone e Título
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PinkDolcezza,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(20.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrownDolcezza
                )
            }

            // Ícone de Navegação
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "Ir para $title",
                tint = BrownDolcezza.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }

        // Divisor Sutil
        Divider(
            color = BrownDolcezza.copy(alpha = 0.1f),
            thickness = 1.dp,
            modifier = Modifier.padding(start = 52.dp, end = 8.dp) // Alinhado após o ícone
        )
    }
}

// Componente para o último item da lista (sem o divisor abaixo)
@Composable
fun ProfileItemNoDivider(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Ícone e Título
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = PinkDolcezza,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(20.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = BrownDolcezza
            )
        }

        // Ícone de Navegação
        Icon(
            Icons.Filled.KeyboardArrowRight,
            contentDescription = "Ir para $title",
            tint = BrownDolcezza.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
    }
}