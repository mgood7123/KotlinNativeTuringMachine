# State Machine Construction Kit

```kotlin
import stateMachine.StateMachines

fun stateMachine.main() {
    val TM = StateMachines.TuringMachine<Int, Int>()
    TM.debug = true
    TM.setInputBlank(-1)
    TM.addTape()
    TM.setInputTape(TM.getInputBlank())
    TM.addRule(0, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeRight, 1)
    TM.addRule(0, 0, TM.getInputBlank(), null, 0)
    TM.addRule(0, 1, 1, TM.LambdaMoveInputTapeLeft, 1)
    TM.addRule(1, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeLeft, 0)
    TM.addRule(1, 0, TM.getInputBlank(), null, 0)
    TM.addRule(1, 1, TM.getInputBlank(), TM.LambdaMoveInputTapeLeft, 2)
    TM.addRule(2, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeRight, null)
    TM.addRule(2, 0, TM.getInputBlank(), null, 0)
    TM.addRule(2, 1, 1, TM.LambdaMoveInputTapeLeft, 3)
    TM.addRule(3, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeRight, 3)
    TM.addRule(3, 0, TM.getInputBlank(), null, 0)
    TM.addRule(3, 1, TM.getInputBlank(), TM.LambdaMoveInputTapeRight, 0)
    TM.ruleBuilder.build()
    TM.executeState(0)
    println(TM.statistics)
}
```

#
### Why this library?
* none suited my needs lol

### what this library offers
* a Turing Machine complete with Debug output support
  * from this other state machines can be implemented, including other Turing Machines
* examples
* programs
* tutorials (soon to come)
#
### how is a State Machine built?
a state machine is typically built in a non extensible hardcoded way, primarily due to speed and simplicity,
you often see these in parser generators such as Lex and Yacc

the exact implementation varies greatly but typically these consist of at least 2 states and optionally
some input

see https://en.wikipedia.org/wiki/Finite-state_machine for more information

an incredibly simple example would be a sequence:

```
start: go to state 0
state 0: when in state 0, go to state 1
state 1: when in state 1, go to state 2
state 2: when in state 2, go to state 3
state 3: exit
```

here, the state transition is as follows: `start>0>1>2>3`

common examples can be found here

* https://rosettacode.org/wiki/Finite_state_machine#Kotlin
* https://rosettacode.org/wiki/Finite_state_machine#C

  (for those familiar with C, since most parser libraries are written in C)

this video can explain better than i can

##### Computers Without Memory (Finite State Automata), A Computerphile Video. ( https://www.youtube.com/watch?v=vhiiia1_hC4 )

#
### how is a Turing Machine built?

a `Turing Machine` typically includes the following:
* x1 tape, infinite in length
* x1 tape head, keeps track of the tape's current positions
* x1 state machine
* (optional) x1 rule list
* x1 state list

`NOTE: this list was simplified, see the following link for the complete definition`

https://en.wikipedia.org/wiki/Turing_machine#Formal_definition

and is usually built using a `Finite State Machine`,
however a `Turing Machine` can be built using any type of `State Machine`

however, a general rule to `ANY` `Turing Machine` implementation is that it `MUST` be `stack free`

* what i mean by `stack free` is that when executing, it must not consume additional `stack space` for `transitions`,
this allows it to recursively enter new states infinitely deep without the risk of `stackoverflow`.

* this is important due to the fact a `Turing Machine MUST NOT` have a `limit` 
that would hinder the result of a computation, this allowing the computation to take infinite time to complete
(eg must allow as much time as required for the computation to be completed)

* an example of this could be the following:
  ```kotlin
  fun odd(n: Int): Boolean =
        if (n == 0) false
        else even(n - 1)
  
  fun even(n: Int): Boolean =
        if (n == 0) true
        else odd(n - 1)

  fun stateMachine.main(args:Array<String>) {
    // :( java.lang.StackOverflowError
    even(99999)
  }
  ```
  * assuming `odd` and `even` were states `1` and `2`, and input of `99999`,
  this would quickly result in a `StackOverflow` during execution
  due to the fact that both states call each other in a nested way, which consumes stack space for each function call
  (as long as the function is active, if the function returns the space is reclaimed from the stack, think of it as
  an automatic garbage collector and each function allocates memory on entry)
* in order to avoid the above case, we must use `Tail Recursion`, combined with a `Trampoline`
  * `Tail Recursion` qualifies when the recursive function calls itself directly:
    ```Kotlin
    fun wasteTime(x: Int) {
       if (x == 0) return
       a(x-1) // return here is optional
    }
    ```
    to make the above function `Tail Recursive`, we add the `tailrec` modifier
    ```Kotlin
    tailrec fun wasteTime(x: Int) {
       if (x == 0) return
       return a(x-1)
    }
    ```
    this `Tail Recursion` would be optimized into an efficient loop, probably the following:
    ```kotlin
    fun wasteTime(x: Int) {
        var tmp = x
        while(tmp != 0) tmp = tmp - 1
    }
    ```
  * a `Trampoline` is a method of implementing a `jump` between two or more points of execution,
    see https://en.wikipedia.org/wiki/Trampoline_(computing)
    also see https://adamschoenemann.dk/posts/2019-02-12-trampolines.html for an `uncompilable` usage in recursion
    (uses a `Sealed Class` in which i do not know of any way to execute the `fib` function without getting
    `errors` (note: the `fib` function refers to functions declared in the `Sealed Class`))

  our specific Trampoline is this
  ```kotlin
  tailrec fun <functionArgumentType, functionReturnType> lamdaTrampoline(
      function: ((functionArgumentType) -> functionReturnType)?,
      argument: functionArgumentType
  ): Any? = when (function) {
      null -> argument
      else -> {
          val r = function(argument)
          @Suppress("UNCHECKED_CAST")
          when (r) {
              !is Pair<*, *> -> r
              else -> lamdaTrampoline(
                  r.first as ((functionArgumentType) -> functionReturnType)?,
                  r.second as functionArgumentType
              )
          }
      }
  }
  ```
  the current limitation of this `Trampoline` implementation is that 
  an infinite number of arguments with different types are not allowed
  and it cannot have its return type automatically determined like a `Lambda` can
  
  how this `Trampoline` works is as follows:
  
  given the following
  ```Kotlin
  val funList = mutableListOf<(i: Int) -> Pair<((i: Int) -> Any?)?, Int?>>()
  val F0: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
      println(it)
      Pair(funList[1], it+1)
  }
  val F1: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
      println(it)
      Pair(funList[2], it+1)
  }
  val F2: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
      println(it)
      Pair(funList[0], it+1) // we could also return Pair(null, it)
  }
  funList.add(F0)
  funList.add(F1)
  funList.add(F2)
  lambdaTrampoline(funList[0], 0)
  ```
  we first declare a `MutableList` that accepts a `Lambda` that accept an argument of type `Int`
  and returns a type of `Pair`
  
  `val funList = mutableListOf<(i: Int) -> Pair<((i: Int) -> Any?)?, Int?>>()`
  
  this `Pair` accepts two arguments
  
  the first is a nullable `Lambda` that accept an argument of type `Int` and returns a type of `Any?`,
  since this is a `nullable Lambda` (`(() -> Unit)?`) it can also accept `null` instead of a `Lamda`

  and the second is an `Int?`,
  since we do not know if the argument could become `null` during execution of the `Trampoline`
  
  next we define out functions that the `Trampoline` shall execute
  ```Kotlin
  val F0: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
      println(it)
      Pair(funList[1], it+1)
  }
  val F1: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
      println(it)
      Pair(funList[2], it+1)
  }
  val F2: (i: Int) -> Pair<((i: Int) -> Any?)?, Int?> = {
      println(it)
      Pair(funList[0], it+1) // we could also return Pair(null, it)
  }
  ```
  for functions `F0`, `F1`, and `F2`
  * these accept an argument of type `Int` and return a type of `Pair`
    
    this `Pair` accepts two arguments
    
    the first is a nullable `Lambda` that accept an argument of type `Int` and returns a type of `Any?`,
    since this is a `nullable Lambda` (`(() -> Unit)?`) it can also accept `null` instead of a `Lamda`
  
    and the second is an `Int?`,
    since we do not know if the argument could become `null` during execution of the `Trampoline`
  
  * they print their current argument and then return a `Pair` that contains the next `function` to `execute`,
    and the `argument` to give that `function`
    
  ```kotlin
  funList.add(F0)
  funList.add(F1)
  funList.add(F2)
  ```
  next we add functions `F0`, `F1`, and `F2` to a list so that the functions can call each other
  without complex ordering to avoid `unresolved reference`'s
  
  `lambdaTrampoline(funList[0], 0)`
  
  finally, we call `lambdaTrampoline` with the function we want to execute, and the argument we want to give it
  
  the Trampoline will do the following:
  * if (`function` argument is `null`) return `argument`
  * else
    * execute `function(argument)` and save its result in the variable `r`
    * if (`r` is not of type `Pair<*, *>`) return r
      
      * `NOTE`
        * a type of `Pair<*, *>` is a `Pair` that accepts `two arguments`, in which the arguments `type` are `ignored`
          when checked
    * else
      ```Kotlin
      return lambdaTrampoline(
          r.first as ((functionArgumentType) -> functionReturnType)?,
          r.second as functionArgumentType
      )
      ```
      *  `NOTE`
         * if the `type casting` is `ommited`
      
           `return lambdaTrampoline(r.first, r.second)`
      
           then it will result in a `Type inference failed` error even if
      
           `return lambdaTrampoline<functionArgumentType, functionReturnType>(r.first, r.second)`
      
           is used
  
  the `Trampoline` is based on https://stackoverflow.com/a/44695092
  ```kotlin
  import kotlin.reflect.KFunction
  
  typealias Result = Pair<KFunction<*>?, Any?>
  typealias Func = KFunction<Result>
  
  tailrec fun trampoline(f: Func, arg: Any?): Any? {
      val (f2,arg2) = f.call(arg)
      @Suppress("UNCHECKED_CAST")
      return if (f2 == null) arg2 
          else trampoline(f2 as Func, arg2)
  }
  
  fun odd(n: Int): Result =
          if (n == 0) null to false
          else ::even to n-1
  
  fun even(n: Int): Result =
          if (n == 0) null to true
          else ::odd to n-1
  
  fun stateMachine.main(args:Array<String>) {
      System.out.println(trampoline(::even, 9999999))
  }
  ```
  
  

examples can be found here
* https://rosettacode.org/wiki/Universal_Turing_machine#Kotlin
* https://rosettacode.org/wiki/Universal_Turing_machine#C

  (for those familiar with C, since most parser libraries are written in C)
  
#
## how to ACTUALLY build a state machine (using this library)
in order to build a state machine we first need to import `stateMachine.StateMachines`
we can do this by adding the following to the top of your file

`import stateMachine.StateMachines`

with this, we import my `StateMachine` class

with the `StateMachine` class imported, we then decide what our machine will do

lets do a `3-State Busy Beaver`

for this we need only accept `0` or `1`, and output nothing so our `Input` will be `Int`

declare a `State Machine` with an `input type` of `Int`

`val TM = StateMachines.TuringMachineInputOnly<Int>()`

and we will enable `debug` output

`TM.debug = true`

for this we will be using https://www.cl.cam.ac.uk/projects/raspberrypi/tutorials/turing-machine/four.html

The busy beaver problem is to find the most number of ‘1’s that a 2-symbol, n-state Turing machine can print on an initially blank tape before halting. The aim of this is to find the smallest program which outputs as much data as possible, and can also stop eventually. Visit this website for more information on the problem.

For a 3-State machine, the maximum number of ‘1’s that it can print is proven to be 6, and it takes 14 steps for the Turing machine to do so. The state table for the program is shown below. Since only 2 symbols are required, the instructions for the ‘0’ symbol are left as the default settings.

```
State Table
State       Symbol Read     Write Instruction   Move Instruction    Next State
State 0     Blank           Write ‘1’           Move tape left      State 1
            0               Write ‘Blank’       None                State 0
            1               Write ‘1’           None                Stop State
State 1     Blank           Write ‘Blank’       Move tape left      State 2
            0               Write ‘Blank’       None                State 0
            1               Write ‘1’           Move tape left      State 1
State 2     Blank           Write ‘1’           Move tape right     State 2
            0               Write ‘Blank’       None                State 0
            1               Write ‘1’           Move tape right     State 0
```

this defines 3 `symbols`: `Blank`, `0`, and `1`

next, since this table uses a `Blank` symbol, we need to set the value of our `Blank` symbol in our `TM`,

since we are using `Int` our `Blank` symbol shall be `-1`

`TM.setInputBlank(-1)`

next we add some input tape

`TM.addInputTape()`

then we initialize our input tape, since it is allowed to be initialized as `Blank` we shall do so

`TM.setInputTape(TM.getInputBlank())`

`getInputBlank()` returns the currently set `Blank` associated with the `input` tape,
this defaults to `null` if not set

next we define our rules

we can do this in two ways:

* using a `ruleBuilder`
  * the function `addRule` that has multiple overloads for different conditions that internally call `ruleBuilder.add`
  * rules are added in any order
  * this dynamically constructs a synthetic `when` expression
  * a rule consists of the following:
    ```kotlin
    inner class Rule(
        State: Int,
        SymbolRead: InputType,
        WriteInstruction: (() -> Unit)?,
        IfNoMatchesInState: (() -> Unit)?,
        MoveInstruction: (() -> Unit)?,
        NextState: Int?,
        IfNoMatchesInStateNextState: Int?
    ) { /* ... */ }
    ```
    where
    * `State` indicates the `state` the rule is to be added in
    * `SymbolRead` indicates what `symbol` this rule should be `evaluated` on when in `State` and the current symbol
     at the `head` of the input type matches
    * `SymbolWrite` (`overload` for 
      ```kotlin
      if (SymbolRead == SymbolWrite)
          null // WriteInstruction
      else
          LambdaWriteInputTape(SymbolWrite) // WriteInstruction
      ```
      ) indicates what symbol to write back to the `input` tape,

       this gets executed this rule is determined to be evaluated
    * `WriteInstruction` takes a Lambda to execute when in `State`
    
      this is intended for writing purposes,
      
      this gets executed this rule is determined to be evaluated
    * `MoveInstruction` takes a Lambda to execute when in `State`
    
      this is intended for head/tape movement purposes,
      
      this gets executed this rule is determined to be evaluated
    * `IfNoMatchesInState` takes a Lambda to execute when in `State`,
      
      this gets executed when no `ReadSymbol` is matched for `all` rules in the `State`
      
      it is currently `Undefined Behavour` to have more than one `IfNoMatchesInState` actions in the same `State`
      due to ambiguity,
      
      for example, multiple rules belonging to the same state may invoke multiple
      state transitions in an `unpredictible` way
    * `NextState` indicates the next state to transition to if this rule is determined to be evaluated,
      `null` results in termination (halting condition)
    * `IfNoMatchesInStateNextState`
       indicates the next state to transition to if no `ReadSymbol` is matched for `all` rules in the `State`,
      `null` results in termination (halting condition)

      it is currently `Undefined Behavour` to have more than one `IfNoMatchesInStateNextState` in the same `State`
      due to ambiguity,
      
      for example, multiple rules belonging to the same state may invoke multiple
      state transitions in an `unpredictible` way
      
    small example:
    ```kotlin
    val TM = StateMachines.TuringMachineInputOnly<String>()
    TM.setInputBlank(" ")
    TM.addRule(0, TM.getInputBlank(), null, TM.LambdaMoveInputTapeLeft, 1)
    TM.addRule(0, "0", "1", TM.LambdaMoveInputTapeRight, 1)
    TM.addRule(0, "1", "0", TM.LambdaMoveInputTapeRight, 0)
    TM.addRule(1, TM.getInputBlank(), null, TM.LMITR)
    TM.addRule(1, "0", "1", TM.LambdaMoveInputTapeLeft, 1)
    TM.addRule(1, "1", "0", TM.LambdaMoveInputTapeLeft, 1)
    ```
* using the `add` function
  * the `add` function accepts a `state id` and a `Lambda`,
  * this allows for maximum control at the cost of simplicity

  small example:
  ```kotlin
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
  ```  
we shall use our `ruleBuilder` to demonstrate its power

```kotlin
TM.addRule(0, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeLeft, 1)
TM.addRule(0, 0, TM.getInputBlank(), 0)
TM.addRule(0, 1, 1)
TM.addRule(1, TM.getInputBlank(), TM.getInputBlank(), TM.LambdaMoveInputTapeLeft, 2)
TM.addRule(1, 0, TM.getInputBlank(), 0)
TM.addRule(1, 1, 1, TM.LambdaMoveInputTapeLeft, 1)
TM.addRule(2, TM.getInputBlank(), 1, TM.LambdaMoveInputTapeRight, 2)
TM.addRule(2, 0, TM.getInputBlank(), 0)
TM.addRule(2, 1, 1, TM.LambdaMoveInputTapeRight, 0)
```

next we build the rule list

`TM.ruleBuilder.build()`

and finally we execute the machine

`TM.executeState(0)`

and optionally print its run stastistics

`TM.statistics.print()`

if all goes well you should see this:
```
set inputBlank to -1
added Input Tape
set Input Tape 0 to [-1]
adding rule: state context: 0, SymbolRead: blank, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 1, IfNoMatchesInStateNextState: null
adding rule: state context: 0, SymbolRead: 0, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: null, NextState: 0, IfNoMatchesInStateNextState: null
adding rule: state context: 0, SymbolRead: 1, WriteInstruction: null, IfNoMatchesInState: null, MoveInstruction: null, NextState: null, IfNoMatchesInStateNextState: null
adding rule: state context: 1, SymbolRead: blank, WriteInstruction: null, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 2, IfNoMatchesInStateNextState: null
adding rule: state context: 1, SymbolRead: 0, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: null, NextState: 0, IfNoMatchesInStateNextState: null
adding rule: state context: 1, SymbolRead: 1, WriteInstruction: null, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 1, IfNoMatchesInStateNextState: null
adding rule: state context: 2, SymbolRead: blank, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 2, IfNoMatchesInStateNextState: null
adding rule: state context: 2, SymbolRead: 0, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: null, NextState: 0, IfNoMatchesInStateNextState: null
adding rule: state context: 2, SymbolRead: 1, WriteInstruction: null, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 0, IfNoMatchesInStateNextState: null
clearing WhenExpression
building all rules
building rule: state context: 0, SymbolRead: blank, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 1, IfNoMatchesInStateNextState: null
building rule: state context: 0, SymbolRead: 0, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: null, NextState: 0, IfNoMatchesInStateNextState: null
building rule: state context: 0, SymbolRead: 1, WriteInstruction: null, IfNoMatchesInState: null, MoveInstruction: null, NextState: null, IfNoMatchesInStateNextState: null
adding built rule to WhenExpression
adding built rule as state 0
adding state 0 to state list
building rule: state context: 1, SymbolRead: blank, WriteInstruction: null, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 2, IfNoMatchesInStateNextState: null
building rule: state context: 1, SymbolRead: 0, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: null, NextState: 0, IfNoMatchesInStateNextState: null
building rule: state context: 1, SymbolRead: 1, WriteInstruction: null, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 1, IfNoMatchesInStateNextState: null
adding built rule to WhenExpression
adding built rule as state 1
adding state 1 to state list
building rule: state context: 2, SymbolRead: blank, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 2, IfNoMatchesInStateNextState: null
building rule: state context: 2, SymbolRead: 0, WriteInstruction: () -> kotlin.Unit, IfNoMatchesInState: null, MoveInstruction: null, NextState: 0, IfNoMatchesInStateNextState: null
building rule: state context: 2, SymbolRead: 1, WriteInstruction: null, IfNoMatchesInState: null, MoveInstruction: () -> kotlin.Unit, NextState: 0, IfNoMatchesInStateNextState: null
adding built rule to WhenExpression
adding built rule as state 2
adding state 2 to state list
setting state to 0
executing state: 0
readInputTape: blank
[blank]
finding WhenExpression with associated state 0
found WhenExpression with associated state 0
executing WhenExpression with associated state 0
writeTape: change from blank to 1
writeTape: change from blank to 1
[1]
move input tape left
1, [blank]
transitioning to new state (0 -> 1)
setting state to 1
readInputTape: blank
1, [blank]
finding WhenExpression with associated state 1
found WhenExpression with associated state 1
executing WhenExpression with associated state 1
move input tape left
1, blank, [blank]
transitioning to new state (1 -> 2)
setting state to 2
readInputTape: blank
1, blank, [blank]
finding WhenExpression with associated state 2
found WhenExpression with associated state 2
executing WhenExpression with associated state 2
writeTape: change from blank to 1
writeTape: change from blank to 1
1, blank, [1]
move input tape right
1, [blank], 1
transitioning to current state (2 -> 2)
readInputTape: blank
1, [blank], 1
finding WhenExpression with associated state 2
found WhenExpression with associated state 2
executing WhenExpression with associated state 2
writeTape: change from blank to 1
writeTape: change from blank to 1
1, [1], 1
move input tape right
[1], 1, 1
transitioning to current state (2 -> 2)
readInputTape: 1
[1], 1, 1
finding WhenExpression with associated state 2
found WhenExpression with associated state 2
executing WhenExpression with associated state 2
move input tape right
[blank], 1, 1, 1
transitioning to new state (2 -> 0)
setting state to 0
readInputTape: blank
[blank], 1, 1, 1
finding WhenExpression with associated state 0
found WhenExpression with associated state 0
executing WhenExpression with associated state 0
writeTape: change from blank to 1
writeTape: change from blank to 1
[1], 1, 1, 1
move input tape left
1, [1], 1, 1
transitioning to new state (0 -> 1)
setting state to 1
readInputTape: 1
1, [1], 1, 1
finding WhenExpression with associated state 1
found WhenExpression with associated state 1
executing WhenExpression with associated state 1
move input tape left
1, 1, [1], 1
transitioning to current state (1 -> 1)
readInputTape: 1
1, 1, [1], 1
finding WhenExpression with associated state 1
found WhenExpression with associated state 1
executing WhenExpression with associated state 1
move input tape left
1, 1, 1, [1]
transitioning to current state (1 -> 1)
readInputTape: 1
1, 1, 1, [1]
finding WhenExpression with associated state 1
found WhenExpression with associated state 1
executing WhenExpression with associated state 1
move input tape left
1, 1, 1, 1, [blank]
transitioning to current state (1 -> 1)
readInputTape: blank
1, 1, 1, 1, [blank]
finding WhenExpression with associated state 1
found WhenExpression with associated state 1
executing WhenExpression with associated state 1
move input tape left
1, 1, 1, 1, blank, [blank]
transitioning to new state (1 -> 2)
setting state to 2
readInputTape: blank
1, 1, 1, 1, blank, [blank]
finding WhenExpression with associated state 2
found WhenExpression with associated state 2
executing WhenExpression with associated state 2
writeTape: change from blank to 1
writeTape: change from blank to 1
1, 1, 1, 1, blank, [1]
move input tape right
1, 1, 1, 1, [blank], 1
transitioning to current state (2 -> 2)
readInputTape: blank
1, 1, 1, 1, [blank], 1
finding WhenExpression with associated state 2
found WhenExpression with associated state 2
executing WhenExpression with associated state 2
writeTape: change from blank to 1
writeTape: change from blank to 1
1, 1, 1, 1, [1], 1
move input tape right
1, 1, 1, [1], 1, 1
transitioning to current state (2 -> 2)
readInputTape: 1
1, 1, 1, [1], 1, 1
finding WhenExpression with associated state 2
found WhenExpression with associated state 2
executing WhenExpression with associated state 2
move input tape right
1, 1, [1], 1, 1, 1
transitioning to new state (2 -> 0)
setting state to 0
readInputTape: 1
1, 1, [1], 1, 1, 1
finding WhenExpression with associated state 0
found WhenExpression with associated state 0
executing WhenExpression with associated state 0
transitioning to internal STOP state (0 -> <internal>)
Statistics:
    states: 3
    debug: true
    rules declared: 9
    rules built: 9
    total state transitions: 14
    input tape: 1, 1, [1], 1, 1, 1
    input tape reads: 14
    input tape writes: 6
    input tape moves (left): 7
    input tape moves (right): 6
    output tape: 
    output tape reads: 0
    output tape writes: 0
    output tape moves (left): 0
    output tape moves (right): 0
```