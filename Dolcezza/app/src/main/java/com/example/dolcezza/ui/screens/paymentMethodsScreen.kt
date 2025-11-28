package com.example.dolcezza

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dolcezza.ui.theme.CardBeige
import com.example.dolcezza.ui.theme.DarkBrownText
import com.example.dolcezza.ui.theme.PinkButton
import com.example.dolcezza.ui.theme.PinkDolcezza
import com.example.dolcezza.ui.theme.VermelhoDolcezza

@Composable
fun paymentMethodsScreen(
    onBackClick: () -> Unit
){
    val BrownDolcezza = DarkBrownText
    val BeigeFundo = BackgroundBeige
    val CardColor = CardBeige
    val PinkSelected = PinkDolcezza

    var metodoSelecionado by remember { mutableStateOf<String?>(null) }
    var salvarCartao by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeFundo)
    ) {
        Spacer(modifier = Modifier.height(38.dp))

        // HEADER ----------------------------------
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                tint = BrownDolcezza,
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                "Forma de Pagamento",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = BrownDolcezza,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // CONTEÚDO ----------------------------------
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
        ) {
            Text(
                text = "Selecione o método de pagamento",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrownDolcezza
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ITENS ----------------------------------
            MetodoPagamentoItem(
                titulo = "Cartão de Crédito/Débito",
                icon = R.drawable.ic_cartao,
                selecionado = metodoSelecionado == "cartao",
                onClick = { metodoSelecionado = "cartao" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            MetodoPagamentoItem(
                titulo = "Pix",
                icon = R.drawable.ic_pix,
                selecionado = metodoSelecionado == "pix",
                onClick = { metodoSelecionado = "pix" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            MetodoPagamentoItem(
                titulo = "Boleto",
                icon = R.drawable.ic_boleto,
                selecionado = metodoSelecionado == "boleto",
                onClick = { metodoSelecionado = "boleto" }
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Salvar cartão para futuras compras",
                    fontSize = 16.sp,
                    color = BrownDolcezza
                )

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = salvarCartao,
                    onCheckedChange = { salvarCartao = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = PinkDolcezza,
                        checkedThumbColor = BrownDolcezza
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // BOTÃO FINALIZAR --------------------------
            Button(
                onClick = { },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinkButton,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .shadow(12.dp, RoundedCornerShape(18.dp))
            ) {
                Text(
                    text = "Finalizar Pedido",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MetodoPagamentoItem(
    titulo: String,
    icon: Int,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    val BrownDolcezza = DarkBrownText

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .background(CardBeige, RoundedCornerShape(20.dp))
            .border(
                width = 2.dp,
                color = if (selecionado) VermelhoDolcezza else BrownDolcezza.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = BrownDolcezza,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrownDolcezza
            )

            Spacer(modifier = Modifier.weight(1f))

            // RADIO ----------------------------------
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(2.dp, if (selecionado) VermelhoDolcezza else BrownDolcezza, CircleShape)
                    .padding(3.dp)
            ) {
                if (selecionado) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(VermelhoDolcezza, CircleShape)
                    )
                }
            }
        }
    }
}