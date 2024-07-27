package com.piyushjt.todo

sealed class Screen(val route : String) {
    object MainScreen : Screen("main_screen")
    object AddTodoScreen : Screen("addNote_screen")

    fun withargs(vararg args : Int) : String {
        return buildString {
            append(route)
            args.forEach { args ->
                append("/$args")
            }
        }
    }

}