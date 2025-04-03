//package com.example.smarthouse_mobile.data.repository
//
//import com.example.smarthouse_mobile.data.model.*
//
//object MockRepository {
//    private val users = listOf(
//        User("gulshan", "gk25", listOf("house1", "house2")),
//        User("kavish", "kavish1202", listOf("house3"))
//    )
//
//    private val houses = listOf(
//        Home(
//            "house1", "Main House",
//            rooms = listOf(
//                Room("room1", "Living Room", listOf(
//                    Device("Bulb 1", true,),
//                    Device( "Fan 1", false,)
//                ))
//            ),
//            unassignedDevices = listOf(
//                Device( "Smart Plug",  false)
//            )
//        ),
//        Home("house2", "Vacation Home", emptyList(), emptyList()),
//        Home("house3", "Office", emptyList(), emptyList())
//    )
//
//    fun authenticateUser(username: String, password: String): User? {
//        return users.find { it.username == username && it.password == password }
//    }
//
//    fun getHousesForUser(user: UserModel): List<Home> {
//        return houses.filter { user.houseIds.contains(it.houseId) }
//    }
//    fun getHouseById(houseId: String?): Home? {
//        return houses.find { it.houseId == houseId }
//    }
//
//}