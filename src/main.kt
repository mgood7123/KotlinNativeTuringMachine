import stateMachine.StateMachines
import stateMachine.examples.*
import stateMachine.lambdaTrampolineUsageExample
import stateMachine.programs.acceptor

fun main() {
    StateMachines.Examples.TuringMachine().SimpleProgram()
    StateMachines.Examples.TuringMachine().ThreeStateBusyBeaver()
    StateMachines.Examples.TuringMachine().FourStateBusyBeaver()
    StateMachines.Examples.TuringMachine().Acceptor()
    StateMachines.Programs().acceptor<String>("def".toLinkedList, "abc".toLinkedList) // false
    StateMachines.Programs().acceptor<String>("c".toLinkedList, "abc".toLinkedList) // false
    StateMachines.Programs().acceptor<String>("abb".toLinkedList, "abc".toLinkedList) // false
    StateMachines.Programs().acceptor<String>("abcdef".toLinkedList, "abc".toLinkedList) // true
    StateMachines.Programs().acceptor("abc".toLinkedList, "abc".toLinkedList) // true
    StateMachines.Programs().acceptor<String>(3, "abcdef".toLinkedList, "def".toLinkedList) // true
    StateMachines.Examples.TuringMachine().AcceptorSequencer("abcdef", "abc", "def") // true
    lambdaTrampolineUsageExample()
}