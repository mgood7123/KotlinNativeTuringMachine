package stateMachine.programs

import stateMachine.LinkedList
import stateMachine.StateMachines

data class StateMachineProgramsAcceptor(val head: Int, val Accepted: Boolean)

fun <T> StateMachines.Programs.acceptor(head: Int, input: LinkedList<T>, toAccept: LinkedList<T>): StateMachineProgramsAcceptor {
    val Acceptor = StateMachines.TuringMachine<T, Boolean>()
    Acceptor.debug = true
    val l = toAccept.lastIndex
    val SUCCESS = -1
    val FAILURE = -2
    Acceptor.add(FAILURE) {
        println("failure")
        Acceptor.writeOutputTape(false)
    }
    Acceptor.add(SUCCESS) {
        println("success")
        Acceptor.moveInputTapeLeft()
        Acceptor.writeOutputTape(true)
    }
    toAccept.forEachIndexed { index, t ->
        if (index == l)
            Acceptor.addRule(
                index,
                t,
                null,
                null,
                SUCCESS,
                FAILURE
            )
        else Acceptor.addRule(
            index,
            t,
            null,
            Acceptor.LMITL,
            index+1,
            FAILURE
        )
    }
    Acceptor.ruleBuilder.build()
    Acceptor.addTape()
    Acceptor.setOutputTape(false)
    Acceptor.setOutputTapeDefault(false)
    Acceptor.setInputTape(input, head)
    Acceptor.executeState(0)
    val o = Acceptor.getOutputTape()
    Acceptor.statistics.print()
    println("result: ${o.tape[0]}")
    return StateMachineProgramsAcceptor(Acceptor.getInputTape().head, o.tape[0])
}

fun <T> StateMachines.Programs.acceptor(input: LinkedList<T>, toAccept: LinkedList<T>) =
    acceptor(0, input, toAccept)