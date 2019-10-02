import stateMachine.LinkedList
import stateMachine.Stack

/**
 * converts a [String] into a [LinkedList]
 * @see LinkedList.toStringConcat
 * @return the resulting conversion
 */
val String.toLinkedList
    get() = LinkedList(
        { t, ACTION -> t.forEach { ACTION(it) } },  // arrayIterator
        { (it as Char).toString() },                // ACTION
        this
    )

/**
 * converts a [String] into a [Stack]
 * @see Stack.toStringConcat
 * @return the resulting conversion
 */
val String.toStack
    get() = Stack(
        { t, ACTION -> t.forEach { ACTION(it) } },  // arrayIterator
        { (it as Char).toString() },                // ACTION
        this
    )

/**
 * converts a [String] into a [LinkedList]
 * @see LinkedList.toStringConcat
 * @return the resulting conversion
 */
val String?.toLinkedListOrNull get() = this?.toLinkedList

/**
 * converts a [String] into a [Stack]
 * @see Stack.toStringConcat
 * @return the resulting conversion
 */
val String?.toStackOrNull get() = this?.toStack
