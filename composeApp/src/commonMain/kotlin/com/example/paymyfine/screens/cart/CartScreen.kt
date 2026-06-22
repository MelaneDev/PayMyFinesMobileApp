package com.example.paymyfine.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.data.cart.CartManager
import com.example.paymyfine.data.cart.CartProvider
import com.example.paymyfine.data.payment.PaymentViewModel
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.payments.PaymentScreen
import com.russhwolf.settings.Settings
import androidx.compose.ui.text.style.TextOverflow
import com.example.paymyfine.data.fines.CartItem
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState


class CartScreen(
    private val sessionStore: SessionStore,
    private val paymentVm: PaymentViewModel
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        val manager =
            remember { CartProvider.get(sessionStore) }


        var cart by remember {
            mutableStateOf(manager.getCart())
        }

        val total =
            cart.sumOf { it.amountInCents } / 100.0

        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            /////////////////////////////////////
            // ⭐ BACK + TITLE ROW
            /////////////////////////////////////

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = { navigator?.pop() }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    "Your Cart",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            /////////////////////////////////////
            // ⭐ CART LIST
            /////////////////////////////////////

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(
                    items = cart,
                    key = { it.noticeNumber }
                ) { item ->

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                manager.remove(item.noticeNumber)
                                cart = manager.getCart()
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true,
                        backgroundContent = {

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    ) {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = MaterialTheme.shapes.large
                        ) {
                            CartItemContent(
                                item = item,
                                onDelete = {
                                    manager.remove(item.noticeNumber)
                                    cart = manager.getCart()
                                }
                            )
                        }
                    }
                }
            }

            /////////////////////////////////////
            // ⭐ TOTAL
            /////////////////////////////////////

            Divider(Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    "R$total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            /////////////////////////////////////
            // ⭐ CHECKOUT BUTTON
            /////////////////////////////////////

            Button(
                onClick = {
                    navigator?.push(PaymentScreen(paymentVm))
                },
                enabled = cart.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(40.dp)
            ) {
                Text("Proceed to Checkout")
            }
        }
    }




@Composable
private fun CartItemContent(
    item: CartItem, // replace with your actual type
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = item.description,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "R${item.amountInCents / 100.0}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove item",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
  }
}
