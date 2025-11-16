package com.example.dolcezza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.ExperimentalMaterial3Api
// ***************************************************************
// IMPORT CORRIGIDO: Esta linha é essencial para acessar R.drawable
// ***************************************************************
import com.example.dolcezza.R


// ----------------------------------------------------------------------
//                        DEFINIÇÃO DE CORES TEMA
// ----------------------------------------------------------------------


val HighlightPink = Color(0xFFFEECEE)


// ----------------------------------------------------------------------
//                             DATA CLASS
// ----------------------------------------------------------------------

data class TeamMember(
    val name: String,
    val role: String,
    val avatarResId: Int
)

// ----------------------------------------------------------------------
//                              TELA PRINCIPAL
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController) {

    // IDs das imagens de perfil (Agora o R.drawable deve funcionar)
    val teamMembers = listOf(
        TeamMember("Rebeca Matewanga", "Desenvolvedora Website", R.drawable.beca),
        TeamMember("João Pedro Machado", "Desenvolvedor Aplicativo", R.drawable.joee),
        TeamMember("Mariana Ocireu", "Desenvolvedora Aplicativo", R.drawable.mari),
        TeamMember("Giovanna Aparecida", "Denvolvedora Website", R.drawable.gi)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundBeige,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sobre Nós",
                        fontWeight = FontWeight.Bold,
                        color = DarkBrownText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = DarkBrownText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBeige
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. IMAGEM DA EQUIPE (Usando R.drawable.team_photo)
            item {
                TeamImageComponent(R.drawable.quartetoa)
            }

            // 2. INTRODUÇÃO À DOCEZZA
            item {
                CompanyIntroSection()
            }

            // 3. SEÇÃO DA EQUIPE
            item {
                Spacer(Modifier.height(30.dp))
                Text(
                    text = "Conheça a Equipe de Criação",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkBrownText,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(16.dp))
            }

            // 4. CARDS DOS MEMBROS
            items(teamMembers) { member ->
                TeamMemberCard(member = member)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// ----------------------------------------------------------------------
//                              COMPONENTES
// ----------------------------------------------------------------------

@Composable
fun TeamImageComponent(resId: Int) {
    Card(
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = HighlightPink),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Imagem da Equipe Dolcezza",
            contentScale = ContentScale.Crop, // Garante que a imagem preencha a área
            modifier = Modifier.fillMaxSize()
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
fun CompanyIntroSection() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.EmojiFoodBeverage,
                contentDescription = null,
                tint = PinkButton,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "A Doce Paixão da Dolcezza",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrownText
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Nós somos a Dolcezza, um projeto nascido da paixão por transformar ingredientes simples em momentos de pura felicidade. Acreditamos que cada doce deve contar uma história e trazer um sorriso. Nossa missão é oferecer a melhor experiência digital para os amantes de doces, com um toque de elegância e sabor.",
            fontSize = 16.sp,
            color = DarkBrownText.copy(alpha = 0.8f),
            textAlign = TextAlign.Justify
        )

        Spacer(Modifier.height(20.dp))
        Divider(color = PinkButton.copy(alpha = 0.2f))
    }
}

@Composable
fun TeamMemberCard(member: TeamMember) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBeige),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar (Agora exibe a imagem de recurso)
            Image(
                painter = painterResource(id = member.avatarResId),
                contentDescription = "Foto de perfil de ${member.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(16.dp))

            // Informações do Membro
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = member.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrownText
                )
                Text(
                    text = member.role,
                    fontSize = 14.sp,
                    color = PinkButton,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Ícone de destaque
            Icon(
                Icons.Filled.Star,
                contentDescription = "Membro da equipe",
                tint = DarkBrownText.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}