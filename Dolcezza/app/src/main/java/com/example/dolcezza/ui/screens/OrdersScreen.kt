package com.example.dolcezza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clip
import java.text.NumberFormat
import java.util.Locale



// ----------------------------------------------------------------------
//                        ENUMERADOR E CLASSES DE DADOS
// ----------------------------------------------------------------------

enum class OrderStatus(val displayName: String, val color: Color) {
    IN_PREPARATION("Em Preparação", Color(0xFFF0AD4E)), // Amarelo/Laranja
    ON_THE_WAY("A Caminho", Color(0xFF5BC0DE)),     // Azul Claro
    DELIVERED("Entregue", Color(0xFF5CB85C)),       // Verde
    CANCELED("Cancelado", Color(0xFFD9534F));       // Vermelho

    fun getIcon(): ImageVector = when (this) {
        IN_PREPARATION -> Icons.Default.Replay
        ON_THE_WAY -> Icons.Default.LocalShipping
        DELIVERED -> Icons.Default.Done
        CANCELED -> Icons.Default.Close
    }
}

data class OrderItem(
    val name: String,
    val quantity: Int
)

data class Order(
    val id: String,
    val date: String,
    val total: Double,
    val status: OrderStatus,
    val items: List<OrderItem>
)


// ----------------------------------------------------------------------
//                        TELA DE PEDIDOS
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(navController: NavController) {

    // Simulação do Histórico de Pedidos (Mock Data)
    val orders = listOf(
        Order(
            id = "DZ-2025-001",
            date = "15/11/2025",
            total = 89.90,
            status = OrderStatus.DELIVERED,
            items = listOf(OrderItem("Bolo de Morango", 1), OrderItem("Doce Gourmet", 3))
        ),
        Order(
            id = "DZ-2025-002",
            date = "20/11/2025",
            total = 55.00,
            status = OrderStatus.ON_THE_WAY,
            items = listOf(OrderItem("Torta de Limão", 1), OrderItem("Cupcake", 2))
        ),
        Order(
            id = "DZ-2025-003",
            date = "01/12/2025",
            total = 125.70,
            status = OrderStatus.IN_PREPARATION,
            items = listOf(OrderItem("Bolo de Aniversário", 1))
        ),
        Order(
            id = "DZ-2025-004",
            date = "05/12/2025",
            total = 25.00,
            status = OrderStatus.CANCELED,
            items = listOf(OrderItem("Macaron Sortido", 5))
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Usa a cor do fundo
        containerColor = BackgroundBeige,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Meus Pedidos",
                        fontWeight = FontWeight.Bold,
                        color = DarkBrownText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Fechar",
                            tint = DarkBrownText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // Usa a cor do card para o TopAppBar
                    containerColor = CardBeige
                )
            )
        }
    ) { paddingValues ->
        if (orders.isEmpty()) {
            EmptyOrdersContent(Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderItemCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: Order) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBeige),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Ação: Ver detalhes do pedido */ }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cabeçalho do Pedido: ID e Data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedido #${order.id.substringAfterLast("-")}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkBrownText
                )
                Text(
                    text = order.date,
                    fontSize = 14.sp,
                    color = DarkBrownText.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Status do Pedido
            StatusIndicator(order.status)

            Divider(Modifier.padding(vertical = 12.dp), color = DarkBrownText.copy(alpha = 0.1f))

            // Itens
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                order.items.forEach { item ->
                    Text(
                        text = "${item.quantity}x ${item.name}",
                        fontSize = 14.sp,
                        color = DarkBrownText.copy(alpha = 0.8f)
                    )
                }
            }


            Divider(Modifier.padding(vertical = 4.dp), color = DarkBrownText.copy(alpha = 0.1f))

            // Total e Ação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total
                Column {
                    Text(
                        "Total Pago:",
                        fontSize = 14.sp,
                        color = DarkBrownText.copy(alpha = 0.7f)
                    )
                    Text(
                        formatCurrency(order.total),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PinkButton // Cor de destaque
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Status e Botão (Adaptado para o design da imagem)
                // A ação de botão (Avaliar Pedido / Acompanhar) é simulada aqui:
                when (order.status) {
                    OrderStatus.DELIVERED -> {
                        Button(
                            onClick = { /* Navegar para Avaliar */ },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkButton),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Avaliar Pedido", fontSize = 14.sp)
                        }
                    }
                    OrderStatus.ON_THE_WAY, OrderStatus.IN_PREPARATION -> {
                        Button(
                            onClick = { /* Navegar para Acompanhar */ },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkButton),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Acompanhar", fontSize = 14.sp)
                        }
                    }
                    OrderStatus.CANCELED -> {
                        // Não mostra botão para pedidos cancelados
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(status: OrderStatus) {
    // Componente que usa a cor definida no OrderStatus enum
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(status.color.copy(alpha = 0.1f)) // Fundo leve
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Ícone opcional: Removido para seguir o estilo da imagem original onde o status é só texto.
        // Se quiser o ícone, descomente:
        /*
        Icon(
            imageVector = status.getIcon(),
            contentDescription = null,
            tint = status.color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        */
        Text(
            text = status.displayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = status.color
        )
    }
}


@Composable
fun EmptyOrdersContent(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.ShoppingBag,
            contentDescription = "Sem Pedidos",
            tint = PinkButton.copy(alpha = 0.6f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Você ainda não fez nenhum pedido.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrownText
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Explore nossas delícias e faça seu primeiro pedido agora!",
            fontSize = 16.sp,
            color = DarkBrownText.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}