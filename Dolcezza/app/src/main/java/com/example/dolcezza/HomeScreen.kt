package com.example.dolcezza

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dolcezza.ui.theme.*
import java.text.NumberFormat
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

// -----------------------------------------------------------------------------
// ⭐ LÓGICA DE CARRINHO (SIMULAÇÃO LOCAL NA HOMESCREEN)
// -----------------------------------------------------------------------------
data class HomeCartProduct(val id: String, val name: String, val price: Double)
data class HomeCartItem(val product: HomeCartProduct, var quantity: Int)

// Função auxiliar para converter preço de String para Double
fun parsePrice(priceString: String): Double {
    val cleanString = priceString.replace("R$", "").replace(",", ".").trim()
    return cleanString.toDoubleOrNull() ?: 0.0
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {

    // 1. CHECAGEM DE LOGIN
    val auth = FirebaseAuth.getInstance()
    val isUserLoggedIn = auth.currentUser != null

    // 2. ESTADO DO DIÁLOGO
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    // Gerenciamento do estado do carrinho local
    val cartItems = remember { mutableStateListOf<HomeCartItem>() }

    val cartItemCount by remember {
        derivedStateOf { cartItems.sumOf { it.quantity } }
    }
    val cartTotalPrice by remember {
        derivedStateOf { cartItems.sumOf { it.product.price * it.quantity } }
    }

    // 3. NOVA FUNÇÃO DE TENTATIVA DE ADICIONAR AO CARRINHO
    fun attemptAddToCart(product: HomeCartProduct, quantity: Int = 1) {
        if (!isUserLoggedIn) {
            // Se não estiver logado, exibe o diálogo
            showLoginRequiredDialog = true
        } else {
            // Lógica existente para adicionar ao carrinho (só executa se estiver logado)
            val existingItem = cartItems.find { it.product.id == product.id }
            if (existingItem != null) {
                // Usa o método copy na lista para garantir a recomposição
                val index = cartItems.indexOf(existingItem)
                cartItems[index] = existingItem.copy(quantity = existingItem.quantity + quantity)
            } else {
                cartItems.add(HomeCartItem(product, quantity))
            }
        }
    }


    // -------------------------------------------------------------------------
    // ⭐ ESTRUTURA PRINCIPAL: BOX para sobrepor o botão flutuante e o diálogo
    // -------------------------------------------------------------------------

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .background(Color.White)
                // Padding extra no fim para o botão não cobrir o último item
                .padding(bottom = if (cartItemCount > 0) 100.dp else 20.dp)
        ) {

            AnimatedWaveHeader()

            Spacer(modifier = Modifier.height(20.dp))

            // -------------------------------------------------------------------------
            // ⭐ CARROSSEL DE NOVIDADES
            // -------------------------------------------------------------------------

            data class ProdutoNovidade(
                val nome: String,
                val imagem: Int,
                val preco: String
            )

            val novidades = listOf(
                ProdutoNovidade("Macarons Coloridos", R.drawable.maracons, "R$ 14,90"),
                ProdutoNovidade("Torta de Morango", R.drawable.torta_m, "R$ 19,90"),
                ProdutoNovidade("Donuts Glaceados", R.drawable.donuts, "R$ 12,00")
            )

            var favoritos by remember { mutableStateOf(setOf<String>()) }
            var selecionado by remember { mutableStateOf<String?>(null) }

            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(novidades) { produto ->

                    val isFav = produto.nome in favoritos
                    val isSelected = selecionado == produto.nome

                    Column(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .width(180.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .height(180.dp)
                                .fillMaxWidth()
                                .clickable {
                                    selecionado =
                                        if (selecionado == produto.nome) null else produto.nome
                                }
                        ) {

                            Image(
                                painter = painterResource(id = produto.imagem),
                                contentDescription = produto.nome,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // ❤️ ÍCONE FAVORITO
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFav) VermelhoDolcezza else Color(0xFFFFAAAA),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                                    .size(28.dp)
                                    .clickable {
                                        favoritos =
                                            if (isFav) favoritos - produto.nome else favoritos + produto.nome
                                    }
                            )


                            // TAG "Novidade"
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(PinkDolcezza, RoundedCornerShape(50))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_especiais),
                                        contentDescription = null,
                                        tint = BrownDolcezza,
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        "Novidade",
                                        color = BrownDolcezza,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // 🛒 ÍCONE DE CARRINHO (AÇÃO ADICIONAR AO CARRINHO)
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MarrowAcizentado) // Cor para indicar que é clicável
                                        .clickable {
                                            // 4. CHAMA A NOVA FUNÇÃO DE TENTATIVA
                                            attemptAddToCart(
                                                HomeCartProduct(
                                                    id = "Novidade-${produto.nome}",
                                                    name = produto.nome,
                                                    price = parsePrice(produto.preco)
                                                )
                                            )
                                            // Opcional: Desselecionar após adicionar
                                            selecionado = null
                                        }
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_carrinho),
                                        contentDescription = "Adicionar ao Carrinho",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = produto.nome,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrownDolcezza
                        )
                        Text(
                            text = produto.preco,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // CATEGORIAS
            val categorias = listOf(
                Triple("Tortas & Bolos", R.drawable.icon_bolo__1_, BeigeDolcezza),
                Triple("Cupcakes", R.drawable.icon_cupcakes, PinkDolcezza),
                Triple("Macarons", R.drawable.icon_macarons1, LightCream),
                Triple("Donuts", R.drawable.icon_donuts, LightCream),
                Triple("Especiais", R.drawable.icon_especiais, PinkDolcezza)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categorias) { (name, icon, color) ->
                    CategoryButton(name = name, icon = icon, background = color)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ⭐ OFERTAS DA SEMANA
            Text(
                "Ofertas da Semana!",
                color = BrownDolcezza,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val ofertasSemana = listOf(
                Oferta("Cupcake de Baunilha", "R$ 6,50", R.drawable.cupcakebanunilha, PinkDolcezza),
                Oferta("Torta de Limão", "R$ 18,00", R.drawable.tortalimao, BeigeDolcezza),
                Oferta("Macarons Sortidos", "R$ 12,00", R.drawable.maracons, LightCream),
                Oferta("Donut Chocolate", "R$ 7,90", R.drawable.donuts, PinkDolcezza),
                Oferta("Cheesecake Morango", "R$ 16,00", R.drawable.torta_m, BeigeDolcezza)
            )

            Column {
                ofertasSemana.forEach { oferta ->
                    // 5. PASSA A NOVA FUNÇÃO DE TENTATIVA PARA OFFERITEM
                    OfferItem(oferta = oferta, onAddToCart = { product -> attemptAddToCart(product) })
                }
            }
        }

        // -------------------------------------------------------------------------
        // ⭐ BOTÃO FLUTUANTE DO CARRINHO (Layered no Box)
        // -------------------------------------------------------------------------
        FloatingCartButton(
            navController = navController,
            itemCount = cartItemCount,
            totalPrice = cartTotalPrice,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 6. RENDERIZAÇÃO CONDICIONAL DO DIÁLOGO
        if (showLoginRequiredDialog) {
            LoginRequiredDialog(
                onDismiss = { showLoginRequiredDialog = false },
                onNavigateToProfile = {
                    showLoginRequiredDialog = false
                    navController.navigate("profile") // Redireciona para o perfil
                }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// ⭐ NOVO COMPONENTE: DIÁLOGO DE LOGIN OBRIGATÓRIO (COM TÍTULO CENTRALIZADO)
// -----------------------------------------------------------------------------

@Composable
fun LoginRequiredDialog(onDismiss: () -> Unit, onNavigateToProfile: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Ação Necessária",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = BrownDolcezza,
                modifier = Modifier.fillMaxWidth(), // Ocupa a largura total do diálogo
                textAlign = TextAlign.Center      // Centraliza o texto dentro dessa largura
            )
        },
        text = {
            Column {
                Text(
                    "Você precisa fazer login ou se cadastrar para adicionar itens ao seu carrinho.",
                    color = BrownDolcezza
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Deseja ir para a tela de Perfil agora?",
                    color = BrownDolcezza.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Botão Principal (Fazer Login / Cadastrar)
                Button(
                    onClick = onNavigateToProfile,
                    colors = ButtonDefaults.buttonColors(containerColor = PinkDolcezza),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fazer Login / Cadastrar", color = BrownDolcezza, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))

                // 2. Botão Secundário (Continuar Comprando)
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar Comprando", color = BrownDolcezza.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = CardBeige,
        shape = RoundedCornerShape(16.dp)
    )
}

// -----------------------------------------------------------------------------
// ⭐ NOVO COMPONENTE: BOTÃO FLUTUANTE DO CARRINHO (ESTILO IFOOD)
// -----------------------------------------------------------------------------

@Composable
fun FloatingCartButton(
    navController: NavController,
    itemCount: Int,
    totalPrice: Double,
    modifier: Modifier = Modifier
) {
    if (itemCount == 0) return

    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(totalPrice)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(VermelhoDolcezza)
                .clickable { navController.navigate("cart") } // AÇÃO PRINCIPAL: NAVEGAÇÃO
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lado Esquerdo: Contador de Itens
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(BrownDolcezza)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$itemCount itens",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            // Centro: Texto "Ver Carrinho"
            Text(
                text = "Ver Carrinho",
                color = BrownDolcezza,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Lado Direito: Preço Total
            Text(
                text = formattedPrice,
                color = BrownDolcezza,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}


// -----------------------------------------------------------------------------
// ⭐ OFERTA ITEM ATUALIZADO (com onAddToCart)
// -----------------------------------------------------------------------------

data class Oferta(
    val nome: String,
    val preco: String,
    val imagem: Int,
    val background: Color
)

@Composable
fun OfferItem(oferta: Oferta, onAddToCart: (HomeCartProduct) -> Unit) {

    var favorito by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(oferta.background)
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {

            Image(
                painter = painterResource(id = oferta.imagem),
                contentDescription = oferta.nome,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // ❤️ FAVORITO
            Icon(
                imageVector = if (favorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = VermelhoDolcezza,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .clickable { favorito = !favorito }
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                oferta.nome,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrownDolcezza
            )

            Text(
                oferta.preco,
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold
            )
        }

        // 🛒 ÍCONE DE CARRINHO NAS OFERTAS (AÇÃO ADICIONAR AO CARRINHO)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MarrowAcizentado) // Mudei a cor para PinkDolcezza para ser mais evidente o clique
                .clickable {
                    onAddToCart(
                        HomeCartProduct(
                            id = "Oferta-${oferta.nome}",
                            name = oferta.nome,
                            price = parsePrice(oferta.preco)
                        )
                    )
                }
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_carrinho),
                contentDescription = "Adicionar ao Carrinho",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


// -----------------------------------------------------------------------------
// ⭐ CABEÇALHO ANIMADO, CATEGORY BUTTON, BOTTOM NAV (MANTIDOS)
// -----------------------------------------------------------------------------

@Composable
fun AnimatedWaveHeader() {

    val infiniteTransition = rememberInfiniteTransition()

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(MarrowAcizentado),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val width = size.width
            val height = size.height

            fun Path.drawSineWave(amplitude: Float, phase: Float, verticalShift: Float) {
                reset()
                moveTo(0f, height * verticalShift)

                for (x in 0..width.toInt()) {
                    val y = (amplitude * kotlin.math.sin((x + phase) * (Math.PI / 180f))).toFloat()
                    lineTo(x.toFloat(), height * verticalShift + y)
                }

                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            val wave1 = Path().apply {
                drawSineWave(35f, offset1, 0.72f)
            }

            val wave2 = Path().apply {
                drawSineWave(55f, offset2, 0.80f)
            }

            drawPath(wave1, VermelhoDolcezza.copy(alpha = 0.7f))
            drawPath(wave2, VermelhoDolcezza.copy(alpha = 0.9f))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(100.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bem-vindo(a) à Dolcezza!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = BeigeDolcezza,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = PinkDolcezza,
                        offset = Offset(0f, 0f)
                    )
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Seu paraíso de delícias está a um toque.",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = BeigeDolcezza,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = PinkDolcezza,
                        offset = Offset(0f, 0f)
                    )
                )
            )
        }
    }
}


@Composable
fun CategoryButton(name: String, icon: Int, background: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(16.dp)
            .width(90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = BrownDolcezza,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(name, fontSize = 12.sp, color = BrownDolcezza, textAlign = TextAlign.Center)
    }
}

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {

    // Box para aplicar o formato de pílula (arredondamento)
    // Usamos padding para que a pílula não toque as bordas da tela
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp), // Ajuste o padding conforme necessário
        contentAlignment = Alignment.BottomCenter
    ) {

        // Aplica o fundo (MarrowAcizentado) e o formato de pílula na Base
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp) // Altura fixa para a barra
                .clip(RoundedCornerShape(32.dp)) // ARREDONDAMENTO
                .background(MarrowAcizentado)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Item: Início
            CustomNavBarItem(
                selected = currentRoute == "home",
                onClick = { navController.navigate("home") },
                icon = Icons.Default.Home,
                label = "Início"
            )

            // Item: Explorar
            CustomNavBarItem(
                selected = currentRoute == "explore",
                onClick = { navController.navigate("explore") },
                icon = Icons.Default.Search,
                label = "Explorar"
            )

            // Item: Pedidos
            CustomNavBarItem(
                selected = currentRoute == "order", // Use "order" conforme a rota corrigida
                onClick = { navController.navigate("order") },
                icon = Icons.Default.Assignment,
                label = "Pedidos"
            )

            // Item: Perfil
            CustomNavBarItem(
                selected = currentRoute == "profile",
                onClick = { navController.navigate("profile") },
                icon = Icons.Default.Person,
                label = "Perfil"
            )
        }
    }
}

@Composable
fun RowScope.CustomNavBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    val iconColor = if (selected) Color.White else Color.White.copy(alpha = 0.7f)
    val backgroundColor = if (selected) PinkButton else Color.Transparent

    // O Box externo é o que recebe o background e o formato arredondado quando selecionado
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp) // Altura do item clicável
            .clip(RoundedCornerShape(24.dp)) // Pílula interna do item
            .background(backgroundColor, shape = RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Se estiver selecionado, exibe apenas o ícone.
        // Se não estiver, exibe o ícone e o label abaixo.
        if (selected) {
            // Apenas Ícone no modo pílula (como na imagem de referência)
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        } else {
            // Modo não selecionado: Exibe Ícone + Label (padrão)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                // Removendo o Label no modo não selecionado para um visual mais limpo/compacto
                /*
                Spacer(Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = iconColor
                )
                */
            }
        }
    }
}