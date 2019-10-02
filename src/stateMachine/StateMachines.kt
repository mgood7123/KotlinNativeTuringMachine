package stateMachine
@Suppress("unused")

class StateMachines {
    class Examples {
        class TuringMachine
    }
    class Programs
    open class TuringMachine<InputType, OutputType> {
        inner class Statistics{
            var states = 0
            var rulesDeclared = 0
            var rulesBuilt = 0
            var stateTransitions = 0
            var inputTapeReads = 0
            var outputTapeReads = 0
            var inputTapeWrites = 0
            var outputTapeWrites = 0
            var inputTapeMoveLefts = 0
            var outputTapeMoveLefts = 0
            var inputTapeMoveRights = 0
            var outputTapeMoveRights = 0
            override fun toString(): String {
                return "Statistics:\n" +
                        "    states: $states\n" +
                        "    debug: $debug\n" +
                        "    rules declared: $rulesDeclared\n" +
                        "    rules built: $rulesBuilt\n" +
                        "    total state transitions: $stateTransitions\n" +
                        "    input tape: ${inputTapeToString()}\n" +
                        "    input tape reads: $inputTapeReads\n" +
                        "    input tape writes: $inputTapeWrites\n" +
                        "    input tape moves (left): $inputTapeMoveLefts\n" +
                        "    input tape moves (right): $inputTapeMoveRights\n" +
                        "    output tape: ${outputTapeToString()}\n" +
                        "    output tape reads: $outputTapeReads\n" +
                        "    output tape writes: $outputTapeWrites\n" +
                        "    output tape moves (left): $outputTapeMoveLefts\n" +
                        "    output tape moves (right): $outputTapeMoveRights\n"
            }

            fun print() = println(toString())
        }
        val statistics = Statistics()
        var debug = false
        val state = Classes().State()
        inner class Tape<T> {
            var tape = LinkedList<T>()
            var default = LinkedList<T>()
            var head = 0
        }
        var InputTape = Stack<Tape<InputType>>()
        var OutputTape = Stack<Tape<OutputType>>()
        fun setInputTape(tapeIndex: Int, tape: LinkedList<InputType>) {
            if (debug) println("set Input Tape $tapeIndex to $tape")
            getInputTape(tapeIndex).tape = tape
        }
        fun setInputTape(tapeIndex: Int, tape: LinkedList<InputType>, head: Int) {
            if (debug) println("set Input Tape $tapeIndex to $tape")
            val i = getInputTape(tapeIndex)
            i.tape = tape
            if (debug) println("set head of Input Tape $tapeIndex to $head")
            i.head = head
        }
        fun setInputTapeHead(tapeIndex: Int, head: Int) {
            if (debug) println("set head of Input Tape $tapeIndex to $head")
            getInputTape(tapeIndex).head = head
        }
        fun setOutputTape(tapeIndex: Int, tape: LinkedList<OutputType>) {
            if (debug) println("set Output Tape $tapeIndex to $tape")
            getOutputTape(tapeIndex).tape = tape
        }
        fun setOutputTape(tapeIndex: Int, tape: LinkedList<OutputType>, head: Int) {
            if (debug) println("set Output Tape $tapeIndex to $tape")
            val o = getOutputTape(tapeIndex)
            o.tape = tape
            if (debug) println("set head of Output Tape $tapeIndex to $head")
            o.head = head
        }
        fun setOutputTapeHead(tapeIndex: Int, head: Int) {
            if (debug) println("set head of Output Tape $tapeIndex to $head")
            getOutputTape(tapeIndex).head = head
        }
        fun setInputTape(tape: LinkedList<InputType>) = setInputTape(inputTape, tape)
        fun setInputTape(tape: LinkedList<InputType>, head: Int) = setInputTape(inputTape, tape, head)
        fun setInputTapeHead(head: Int) = setInputTapeHead(inputTape, head)
        fun setOutputTape(tape: LinkedList<OutputType>) = setOutputTape(outputTape, tape)
        fun setOutputTape(tape: LinkedList<OutputType>, head: Int) = setOutputTape(outputTape, tape, head)
        fun setOutputTapeHead(head: Int) = setOutputTapeHead(inputTape, head)
        fun setInputTapeWithIndex(tapeIndex: Int, vararg squares: InputType) = setInputTape(tapeIndex, LinkedList(*squares))
        fun setInputTapeWithIndex(tapeIndex: Int, head: Int, vararg squares: InputType) = setInputTape(tapeIndex, LinkedList(*squares), head)
        fun setInputTapeHeadWithIndex(tapeIndex: Int, head: Int) = setInputTapeHead(tapeIndex, head)
        fun setOutputTapeWithIndex(tapeIndex: Int, vararg squares: OutputType) = setOutputTape(tapeIndex, LinkedList(*squares))
        fun setOutputTapeWithIndex(tapeIndex: Int, head: Int, vararg squares: OutputType) = setOutputTape(tapeIndex, LinkedList(*squares), head)
        fun setOutputTapeHeadWithIndex(tapeIndex: Int, head: Int) = setOutputTapeHead(tapeIndex, head)
        fun setInputTape(vararg squares: InputType) = setInputTapeWithIndex(inputTape, *squares)
        fun setOutputTape(vararg squares: OutputType) = setOutputTapeWithIndex(inputTape, *squares)
        fun setInputTapeDefault(tapeIndex: Int, tape: LinkedList<InputType>) {
            if (debug) println("set default Input Tape $tapeIndex to $tape")
            getInputTape(tapeIndex).default = tape
        }
        fun setOutputTapeDefault(tapeIndex: Int, tape: LinkedList<OutputType>) {
            if (debug) println("set default Output Tape $tapeIndex to $tape")
            getOutputTape(tapeIndex).default = tape
        }
        fun setInputTapeDefault(tape: LinkedList<InputType>) = setInputTapeDefault(inputTape, tape)
        fun setOutputTapeDefault(tape: LinkedList<OutputType>) = setOutputTapeDefault(outputTape, tape)
        fun setInputTapeWithIndexDefault(tapeIndex: Int, vararg squares: InputType) = setInputTapeDefault(tapeIndex, LinkedList(*squares))
        fun setOutputTapeWithIndexDefault(tapeIndex: Int, vararg squares: OutputType) = setOutputTapeDefault(tapeIndex, LinkedList(*squares))
        fun setInputTapeDefault(vararg squares: InputType) = setInputTapeWithIndexDefault(inputTape, *squares)
        fun setOutputTapeDefault(vararg squares: OutputType) = setOutputTapeWithIndexDefault(inputTape, *squares)

        fun addInputTape() = InputTape.push(Tape()).let {
            inputTape = it
            if (debug) println("added Input Tape")
        }
        fun addOutputTape() = OutputTape.push(Tape()).let {
            outputTape = it
            if (debug) println("added Output Tape")
        }
        fun addTape() {
            addInputTape()
            addOutputTape()
        }
        var inputTape = 0
        var outputTape = 0
        private var inputBlank: InputType? = null
        private var outputBlank: OutputType? = null
        private var inputBlankIsSet = true
        private var outputBlankIsSet = true
        var resetAfterExecution = false
        fun setInputBlank(value: InputType) {
            inputBlankIsSet = true
            inputBlank = value
            if (debug) println("set inputBlank to $value")
        }
        fun setOutputBlank(value: OutputType) {
            outputBlankIsSet = true
            outputBlank = value
            if (debug) println("set outputBlank to $value")
        }
        fun setBlank(inputBlank: InputType, outputBlank: OutputType) {
            setInputBlank(inputBlank)
            setOutputBlank(outputBlank)
        }
        fun unsetInputBlank() {
            inputBlankIsSet = false
            inputBlank = null
            if (debug) println("set inputBlank to null")
        }
        fun unsetOutputBlank() {
            outputBlankIsSet = false
            outputBlank = null
            if (debug) println("set outputBlank to null")
        }
        fun getInputBlank() = if (inputBlankIsSet) inputBlank as InputType else throw UninitializedPropertyAccessException("inputBlank")
        fun getOutputBlank() = if (outputBlankIsSet) outputBlank as OutputType else throw UninitializedPropertyAccessException("outputBlank")
        var Empty = null
        fun hasInputTape() = !InputTape.isEmpty()
        fun hasOutputTape() = !OutputTape.isEmpty()
        fun hasInputTape(tapeIndex: Int) = tapeIndex < InputTape.size
        fun hasOutputTape(tapeIndex: Int) = tapeIndex < OutputTape.size
        fun getInputTape(tapeIndex: Int) = InputTape[tapeIndex]
        fun getOutputTape(tapeIndex: Int) = OutputTape[tapeIndex]
        fun getInputTape() = getInputTape(inputTape)
        fun getOutputTape() = getOutputTape(outputTape)
        fun InputTapeToString(tapeIndex: Int) =
            if (hasInputTape(tapeIndex)) {
                var s = ""
                val TAPE = getInputTape(tapeIndex)
                var node = TAPE.tape.first()
                var headPrinted = false
                while (node != null) {
                    if (!headPrinted) if (TAPE.tape.nodeAtIndex(TAPE.head) == node) s += "["
                    s += valueInputToString(node.value)
                    if (!headPrinted) if (TAPE.tape.nodeAtIndex(TAPE.head) == node) {
                        s += "]"
                        headPrinted = true
                    }
                    node = node.next
                    if (node != null) {
                        s += ", "
                    }
                }
                s
            } else ""
        fun OutputTapeToString(tapeIndex: Int) =
            if (hasOutputTape(tapeIndex)) {
                var s = ""
                val TAPE = getOutputTape(tapeIndex)
                var node = TAPE.tape.first()
                var headPrinted = false
                while (node != null) {
                    if (!headPrinted) if (TAPE.tape.nodeAtIndex(TAPE.head) == node) s += "["
                    s += valueOutputToString(node.value)
                    if (!headPrinted) {
                        if (TAPE.tape.nodeAtIndex(TAPE.head) == node) s += "]"
                        headPrinted = true
                    }
                    node = node.next
                    if (node != null) {
                        s += ", "
                    }
                }
                s
            } else ""
        fun inputTapeToString() = InputTapeToString(inputTape)
        fun outputTapeToString() = OutputTapeToString(outputTape)
        fun tapeHasInput() = hasInputTape(inputTape)
        fun tapeHasOutput() = hasOutputTape(outputTape)

        fun valueInputToString(value: InputType) = when(value) {
            null -> value.toString()
            getInputBlank() -> "blank"
            else -> value.toString()
        }
        fun valueOutputToString(value: OutputType) = when(value) {
            null -> value.toString()
            getOutputBlank() -> "blank"
            else -> value.toString()
        }

        @Throws(IndexOutOfBoundsException::class)
        fun readInputTape() =
            if (tapeHasInput()) {
                val TAPE = getInputTape()
                val node = TAPE.tape.nodeAtIndex(TAPE.head)
                    ?: throw IndexOutOfBoundsException(
                        "getInputTape().head is an invalid value: ${TAPE.head}, tape size: ${TAPE.tape.count()}"
                    )
                val v = node.value
                if (debug) {
                    println("readInputTape: ${valueInputToString(v)}")
                    println(inputTapeToString())
                }
                statistics.inputTapeReads++
                v
            } else {
                if (debug) println("error: no input tape found")
                Empty
            }
        @Throws(IndexOutOfBoundsException::class)
        fun readOutputTape() =
            if (tapeHasOutput()) {
                val TAPE = getOutputTape()
                val node = TAPE.tape.nodeAtIndex(TAPE.head)
                    ?: throw IndexOutOfBoundsException(
                        "getOutputTape().head is an invalid value: ${TAPE.head}, tape size: ${TAPE.tape.count()}"
                    )
                val v = node.value
                if (debug) {
                    println("readOutputTape: ${valueOutputToString(v)}")
                    println(outputTapeToString())
                }
                statistics.outputTapeReads++
                v
            } else {
                if (debug) println("error: no output tape found")
                Empty
            }

        fun writeInputTape(value: InputType) =
            if (tapeHasInput()) {
                val TAPE = getInputTape()
                val v = TAPE.tape.nodeAtIndex(TAPE.head)
                    ?: throw IndexOutOfBoundsException(
                        "getInputTape().head is an invalid value: ${TAPE.head}, tape size: ${TAPE.tape.count()}"
                    )
                if (debug) println("writeTape: change from ${valueInputToString(v.value)} to ${valueInputToString(value)}")
                if (v.value != value) {
                    if (debug) println("writeTape: change from ${valueInputToString(v.value)} to ${valueInputToString(value)}")
                    v.value = value
                    statistics.inputTapeWrites++
                } else if (debug) println("writeTape: no change")
                if (debug) println(inputTapeToString())
                true
            } else {
                if (debug) println("error: no Input tape found")
                false
            }
        fun writeOutputTape(value: OutputType) =
            if (tapeHasOutput()) {
                val TAPE = getOutputTape()
                val v = TAPE.tape.nodeAtIndex(TAPE.head)
                    ?: throw IndexOutOfBoundsException(
                        "getOutputTape().head is an invalid value: ${TAPE.head}, tape size: ${TAPE.tape.count()}"
                    )
                if (v.value != value) {
                    if (debug) println("writeTape: change from ${valueOutputToString(v.value)} to ${valueOutputToString(value)}")
                    v.value = value
                    statistics.outputTapeWrites++
                } else if (debug) println("writeTape: no change")
                if (debug) println(outputTapeToString())
                true
            } else {
                if (debug) println("error: no Output tape found")
                false
            }

        fun moveInputTapeRight(tapeIndex: Int) =
            if (hasInputTape(tapeIndex)) {
                val TAPE = getInputTape()
                if (TAPE.tape.isFirst(TAPE.head)) {
                    TAPE.tape.appendFirst(getInputBlank())
                } else TAPE.head--
                if (debug) {
                    println("move input tape right")
                    println(inputTapeToString())
                }
                statistics.inputTapeMoveRights++
                true
            } else false
        fun moveOutputTapeRight(tapeIndex: Int) =
            if (hasOutputTape(tapeIndex)) {
                val TAPE = getOutputTape()
                if (TAPE.tape.isFirst(TAPE.head)) {
                    TAPE.tape.appendFirst(getOutputBlank())
                } else TAPE.head--
                if (debug) {
                    println("move output tape right")
                    println(outputTapeToString())
                }
                statistics.outputTapeMoveRights++
                true
            } else false

        fun moveInputTapeLeft(tapeIndex: Int) =
            if (hasInputTape(tapeIndex)) {
                val TAPE = getInputTape()
                if (TAPE.tape.isLast(TAPE.head)) {
                    TAPE.tape.appendLast(getInputBlank())
                }
                TAPE.head++
                if (debug) {
                    println("move input tape left")
                    println(inputTapeToString())
                }
                statistics.inputTapeMoveLefts++
                true
            } else false
        fun moveOutputTapeLeft(tapeIndex: Int) =
            if (hasOutputTape(tapeIndex)) {
                val TAPE = getOutputTape()
                if (TAPE.tape.isLast(TAPE.head)) {
                    TAPE.tape.appendLast(getOutputBlank())
                }
                TAPE.head++
                if (debug) {
                    println("move output tape left")
                    println(outputTapeToString())
                }
                statistics.outputTapeMoveLefts++
                true
            } else false

        fun moveInputTapeRight() = if (moveInputTapeRight(inputTape)) true else {
            if (debug) println("error: no input tape")
            false
        }
        fun moveInputTapeLeft() = if (moveInputTapeLeft(inputTape)) true else {
            if (debug) println("error: no input tape")
            false
        }

        fun moveOutputTapeRight() = if (moveOutputTapeRight(outputTape)) true else {
            if (debug) println("error: no output tape")
            false
        }

        fun moveOutputTapeLeft() = if (moveOutputTapeLeft(outputTape)) true else {
            if (debug) println("error: no output tape")
            false
        }

        fun LambdaMoveInputTapeLeft(tapeIndex: Int): () -> Unit = { moveInputTapeLeft(tapeIndex) }
        fun LMITL(tapeIndex: Int) = LambdaMoveInputTapeLeft(tapeIndex)
        fun LambdaMoveInputTapeRight(tapeIndex: Int): () -> Unit = { moveInputTapeRight(tapeIndex) }
        fun LMITR(tapeIndex: Int) = LambdaMoveInputTapeRight(tapeIndex)
        fun LambdaMoveOutputTapeLeft(tapeIndex: Int): () -> Unit = { moveOutputTapeLeft(tapeIndex) }
        fun LMOTL(tapeIndex: Int) = LambdaMoveOutputTapeLeft(tapeIndex)
        fun LambdaMoveOutputTapeRight(tapeIndex: Int): () -> Unit = { moveOutputTapeRight(tapeIndex) }
        fun LMOTR(tapeIndex: Int) = LambdaMoveOutputTapeRight(tapeIndex)
        val LambdaMoveInputTapeLeft = LambdaMoveInputTapeLeft(inputTape)
        val LMITL = LMITL(inputTape)
        val LambdaMoveInputTapeRight = LambdaMoveInputTapeRight(inputTape)
        val LMITR = LMITR(inputTape)
        val LambdaMoveOutputTapeLeft = LambdaMoveOutputTapeLeft(outputTape)
        val LMOTL = LMOTL(outputTape)
        val LambdaMoveOutputTapeRight = LambdaMoveOutputTapeRight(outputTape)
        val LMOTR = LMOTR(outputTape)
        fun LambdaWriteInputTape(value: InputType): () -> Unit = { writeInputTape(value) }
        fun LWIT(value: InputType) = LambdaWriteInputTape(value)
        fun LambdaWriteOutputTape(value: OutputType): () -> Unit = { writeOutputTape(value) }
        fun LWOT(value: OutputType) = LambdaWriteOutputTape(value)

        inner class Classes {
            inner class State {
                inner class StateList(s: Int, a: (state: State) -> Any) {
                    val state: Int = s
                    val action: (state: State) -> Any = a
                }

                val stateList = mutableListOf<StateList>()
                private var defaultState: Int = -1

                private var state: Int = defaultState

                fun currentState() = state
                fun defaultState() = defaultState

                fun add(state: Int, action: (state: State) -> Any) = stateList.add(StateList(state, action))
                    .also {
                        if (debug) println("adding state $state to state list")
                        statistics.states++
                    }

                fun get(targetState: Int) = stateList.find { it.state == targetState }

                @Throws(IndexOutOfBoundsException::class)
                private fun throwState(targetState: Int): Nothing = when {
                    ruleBuilder.hasState(targetState) -> throw IndexOutOfBoundsException(
                        "requested state $targetState could not be found in stateList, however " +
                                "a rule for state $targetState exists that is not built, " +
                                "consider building it with ruleBuilder.build() ."
                    )
                    else -> throw IndexOutOfBoundsException("requested state $targetState could not be found.")
                }

                @Throws(IndexOutOfBoundsException::class)
                fun setDefaultState(targetState: Int): StateList {
                    val S = get(targetState)
                    if (S == null) throwState(targetState)
                    if (debug) println("setting default state to $targetState")
                    defaultState = targetState
                    return S
                }

                @Throws(IndexOutOfBoundsException::class)
                private fun setState(targetState: Int): StateList {
                    val S = get(targetState)
                    if (S == null) throwState(targetState)
                    if (debug) println("setting state to $targetState")
                    state = targetState
                    return S
                }

                fun erase() {
                    if (debug) println("erasing state")
                    stateList.clear()
                    state = -1
                    defaultState = -1
                }

                fun transitionState(targetState: Int?): Pair<((State) -> Any)?, State> =
                    when (targetState) {
                        null -> {
                            if (debug) println("transitioning to internal STOP state ($state -> <internal>)")
                            Pair(null, this)
                        }
                        state -> {
                            if (debug) println("transitioning to current state ($state -> $state)")
                            Pair(stateList[state].action, this)
                        }
                        else -> {
                            if (debug) println("transitioning to new state ($state -> $targetState)")
                            Pair(setState(targetState)?.action, this)
                        }
                    }.also {
                        statistics.stateTransitions++
                    }

                fun transitionCurrent(): Pair<((State) -> Any)?, State> {
                    if (debug) println("transitioning to current state ($state -> $state)")
                    return Pair(stateList[state].action, this).also {
                        statistics.stateTransitions++
                    }
                }

                fun transitionDefault(): Pair<((State) -> Any)?, State> {
                    if (debug) println("transitioning to default state ($state -> $defaultState)")
                    return Pair(setState(defaultState)?.action, this).also {
                        statistics.stateTransitions++
                    }
                }
                fun transitionStop(): Pair<((State) -> Any)?, State> {
                    if (debug) println("transitioning to internal STOP state ($state -> <internal>)")
                    return Pair(null, this).also {
                        statistics.stateTransitions++
                    }
                }
                fun executeState(targetState: Int): Any? {
                    val exe = setState(targetState)
                    if (debug) println("executing state: $targetState")
                    val L = lambdaTrampoline(exe.action, this)
                    if (resetAfterExecution) reset()
                    return L
                }
                fun executeDefault(): Any? {
                    val exe = setState(defaultState)
                    if (debug) println("executing default state")
                    val L = lambdaTrampoline(exe.action, this)
                    if (resetAfterExecution) reset()
                    return L
                }
                fun executeCurrent(): Any? {
                    val exe = setState(state)
                    if (debug) println("executing state: $state")
                    val L = lambdaTrampoline(exe.action, this)
                    if (resetAfterExecution) reset()
                    return L
                }
                fun reset() {
                    if (debug) println("resetting input")
                    InputTape.forEachIndexed { index, tape ->
                        if (debug) println("setting input tape (located at InputTape[$index] head to 0")
                        tape.head = 0
                        if (debug) println("setting input tape (located at InputTape[$index] tape to default")
                        tape.tape = tape.default
                    }
                    if (debug) println("resetting input")
                    OutputTape.forEachIndexed { index, tape ->
                        if (debug) println("setting output tape (located at OutputTape[$index] head to 0")
                        tape.head = 0
                        if (debug) println("setting output tape (located at OutputTape[$index] tape to default")
                        tape.tape = tape.default
                    }
                }
            }
        }

        fun transition(targetState: Int?) = state.transitionState(targetState)
        fun transitionCurrent() = state.transitionCurrent()
        fun transitionDefault() = state.transitionDefault()
        fun transitionStop() = state.transitionStop()
        fun executeState(targetState: Int) = state.executeState(targetState)
        fun executeDefault() = state.executeDefault()
        fun executeCurrent() = state.executeCurrent()
        fun reset() = state.reset()
        fun add(State: Int, Action: (state: Classes.State) -> Any) = state.add(State, Action)
        fun defaultState() = state.defaultState()
        fun currentState() = state.currentState()

        data class WHENEXPR<InputType, OutputType>(
            var condition: InputType,
            var action: () -> Pair<((TuringMachine<InputType, OutputType>.Classes.State) -> Any)?, TuringMachine<InputType, OutputType>.Classes.State>,
            var Else: () -> Pair<((TuringMachine<InputType, OutputType>.Classes.State) -> Any)?, TuringMachine<InputType, OutputType>.Classes.State>
        )
        data class WHEN<InputType, OutputType>(var assosiatedState: Int, var expression: Stack<WHENEXPR<InputType, OutputType>>)
        val WhenExpression = Stack<WHEN<InputType, OutputType>>()

        inner class RuleBuilder() {
            fun hasState(targetState: Int): Boolean {
                rules.forEach {
                    if (it.State == targetState) return true
                }
                return false
            }
            inner class Rule(
                State: Int,
                SymbolRead: InputType,
                WriteInstruction: (() -> Unit)?,
                IfNoMatchesInState: (() -> Unit)?,
                MoveInstruction: (() -> Unit)?,
                NextState: Int?,
                IfNoMatchesInStateNextState: Int?
            ) {
                var State: Int = State
                var SymbolRead: InputType = SymbolRead
                var WriteInstruction: (() -> Unit)? = WriteInstruction
                var IfNoMatchesInState: (() -> Unit)? = IfNoMatchesInState
                var MoveInstruction: (() -> Unit)? = MoveInstruction
                var NextState: Int? = NextState
                var IfNoMatchesInStateNextState: Int? = IfNoMatchesInStateNextState
            }
            val rules = LinkedList<Rule>()
            fun add(
                State: Int,
                SymbolRead: InputType,
                WriteInstruction: (() -> Unit)?,
                IfNoMatchesInState: (() -> Unit)?,
                MoveInstruction: (() -> Unit)?,
                NextState: Int?,
                IfNoMatchesInStateNextState: Int?
            ) {
                if (debug) println("adding rule: state context: $State, SymbolRead: ${valueInputToString(SymbolRead)}, WriteInstruction: $WriteInstruction, IfNoMatchesInState: $IfNoMatchesInState, MoveInstruction: $MoveInstruction, NextState: $NextState, IfNoMatchesInStateNextState: $IfNoMatchesInStateNextState")
                rules.appendLast(Rule(State, SymbolRead, WriteInstruction, IfNoMatchesInState, MoveInstruction, NextState, IfNoMatchesInStateNextState))
                statistics.rulesDeclared++
            }
            @Throws(UninitializedPropertyAccessException::class)
            fun buildingEmpty(): Nothing = throw UninitializedPropertyAccessException("attempted to build an empty rule list, please add a rule before building")
            fun buildInit(): List<List<Rule>> {
                if (rules.isEmpty()) buildingEmpty()
                // build rules from lowest state to highest state
                val sortedRules = rules.sortedBy { it.State }
                return sortedRules.groupBy { it.State }.values.toList()
            }
            fun getRulesByStateId(id: Int): List<Rule>? {
                if (hasState(id)) buildInit().forEachIndexed { index, it ->
                    if (it[0].State == id) return it
                }
                return null
            }
            fun build(ruleList: List<Rule>?) {
                if (ruleList == null) throw NullPointerException("ruleList is null")
                if (ruleList.isEmpty()) buildingEmpty()
                val W = WHEN<InputType, OutputType>(ruleList[0].State, Stack())
                ruleList.forEach {
                    if (debug) println("building rule: state context: ${it.State}, SymbolRead: ${valueInputToString(it.SymbolRead)}, WriteInstruction: ${it.WriteInstruction}, IfNoMatchesInState: ${it.IfNoMatchesInState}, MoveInstruction: ${it.MoveInstruction}, NextState: ${it.NextState}, IfNoMatchesInStateNextState: ${it.IfNoMatchesInStateNextState}")
                    W.expression.push(
                        WHENEXPR(
                            condition = it.SymbolRead,
                            action = {
                                it.WriteInstruction?.invoke()
                                it.MoveInstruction?.invoke()
                                transition(it.NextState)
                            },
                            Else = {
                                it.IfNoMatchesInState?.invoke()
                                if (it.IfNoMatchesInStateNextState != null)
                                    transition(it.IfNoMatchesInStateNextState)
                                else transitionStop()
                            }
                        )
                    )
                    statistics.rulesBuilt++
                }
                if (debug) println("adding built rule to WhenExpression")
                WhenExpression.push(W)
                if (debug) println("adding built rule as state ${W.assosiatedState}")
                state.add(W.assosiatedState) {
                    val input = readInputTape()
                    if (debug) println("finding WhenExpression with associated state ${it.currentState()}")
                    val expression = WhenExpression.find { expr -> expr.assosiatedState == it.currentState() }
                    if (expression == null) throw NoSuchElementException(
                        "no expression found with associated state: ${it.currentState()}"
                    )
                    if (debug) println("found WhenExpression with associated state ${it.currentState()}")
                    var result: Pair<((Classes.State) -> Any)?, Classes.State>? = null
                    if (debug) println("executing WhenExpression with associated state ${it.currentState()}")
                    expression.expression.forEach FOREACH@ {
                        if (input == it.condition) {
                            result = it.action()
                            return@FOREACH
                        }
                    }
                    result ?: expression.expression[0].Else()
                }
            }

            fun build(ruleByStateId: Int) = build(getRulesByStateId(ruleByStateId))
            fun build() {
                if (debug) println("clearing WhenExpression")
                WhenExpression.clear()
                if (debug) println("building all rules")
                buildInit().forEach { build(it) }
            }
        }
        val ruleBuilder = RuleBuilder()
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            WriteInstruction: (() -> Unit)?,
            IfNoMatchesInState: (() -> Unit)?,
            MoveInstruction: (() -> Unit)?,
            NextState: Int?,
            IfNoMatchesInStateNextState: Int?
        ) = ruleBuilder.add(State, SymbolRead, WriteInstruction, IfNoMatchesInState, MoveInstruction, NextState, IfNoMatchesInStateNextState)
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            WriteInstruction: (() -> Unit)?,
            IfNoMatchesInState: (() -> Unit)?,
            MoveInstruction: (() -> Unit)?,
            NextState: Int?
        ) = addRule(State, SymbolRead, WriteInstruction, IfNoMatchesInState, MoveInstruction, NextState, null)
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            WriteInstruction: (() -> Unit)?,
            MoveInstruction: (() -> Unit)?,
            NextState: Int?
        ) = addRule(State, SymbolRead, WriteInstruction, null, MoveInstruction, NextState)
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            WriteInstruction: (() -> Unit)?,
            MoveInstruction: (() -> Unit)?,
            NextState: Int?,
            IfNoMatchesInStateNextState: Int?
        ) = addRule(State, SymbolRead, WriteInstruction, null, MoveInstruction, NextState, IfNoMatchesInStateNextState)
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            WriteInstruction: (() -> Unit)?,
            MoveInstruction: (() -> Unit)?
        ) = addRule(State, SymbolRead, WriteInstruction, MoveInstruction, null)
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            WriteInstruction: (() -> Unit)?
        ) = addRule(State, SymbolRead, WriteInstruction, null)
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            SymbolWrite: InputType,
            MoveInstruction: (() -> Unit)?,
            NextState: Int?
        ) = addRule(
            State,
            SymbolRead,
            // skip writing if both read and write are the same
            if (SymbolRead == SymbolWrite)
                null
            else
                LambdaWriteInputTape(SymbolWrite),
            MoveInstruction,
            NextState
        )
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            SymbolWrite: InputType,
            MoveInstruction: (() -> Unit)?
        ) = addRule(State, SymbolRead, SymbolWrite, MoveInstruction, null)
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            SymbolWrite: InputType,
            NextState: Int?
        ) = addRule(State, SymbolRead, SymbolWrite, null, NextState)
        fun addRule(
            State: Int,
            SymbolRead: InputType,
            SymbolWrite: InputType
        ) = addRule(State, SymbolRead, SymbolWrite, NextState = null) // ambiguity: MoveInstruction or NextState
        fun addRule(
            State: Int,
            SymbolRead: InputType
        ) = addRule(State, SymbolRead, null)
    }
    class TuringMachineInputOnly<InputType> : TuringMachine<InputType, Nothing>()
    class TuringMachineOutputOnly<OutputType> : TuringMachine<Nothing, OutputType>()
}