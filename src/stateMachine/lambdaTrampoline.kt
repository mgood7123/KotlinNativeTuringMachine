package stateMachine

tailrec fun <functionArgumentType, functionReturnType> lambdaTrampoline(
    function: ((functionArgumentType) -> functionReturnType)?,
    argument: functionArgumentType
): Any? = when (function) {
    null -> argument
    else -> {
        val r = function(argument)
        @Suppress("UNCHECKED_CAST")
        when (r) {
            !is Pair<*, *> -> r
            else -> lambdaTrampoline(
                r.first as ((functionArgumentType) -> functionReturnType)?,
                r.second as functionArgumentType
            )
        }
    }
}

fun lambdaTrampolineUsageExample() {
    lambdaTrampoline({ it: Int ->
        println(it)
        Pair(
            { it: Int ->
                println(it)
                Pair(
                    { it: Int ->
                        println(it)
                        Pair(null, null)
                    },
                    it + 1
                )
            },
            it+1
        )
    }, 0)

    val F2: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
        println(it)
        Pair(null, null) // we could also return Pair(null, it)
    }
    val F1: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
        println(it)
        Pair(F2, it+1)
    }
    val F0: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
        println(it)
        Pair(F1, it+1)
    }
    lambdaTrampoline(F0, 5)
    val funList = mutableListOf<(i: Int) -> Pair<((i: Int) -> Any?)?, Int?>>()
    val F0A: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
        println(it)
        if (it == 0)
            Pair(null, it)
        else
            Pair(funList[1], it-1)
    }
    val F1A: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
        println(it)
        if (it == 0)
            Pair(null, it)
        else
            Pair(funList[0], it-1)
    }
    funList.add(F0A)
    funList.add(F1A)
    lambdaTrampoline(funList[0], 50)
}