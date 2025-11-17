package com.example.dolcezza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.dolcezza.ui.theme.LightPink
import java.text.NumberFormat
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth


// ----------------------------------------------------------------------
//                        CLASSES DE DADOS DO CARRINHO (Mantidas)
// ----------------------------------------------------------------------

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    // Usando um Int para simular o ID do recurso drawable (ex: R.drawable.bolo_chocolate)
    val imageUrl: Int
)

data class CartItem(
    val product: Product,
    val quantity: Int
)

// ----------------------------------------------------------------------
//                        LÓGICA E TELA DO CARRINHO
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {

    // Inicializa Firebase Auth
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val isUserLoggedIn = user != null

    // 2. VERIFICAÇÃO DE LOGIN E REDIRECIONAMENTO (CRÍTICO)
    LaunchedEffect(isUserLoggedIn) {
        if (!isUserLoggedIn) {
            // Se não estiver logado, navega para a tela de Perfil
            navController.navigate("profile") {
                // Limpa a tela do carrinho da pilha após a navegação
                popUpTo("cart") { inclusive = true }
            }
        }
    }

    // Se o usuário não estiver logado, não renderiza o conteúdo do carrinho
    if (!isUserLoggedIn) {
        return
    }

    // ESTADO DO CARRINHO: Agora inicializado vazio. O estado real
    // viria de um ViewModel compartilhado entre Home e Cart.
    val cartItems = remember {
        mutableStateListOf<CartItem>()
    }

    // ... (o restante da lógica e UI do Carrinho permanece inalterado) ...

    // Calculadoras de Preço
    val subtotal by remember {
        derivedStateOf { cartItems.sumOf { it.product.price * it.quantity } }
    }

    // Valor fixo de frete para simulação
    val shippingCost = 15.00

    val total by remember {
        derivedStateOf { subtotal + shippingCost }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundBeige,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Meu Carrinho",
                        fontWeight = FontWeight.Bold,
                        color = DarkBrownText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Fechar Carrinho",
                            tint = DarkBrownText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBeige
                )
            )
        },
        // O Rodapé (BottomBar) só é exibido se o carrinho não estiver vazio
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartBottomBar(
                    subtotal = subtotal,
                    shipping = shippingCost,
                    total = total,
                    onCheckoutClick = {
                        // Lógica de Finalizar Compra
                        println("Total a pagar: ${formatCurrency(total)}")
                    }
                )
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyCartContent(Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                // Padding ajustado para o BottomBar
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(cartItems, key = { it.product.id }) { item ->
                    CartItemRow(
                        item = item,
                        onQuantityChange = { newQuantity ->
                            updateCartItemQuantity(cartItems, item.product.id, newQuantity)
                        },
                        onRemove = {
                            removeCartItem(cartItems, item.product.id)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
//                        COMPONENTES E LÓGICA DE ESTADO (MANTIDOS)
// ----------------------------------------------------------------------

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(amount)
}

fun updateCartItemQuantity(
    cartItems: SnapshotStateList<CartItem>,
    productId: String,
    newQuantity: Int
) {
    if (newQuantity < 1) return

    val index = cartItems.indexOfFirst { it.product.id == productId }
    if (index != -1) {
        cartItems[index] = cartItems[index].copy(quantity = newQuantity)
    }
}

fun removeCartItem(
    cartItems: SnapshotStateList<CartItem>,
    productId: String
) {
    cartItems.removeAll { it.product.id == productId }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBeige),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem do Produto (Mock)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightPink)
                    .wrapContentSize(Alignment.Center)
            ) {
                // Aqui você usaria Coil/Glide para carregar a imagem do produto.
                // Usando ícone mockado para fins de demonstração
                Icon(
                    Icons.Filled.Favorite, // Placeholder Icon
                    contentDescription = null,
                    tint = PinkButton,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Detalhes do Item
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = DarkBrownText,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatCurrency(item.product.price),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PinkButton
                )
            }

            // Controle de Quantidade
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão Remover
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    enabled = item.quantity > 1,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(LightPink)
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Diminuir", tint = DarkBrownText)
                }

                Text(
                    text = item.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = DarkBrownText
                )

                // Botão Adicionar
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(PinkButton)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Aumentar", tint = Color.White)
                }
            }

            // Botão de Excluir Item (X)
            IconButton(
                onClick = onRemove,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Remover item", tint = DarkBrownText.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun CartBottomBar(
    subtotal: Double,
    shipping: Double,
    total: Double,
    onCheckoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBeige)
            .padding(16.dp)
    ) {
        // Detalhes do Preço
        PriceDetailRow("Subtotal", subtotal)
        Spacer(Modifier.height(4.dp))
        PriceDetailRow("Frete", shipping)

        Divider(Modifier.padding(vertical = 8.dp), color = DarkBrownText.copy(alpha = 0.1f))

        // Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkBrownText)
            Text(
                formatCurrency(total),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = PinkButton
            )
        }

        Spacer(Modifier.height(16.dp))

        // Botão Finalizar Compra
        Button(
            onClick = onCheckoutClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = PinkButton,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("Finalizar Compra", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
fun PriceDetailRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = DarkBrownText.copy(alpha = 0.8f))
        Text(formatCurrency(amount), fontSize = 14.sp, color = DarkBrownText)
    }
}

@Composable
fun EmptyCartContent(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.ShoppingBag,
            contentDescription = "Carrinho Vazio",
            tint = PinkButton.copy(alpha = 0.6f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Seu carrinho está vazio!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrownText
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Adicione bolos e doces para começar seu pedido.",
            fontSize = 16.sp,
            color = DarkBrownText.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CartIconInTopBar(navController: NavController) {
    IconButton(onClick = {
        // Esta é a ação que você deve usar:
        navController.navigate("cart")
    }) {
        Icon(
            Icons.Filled.ShoppingBag,
            contentDescription = "Carrinho de Compras"
        )
    }
}