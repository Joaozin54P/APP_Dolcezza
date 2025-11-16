package com.example.dolcezza

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

/**
 * LoadingScreen - nova sequência:
 * 1 → Massa caindo
 * 2 → Indo para a forma
 * 3 → Cupcake assado no forno
 * 4 → Solta fumacinha
 * 5 → Emoji 😉 piscando + ✨
 */

@Composable
fun LoadingScreen(navController: NavHostController) {

    // etapa atual (1..5)
    var stage by remember { mutableStateOf(1) }

    // tempos ajustados (~5s total)
    LaunchedEffect(Unit) {
        delay(1400); stage = 2     // massa -> forminha
        delay(1400); stage = 3     // cupcake assado no forno
        delay(1600); stage = 4     // fumacinha gostosa
        delay(1200); stage = 5     // emoji piscadinha final
        delay(1400)

        // ir para a próxima tela
        navController.navigate("profile") {
            popUpTo("register") { inclusive = true }
        }
    }

    // Cores
    val creamPink = Color(0xFFFFA7C4)
    val cupcakeBase = Color(0xFF6B3F2A)
    val wrapper = Color(0xFFB86F5B)
    val ovenGray = Color(0xFF4E4E50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F6)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Troca animada de etapas
        AnimatedContent(
            targetState = stage,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith
                        fadeOut(tween(300))
            }
        ) { s ->

            when (s) {
                1 -> CupcakePouringStage(cupcakeBase = cupcakeBase, wrapper = wrapper)
                2 -> CupcakeOnTrayIntoOven(cupcakeBase = cupcakeBase, wrapper = wrapper, ovenGray = ovenGray)
                3 -> CupcakeReadyInOven(cupcakeBase = cupcakeBase, wrapper = wrapper, ovenGray = ovenGray)
                4 -> CupcakeSmokeStage(base = cupcakeBase, wrapper = wrapper)
                5 -> FinalWinkStage()
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Textos para cada etapa
        Text(
            text = when (stage) {
                1 -> "Misturando a massa..."
                2 -> "Colocando na forminha..."
                3 -> "Cupcake assando no forno..."
                4 -> "Humm... que cheirinho bom!"
                5 -> "Prontinho! 😉✨"
                else -> "Preparando..."
            },
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            color = Color(0xFF6B4232)
        )
    }
}

/* -----------------------------------------------------------
   ETAPA 1 — massa sendo colocada na forminha (versão premium)
   ----------------------------------------------------------- */
@Composable
fun CupcakePouringStage(
    cupcakeBase: Color,
    wrapper: Color
) {
    val transition = rememberInfiniteTransition(label = "pouring")

    // queda da gota
    val dropY by transition.animateFloat(
        initialValue = -36f,
        targetValue = 36f,
        animationSpec = infiniteRepeatable(
            tween(850, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dropAnim"
    )

    // massa acumulando na forma conforme a gota cai
    val fillAmount = ((dropY + 36f) / 72f).coerceIn(0f, 1f)

    Canvas(modifier = Modifier.size(260.dp)) {

        val w = size.width
        val h = size.height

        /* -----------------------------------------------------------
           1) PRATINHO (mais detalhado)
           ----------------------------------------------------------- */
        drawRoundRect(
            color = Color(0xFFF7EEE9),
            topLeft = Offset(w * 0.07f, h * 0.74f),
            size = Size(w * 0.86f, h * 0.12f),
            cornerRadius = CornerRadius(40f, 40f)
        )

        // sombra leve do pratinho
        drawOval(
            color = Color(0x22000000),
            topLeft = Offset(w * 0.12f, h * 0.82f),
            size = Size(w * 0.76f, h * 0.08f)
        )

        /* -----------------------------------------------------------
           2) JARRA despejando massa
           ----------------------------------------------------------- */
        val jarWidth = w * 0.28f
        val jarHeight = h * 0.22f

        rotate(
            degrees = -22f,
            pivot = Offset(w * 0.50f, h * 0.32f)
        ) {
            drawRoundRect(
                color = Color(0xFFF0E4D8),
                topLeft = Offset(w * 0.50f, h * 0.20f),
                size = Size(jarWidth, jarHeight),
                cornerRadius = CornerRadius(28f, 28f)
            )

            // detalhe do vidro (reflexo)
            drawRoundRect(
                color = Color(0x33FFFFFF),
                topLeft = Offset(w * 0.52f, h * 0.22f),
                size = Size(jarWidth * 0.6f, jarHeight * 0.8f),
                cornerRadius = CornerRadius(18f, 18f)
            )
        }

        /* -----------------------------------------------------------
           3) WRAPPER (com brilho)
           ----------------------------------------------------------- */
        val wrapW = w * 0.52f
        val wrapH = h * 0.22f
        val wrapLeft = (w - wrapW) / 2f
        val wrapTop = h * 0.58f

        drawRoundRect(
            color = wrapper,
            topLeft = Offset(wrapLeft, wrapTop),
            size = Size(wrapW, wrapH),
            cornerRadius = CornerRadius(20f, 20f)
        )

        // brilho lateral do wrapper
        drawRoundRect(
            color = Color(0x22FFFFFF),
            topLeft = Offset(wrapLeft + wrapW * 0.12f, wrapTop + 6f),
            size = Size(wrapW * 0.12f, wrapH - 12f),
            cornerRadius = CornerRadius(16f, 16f)
        )

        /* -----------------------------------------------------------
           4) MASSA CAINDO (gota principal)
           ----------------------------------------------------------- */
        val dropWidth = 42f
        val dropHeight = 34f

        drawOval(
            color = cupcakeBase,
            topLeft = Offset(w / 2f - dropWidth / 2f, h * 0.40f + dropY),
            size = Size(dropWidth, dropHeight)
        )

        // reflexo suave na gota
        drawOval(
            color = Color.White.copy(alpha = 0.20f),
            topLeft = Offset(w / 2f - dropWidth / 4f, h * 0.41f + dropY),
            size = Size(dropWidth / 2.2f, dropHeight / 2.2f)
        )

        /* -----------------------------------------------------------
           5) RESPINGOS DELICADOS
           ----------------------------------------------------------- */
        drawCircle(
            color = cupcakeBase,
            radius = 7f,
            center = Offset(w * 0.46f, h * 0.50f + dropY * 0.35f),
            alpha = 0.9f
        )

        drawCircle(
            color = cupcakeBase,
            radius = 5f,
            center = Offset(w * 0.54f, h * 0.50f + dropY * 0.65f),
            alpha = 0.9f
        )

        /* -----------------------------------------------------------
           6) MASSA ACUMULANDO DENTRO DA FORMA
           ----------------------------------------------------------- */
        val fillHeight = wrapH * 0.65f * fillAmount

        drawRoundRect(
            color = cupcakeBase,
            topLeft = Offset(wrapLeft + wrapW * 0.06f, wrapTop + wrapH * 0.35f - fillHeight),
            size = Size(wrapW * 0.88f, fillHeight),
            cornerRadius = CornerRadius(16f, 16f)
        )
    }
}



/* -------------------------------------------------------------------------------------
   ETAPA 2 — cupcake sobre bandeja entrando no forno
   + vapor quente
   + luz interna piscando
   + sombra dinâmica conforme entra
   ------------------------------------------------------------------------------------- */
@Composable
fun CupcakeOnTrayIntoOven(
    cupcakeBase: Color,
    wrapper: Color,
    ovenGray: Color
) {
    /* -----------------------------------------------------------
       ANIMAÇÕES
    ----------------------------------------------------------- */

    // deslize do cupcake (para dentro)
    val offsetX by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(850, easing = FastOutSlowInEasing),
        label = "cupcakeSlide"
    )
    val startOffset = 120f

    // vapor subindo (loop)
    val vaporAnim = rememberInfiniteTransition(label = "vapor")
    val vaporOffset by vaporAnim.animateFloat(
        initialValue = 0f,
        targetValue = -28f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vaporOffset"
    )

    // luz quente pulsando dentro do forno
    val flicker by vaporAnim.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flickerLight"
    )

    // sombra do cupcake (fica mais intensa conforme entra)
    val dynamicShadowAlpha = (1f - (offsetX / startOffset)).coerceIn(0f, 1f) * 0.28f


    /* -----------------------------------------------------------
       DESENHO
    ----------------------------------------------------------- */

    Canvas(modifier = Modifier.size(260.dp)) {

        val w = size.width
        val h = size.height

        /* -----------------------------------------------------------
           FORNO — estrutura externa
        ----------------------------------------------------------- */
        val ovenTop = Offset(w * 0.05f, h * 0.06f)
        val ovenSize = Size(w * 0.9f, h * 0.7f)

        drawRoundRect(
            color = ovenGray,
            topLeft = ovenTop,
            size = ovenSize,
            cornerRadius = CornerRadius(22f, 22f)
        )

        // borda interna (profundidade)
        drawRoundRect(
            color = Color(0xFF1F1F1F),
            topLeft = Offset(ovenTop.x + 6f, ovenTop.y + 6f),
            size = Size(ovenSize.width - 12f, ovenSize.height - 12f),
            cornerRadius = CornerRadius(20f, 20f)
        )

        /* -----------------------------------------------------------
           VIDRO DO FORNO — com luz quente pulsando
        ----------------------------------------------------------- */
        val winLeft = ovenTop.x + 18f
        val winTop = ovenTop.y + ovenSize.height * 0.12f
        val winW = ovenSize.width - 36f
        val winH = ovenSize.height - ovenSize.height * 0.28f

        drawRoundRect(
            color = Color(0xFF2E2F31),
            topLeft = Offset(winLeft, winTop),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(12f, 12f)
        )

        // reflexo suave
        drawRect(
            color = Color.White.copy(alpha = 0.06f),
            topLeft = Offset(winLeft + winW * 0.12f, winTop + winH * 0.06f),
            size = Size(winW * 0.35f, winH * 0.18f)
        )

        // 🔥 luz quente interna pulsando
        drawOval(
            color = Color(0xFFCC8B3D).copy(alpha = flicker),
            topLeft = Offset(winLeft + winW * 0.22f, winTop + winH * 0.48f),
            size = Size(winW * 0.55f, winH * 0.38f)
        )


        /* -----------------------------------------------------------
           BANDEJA — volume
        ----------------------------------------------------------- */
        val trayW = winW * 0.60f
        val trayH = 18f
        val trayLeft = winLeft + (winW - trayW) / 2f
        val trayTop = winTop + winH * 0.58f

        drawRoundRect(
            color = Color(0xFF2E2E2E),
            topLeft = Offset(trayLeft, trayTop + 2f),
            size = Size(trayW, trayH + 2f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        drawRoundRect(
            color = Color(0xFF474747),
            topLeft = Offset(trayLeft, trayTop),
            size = Size(trayW, trayH),
            cornerRadius = CornerRadius(6f, 6f)
        )


        /* -----------------------------------------------------------
           CUPCAKE — posição animada
        ----------------------------------------------------------- */
        val cakeW = winW * 0.28f
        val cakeH = winH * 0.22f
        val cakeX = trayLeft + (trayW - cakeW) / 2f + startOffset * (offsetX / startOffset)
        val cakeY = trayTop - cakeH * 0.12f


        // 🌑 sombra dinâmica conforme entra
        drawOval(
            color = Color.Black.copy(alpha = dynamicShadowAlpha),
            topLeft = Offset(cakeX + cakeW * 0.10f, cakeY + cakeH * 0.42f),
            size = Size(cakeW * 0.80f, cakeH * 0.22f)
        )

        // wrapper
        drawRoundRect(
            color = wrapper,
            topLeft = Offset(cakeX, cakeY + cakeH * 0.45f),
            size = Size(cakeW, cakeH * 0.55f),
            cornerRadius = CornerRadius(12f, 12f)
        )

        // massa
        drawOval(
            color = cupcakeBase,
            topLeft = Offset(cakeX + cakeW * 0.06f, cakeY),
            size = Size(cakeW * 0.88f, cakeH * 0.90f)
        )

        // brilho da massa
        drawOval(
            color = Color.White.copy(alpha = 0.14f),
            topLeft = Offset(cakeX + cakeW * 0.18f, cakeY + cakeH * 0.06f),
            size = Size(cakeW * 0.50f, cakeH * 0.26f)
        )


        /* -----------------------------------------------------------
           VAPOR — subindo do interior do forno
        ----------------------------------------------------------- */
        val vaporX = trayLeft + trayW * 0.5f

        repeat(3) { i ->
            val drift = (i - 1) * 10f
            drawCircle(
                color = Color.White.copy(alpha = 0.14f - (i * 0.03f)),
                radius = 10f + i * 3f,
                center = Offset(vaporX + drift, trayTop + vaporOffset + (i * -10f))
            )
        }
    }
}


@Composable
fun CupcakeReadyInOven(cupcakeBase: Color, wrapper: Color, ovenGray: Color) {

    val glowAnim = rememberInfiniteTransition()
    val glow by glowAnim.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.23f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = Modifier.size(260.dp)) {

        val w = size.width
        val h = size.height

        // forno
        val ovenTop = Offset(w * 0.05f, h * 0.06f)
        val ovenSize = Size(w * 0.9f, h * 0.7f)
        drawRoundRect(
            color = ovenGray,
            topLeft = ovenTop,
            size = ovenSize,
            cornerRadius = CornerRadius(20f, 20f)
        )

        // janela escura
        val winLeft = ovenTop.x + 18f
        val winTop = ovenTop.y + ovenSize.height * 0.12f
        val winW = ovenSize.width - 36f
        val winH = ovenSize.height * 0.72f
        drawRoundRect(
            color = Color(0xFF2E2F31),
            topLeft = Offset(winLeft, winTop),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(14f, 14f)
        )

        // luz interna animada
        drawRoundRect(
            color = Color(0xFFFFE9D4).copy(alpha = glow),
            topLeft = Offset(winLeft, winTop),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(14f, 14f)
        )

        // bandeja
        val trayW = winW * 0.60f
        val trayH = 16f
        val trayLeft = winLeft + (winW - trayW) / 2f
        val trayTop = winTop + winH * 0.62f
        drawRoundRect(
            color = Color(0xFF3E3E3E),
            topLeft = Offset(trayLeft, trayTop),
            size = Size(trayW, trayH),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // cupcake pronto (sem carinha)
        val cakeW = winW * 0.30f
        val cakeH = winH * 0.24f
        val cakeX = trayLeft + (trayW - cakeW) / 2f
        val cakeY = trayTop - cakeH * 0.20f

        // wrapper
        drawRoundRect(
            color = wrapper,
            topLeft = Offset(cakeX, cakeY + cakeH * 0.45f),
            size = Size(cakeW, cakeH * 0.55f),
            cornerRadius = CornerRadius(14f, 14f)
        )

        // massa assada
        drawOval(
            color = cupcakeBase,
            topLeft = Offset(cakeX + cakeW * 0.06f, cakeY),
            size = Size(cakeW * 0.88f, cakeH * 0.9f)
        )
    }
}


@Composable
fun CupcakeSmokeStage(base: Color, wrapper: Color) {

    val riseAnim = rememberInfiniteTransition()
    val smokeOffset by riseAnim.animateFloat(
        initialValue = 12f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.size(240.dp)) {

        val w = size.width
        val h = size.height

        // cupcake centralizado fora do forno
        val cakeW = w * 0.52f
        val cakeH = h * 0.34f
        val cakeX = (w - cakeW) / 2f
        val cakeY = h * 0.48f

        // fumaça subindo
        drawCircle(
            color = Color(0xFFC8C8C8).copy(alpha = 0.28f),
            radius = 22f,
            center = Offset(w / 2f - 14f, cakeY - 24f + smokeOffset)
        )
        drawCircle(
            color = Color(0xFFC8C8C8).copy(alpha = 0.20f),
            radius = 18f,
            center = Offset(w / 2f + 12f, cakeY - 40f + smokeOffset * 1.2f)
        )
        drawCircle(
            color = Color(0xFFC8C8C8).copy(alpha = 0.14f),
            radius = 26f,
            center = Offset(w / 2f, cakeY - 64f + smokeOffset * 1.4f)
        )

        // wrapper
        drawRoundRect(
            color = wrapper,
            topLeft = Offset(cakeX, cakeY + cakeH * 0.55f),
            size = Size(cakeW, cakeH * 0.45f),
            cornerRadius = CornerRadius(14f, 14f)
        )

        // massa
        drawOval(
            color = base,
            topLeft = Offset(cakeX + cakeW * 0.08f, cakeY),
            size = Size(cakeW * 0.84f, cakeH * 0.85f)
        )
    }
}


@Composable
fun FinalWinkStage() {

    val anim = rememberInfiniteTransition()

    // piscar do olho
    val blink by anim.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            tween(700, easing = LinearEasing),
            RepeatMode.Reverse
        )
    )

    // estrela saindo
    val starRise by anim.animateFloat(
        initialValue = 10f,
        targetValue = -24f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.size(160.dp)) {

        val w = size.width
        val h = size.height

        // emoji piscando 😉
        drawCircle(
            color = Color(0xFFFFE28A),
            radius = 60f,
            center = Offset(w/2f, h/2f)
        )

        // olho aberto
        drawCircle(
            color = Color.Black,
            radius = 8f,
            center = Offset(w/2f - 20f, h/2f - 10f)
        )

        // olho piscando (vira uma linha)
        drawRoundRect(
            color = Color.Black,
            topLeft = Offset(w/2f + 10f, h/2f - 10f),
            size = Size(20f, blink * 4f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // boca
        drawRoundRect(
            color = Color(0xFF3B2A20),
            topLeft = Offset(w/2f - 18f, h/2f + 18f),
            size = Size(36f, 12f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // estrelinha ✨ subindo
        drawCircle(
            color = Color(0xFFFFF4B0),
            radius = 10f,
            center = Offset(w/2f + 30f, h/2f - 20f + starRise)
        )
    }
}