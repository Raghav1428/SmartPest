package com.example.smartpest.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class Shop(
    val name: String,
    val address: String,
    val products: String
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyShops(navController: NavHostController) {
    val shopList = listOf(
        Shop(
            "Green Fields Store",
            " 231/1, Katpadi Main Rd, Thottapalayam, Vellore, Tamil Nadu 632004",
            "Fertilizers, Seeds, Pesticides"
        ),
        Shop(
            "Crop Care Supplies",
            "No.4/1, Sai Arcade, 1, Katpadi Main Rd, Thottapalayam, Vellore, Tamil Nadu 632004",
            "Herbicides, Tools, Plant Nutrition"
        ),
        Shop(
            "Harvest Hub",
            "Bus Stop, No: 46, Elango Street, Dharapadavedu Near Chittoor, Katpadi, Vellore, Tamil Nadu 632007",
            "Farming Tools, Insecticides, Seeds"
        ),
        Shop(
            "AgriTools Depot",
            "Sai Arcade, Katpadi Main Rd, near Pachaiyyapas and Benz Park, near National Circle, Vellore, Tamil Nadu 632004",
            "Agriculture Equipment, Pesticides"
        ),
        Shop(
            "Farmers Friend",
            "No.16, 2, 14th East Cross Rd, Suthanthira Ponvizha Nagar, Gandhi Nagar, Vellore, Tamil Nadu 632006",
            "Fertilizers, Farm Machines"
        ),
        Shop(
            "Seed & Sprout",
            "Old No 40/11, New No 85, Near C.M.C Out Gate, Katpadi Road, Vellore, Tamil Nadu 632004",
            "Seeds, Irrigation Supplies"
        ),
        Shop(
            "Agro Mart",
            "16-B, Registrar Periasamy St, Sasthri Nagar, Krishna Nagar, Sankaranpalayam, Vellore, Tamil Nadu 632001",
            "Fertilizers, Plant Nutrition, Seeds"
        ),
        Shop(
            "Plant Power Hub",
            "80, Bangalore Rd, near Police quarters, Thottapalayam, Vellore, Tamil Nadu 632004",
            "Organic Fertilizers, Tools"
        ),
        Shop(
            "Field Solutions",
            "SH 9, Thottapalayam, Vellore, Tamil Nadu 632004",
            "Farm Equipment, Seeds, Pesticides"
        ),
        Shop(
            "Agro Growers",
            "No. 254, Srivari Building, Phase 2, Sathuvachari, Vellore, Tamil Nadu 632009",
            "Plant Growth Supplements, Tools"
        ),
        Shop(
            "Crop Yielders",
            "335, 14th St, Phase 2, Sathuvachari, Vellore, Tamil Nadu 632009",
            "Farming Tools, Fertilizers"
        ),
        Shop(
            "Farmland Essentials",
            "No 30/3 Rkc Complex, Chunnambukara St, Vellore, Tamil Nadu 632004",
            "Pesticides, Herbicides, Irrigation"
        )
    )

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    androidx.compose.material3.Text(
                        text = "Nearby Shops",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { navController.popBackStack() }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) {
        ShopList(
            shopList = shopList,
            modifier = Modifier.padding(top = 80.dp)
        )
    }
}


@Composable
fun ShopList(shopList: List<Shop>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(shopList) { shop ->
            ShopItem(shop = shop)
        }
    }
}

@Composable
fun ShopItem(shop: Shop) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = shop.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Address: ${shop.address}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Products: ${shop.products}", style = MaterialTheme.typography.bodySmall)
        }
    }
}