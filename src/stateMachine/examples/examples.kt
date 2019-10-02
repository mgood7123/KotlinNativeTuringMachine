package stateMachine.examples

import stateMachine.StateMachines
import stateMachine.programs.StateMachineProgramsAcceptor
import stateMachine.programs.acceptor
import toLinkedList

fun StateMachines.Examples.TuringMachine.SimpleProgram() {
    val TM = StateMachines.TuringMachineInputOnly<String>()
    TM.debug = true
    TM.setInputBlank(" ")
    TM.addInputTape()
    TM.setInputTape(TM.getInputBlank(),"0","0","1")
    TM.addRule(0, TM.getInputBlank(), null, TM.LambdaMoveInputTapeLeft, 1)
    TM.addRule(0, "0", "1", TM.LambdaMoveInputTapeRight, 1)
    TM.addRule(0, "1", "0", TM.LambdaMoveInputTapeRight, 0)
    TM.addRule(1, TM.getInputBlank(), null, TM.LMITR)
    TM.addRule(1, "0", "1", TM.LambdaMoveInputTapeLeft, 1)
    TM.addRule(1, "1", "0", TM.LambdaMoveInputTapeLeft, 1)
    TM.ruleBuilder.build()
    TM.executeState(0)
}

fun StateMachines.Examples.TuringMachine.ThreeStateBusyBeaver() {
    val TM = StateMachines.TuringMachineInputOnly<Int>()
    TM.debug = true
    TM.setInputBlank(-1)
    TM.addInputTape()
    TM.setInputTape(TM.getInputBlank())
    TM.addRule(0, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeLeft, 1)
    TM.addRule(0, 0, TM.getInputBlank(), 0)
    TM.addRule(0, 1, 1)
    TM.addRule(1, TM.getInputBlank(), TM.getInputBlank(), TM.LambdaMoveInputTapeLeft, 2)
    TM.addRule(1, 0, TM.getInputBlank(), 0)
    TM.addRule(1, 1, 1, TM.LambdaMoveInputTapeLeft, 1)
    TM.addRule(2, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeRight, 2)
    TM.addRule(2, 0, TM.getInputBlank(), 0)
    TM.addRule(2, 1, 1, TM.LambdaMoveInputTapeRight, 0)
    TM.ruleBuilder.build()
    TM.executeState(0)
    println(TM.statistics)
}

fun StateMachines.Examples.TuringMachine.FourStateBusyBeaver() {
    val TM = StateMachines.TuringMachineInputOnly<Int>()
    TM.debug = true
    TM.setInputBlank(-1)
    TM.addInputTape()
    TM.setInputTape(TM.getInputBlank())
    TM.addRule(0, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeRight, 1)
    TM.addRule(0, 0, TM.getInputBlank(), 0)
    TM.addRule(0, 1, 1, TM.LambdaMoveInputTapeLeft, 1)
    TM.addRule(1, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeLeft, 0)
    TM.addRule(1, 0, TM.getInputBlank(), 0)
    TM.addRule(1, 1, TM.getInputBlank(), TM.LambdaMoveInputTapeLeft, 2)
    TM.addRule(2, TM.getInputBlank())
    TM.addRule(2, 0, TM.getInputBlank(),  0)
    TM.addRule(2, 1, 1, TM.LambdaMoveInputTapeLeft, 3)
    TM.addRule(3, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeRight, 3)
    TM.addRule(3, 0, TM.getInputBlank(),  0)
    TM.addRule(3, 1, TM.getInputBlank(), TM.LambdaMoveInputTapeRight, 0)
    TM.ruleBuilder.build()
    TM.executeState(0)
    println(TM.statistics)
}

fun StateMachines.Examples.TuringMachine.Acceptor() {
    val Acceptor = StateMachines.TuringMachine<String, Boolean>()
    Acceptor.add(Acceptor.defaultState()) {
        println("state 1")
        when (Acceptor.readInputTape()) {
            "a" -> {
                Acceptor.moveInputTapeLeft()
                it.transitionState(0)
            }
            else -> it.transitionState(2)
        }
    }
    Acceptor.add(0) {
        println("state 2")
        when (Acceptor.readInputTape()) {
            "b" -> {
                Acceptor.moveInputTapeLeft()
                it.transitionState(1)
            }
            else -> it.transitionState(2)
        }
    }
    Acceptor.add(1) {
        println("state 3")
        when (Acceptor.readInputTape()) {
            "c" -> {
                println("success")
                Acceptor.writeOutputTape(true)
            }
            else -> it.transitionState(2)
        }
    }
    Acceptor.add(2) {
        println("state 4")
        println("INVALID INPUT")
        Acceptor.writeOutputTape(false)
    }
    Acceptor.addTape()
    Acceptor.setOutputTape(false)
    Acceptor.setOutputTapeDefault(false)
    Acceptor.setInputTape("abc".toLinkedList)
    Acceptor.executeDefault()
    println("result: ${Acceptor.getOutputTape().tape}")
    Acceptor.statistics.print()
    Acceptor.reset()
    Acceptor.setInputTape("abcef".toLinkedList)
    Acceptor.executeDefault()
    println("result: ${Acceptor.getOutputTape().tape}")
    Acceptor.statistics.print()
    Acceptor.reset()
    Acceptor.setInputTape("c".toLinkedList)
    Acceptor.executeDefault()
    println("result: ${Acceptor.getOutputTape().tape}")
    Acceptor.statistics.print()
    Acceptor.reset()
    Acceptor.setInputTape("cba".toLinkedList)
    Acceptor.executeDefault()
    println("result: ${Acceptor.getOutputTape().tape}")
    Acceptor.statistics.print()
    Acceptor.reset()
    Acceptor.setInputTape("abb".toLinkedList)
    Acceptor.executeDefault()
    println("result: ${Acceptor.getOutputTape().tape}")
    Acceptor.statistics.print()
    Acceptor.reset()
}

// simple Acceptor sequencer
fun StateMachines.Examples.TuringMachine.AcceptorSequencer(
    input: String,
    AcceptingConditionA: String,
    AcceptingConditionB: String
): StateMachineProgramsAcceptor {
    val P = StateMachines.Programs()
    fun A() = P.acceptor(input.toLinkedList, AcceptingConditionA.toLinkedList)
    fun B(head: Int) = P.acceptor(head, input.toLinkedList, AcceptingConditionB.toLinkedList)
    val a = A()
    return if (a.Accepted) B(a.head)
    else a
}
