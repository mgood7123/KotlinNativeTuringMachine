package stateMachine

// Mathematical Terms

/*
Linear Algebra is primarily the study of vector spaces

Vector space
    A collection of vectors, where vectors are objects that may be added together and
    multiplied by scalars

    Euclidean vectors are an example of a vector space, typically used to represent
    displacements, as well as physical quantities such as force or momentum

Dimensions of a vector space
    The number of coordinates required to specify any point within the space
 */

/*
Matrix
    A rectangular arrangement of numbers, symbols, or expressions organized in rows
    and columns

    A matrix having R rows and C columns is said to have size R x C

    Matrices provide a useful way of representing linear transformations from one
    vector space to another

Element
    An individual member of the rectangular arrangement comprising the matrix

    Rows are traditionally indexed from 1 to R, and columns from 1 to C

    In matrix A, element a11 appears in the upper left-hand corner, while element
    aRC appears in the lower right-hand corner
 */

/*
Row vector
    A matrix containing a single row - a matrix of size 1 x C

    The rows of the matrix are sometimes called row vectors

Column vector
    A matrix containing a single column - a matrix of size R x 1

    The columns of the matrix are sometimes called column vectors

NOTE: We do not distinguish between Row vectors and Column vectors
      which themselves are matrices that represent a single column
      of another matrix

Rank (of a matrix)
    The dimension of the vector space spanned by its rows/columns
    for example, if you have a matrix of 3 x 3 it would be Rank 3
    if you have a matrix of 3 x 4 it would be Rank 3

    Also equal to the maximum of linearly-independent rows/columns

 */

/*
Element transforms
    Non-arithmetic operations that allow modifying the relative positions of elements in a
    matrix, such as transpose, column exchange, and row exchange

Element arithmetic
    Arithmetical operations that read or modify the values of individual elements
    independently of other elements

Matrix arithmetic
    Assignment, addition, subtraction, negation, and multiplication operations
    defined for matrices and vectors as wholes
 */

/*
Decompositions
    Complex sequences of arithmetic operations, element arithmetic, and element
    transforms performed upon a matrix to determine the important mathematical
    properties of that matrix

Eigen-decompositions
    Sequences of operations performed upon a symmetric matrix in order to compute
    the eigenvalues and eigenvectors of that matrix

 */

// Terms regarding Types

/*
Math object
    Generically, one of the types matrix or vector described here

Storage
    A synonym for memory

Dense
    A math object representation with storage allocated for every element

Sparse
    A math object representation with storage allocated only for non-zero elements
 */

/*
Engines are implementation types that manage the resources associated with a math object
    Element storage ownership and lifetime

    Access to individual elements

    Resizing/reserving, if appropriate

    Execution context

In this interface design, an engine object is a private member of a containing
math object, ei a matrix object contains a matrix engine

Other than as a template parameter, engines are not part of a math object's
public interface
 */

/*
Traits
    A (usually) stateless class or class template whose members provide an interface
    normally over some set of types or template parameters

    Often appear as parameters in class/function templates

Row capacity / column capacity
    The maximum number of rows/columns that t    The process of determining the resulting element type is element promotion
he math object could possibly have

Row size / column size
    The number of rows/columns that the math object actually has
    Must be less than or equal to the corresponding row/column capacities
 */

/*
Fixed-size
    An engine type whose row/column sizes are fixed and known at compile time

Fixed-capacity
    An engine type whose row/column capacities are fixed and known at compile time

Dynamically re-sizable
    An engine type whose row/column sizes/capacities are set at run time
 */

/*
In programming, dimension refers to the number of indices required to
access an element of an array

In linear algebra, a vector space V is n-dimensional if there exists n
linearly independent vectors that span V

We use dimension both ways
    A vector describing a point in an electric field is a one-dimensional data structure
    implemented as a three-dimensional vector

    A rotation matrix used by a game engine is two-dimensional data structure
    composed of three-dimensional row and column vectors
 */

// Design Aspects

/*
Capacity and resizability
    In some problem domains, it is useful for a math object to have excess storage
    capacity, so that resizes fo not require reallocations

    In other problem domains (like graphics) math objects are small and never resize

Expressions with mixed element types
    In general, when multiple primitive types are present in a arithmetic expression,
    the resulting type is the "largest" of all the types

    Information should be preserved

    The process of determining the resulting element type is element promotion

Expressions with mixed engine types
    Consider fixed-size matrix multiplied by a dynamically-resizable matrix

    The resulting engine should be at least as "general" as the "most general" of all
    the engine types participating in the expression

    Determining the resulting engine type is called engine promotion
 */

open class TypeChecker() {
    fun bothByte(value1: Any, value2: Any) = value1 is Byte && value2 is Byte
    fun bothShort(value1: Any, value2: Any) = value1 is Short && value2 is Short
    fun bothInt(value1: Any, value2: Any) = value1 is Int && value2 is Int
    fun bothLong(value1: Any, value2: Any) = value1 is Long && value2 is Long
    fun bothFloat(value1: Any, value2: Any) = value1 is Float && value2 is Float
    fun bothDouble(value1: Any, value2: Any) = value1 is Double && value2 is Double
    fun typesDiffer(value1: Any, value2: Any) = !(
            bothByte(value1, value2) ||
                    bothShort(value1, value2) ||
                    bothInt(value1, value2) ||
                    bothLong(value1, value2) ||
                    bothFloat(value1, value2) ||
                    bothDouble(value1, value2)
            )
    fun isNumber(value: Any) = value is Byte || value is Short || value is Int || value is Long
    fun isFloatingPoint(value: Any) = value is Float || value is Double
    fun isNumberOrFloatingPoint(value: Any) = isNumber(value) || isFloatingPoint(value)
    fun bothValid(value1: Any, value2: Any) = isNumberOrFloatingPoint(value1) && isNumberOrFloatingPoint(value2)
    fun promoteToLargestNumber(value1: Any, value2: Any) = when (value1) {
        is Byte -> {
            when (value2) {
                is Short -> Pair(value1.toShort(), value2)
                is Int -> Pair(value1.toInt(), value2)
                is Long -> Pair(value1.toLong(), value2)
                else -> throw TypeCastException("type promotion error: Unsupported type")
            }
        }
        is Short -> {
            when (value2) {
                is Byte -> Pair(value1, value2.toShort())
                is Int -> Pair(value1.toInt(), value2)
                is Long -> Pair(value1.toLong(), value2)
                else -> throw TypeCastException("type promotion error: Unsupported type")
            }
        }
        is Int -> {
            when (value2) {
                is Byte -> Pair(value1, value2.toInt())
                is Short -> Pair(value1, value2.toInt())
                is Long -> Pair(value1.toLong(), value2)
                else -> throw TypeCastException("type promotion error: Unsupported type")
            }
        }
        is Long -> {
            when (value2) {
                is Byte -> Pair(value1, value2.toLong())
                is Short -> Pair(value1, value2.toLong())
                is Int -> Pair(value1, value2.toLong())
                else -> throw TypeCastException("type promotion error: Unsupported type")
            }
        }
        else -> throw TypeCastException("type promotion error: Unsupported type")
    }
    fun promoteToFloat(value: Any) = when(value) {
        is Byte -> value.toFloat()
        is Short -> value.toFloat()
        is Int -> value.toFloat()
        is Long -> value.toFloat()
        is Float -> value
        is Double -> value
        else -> throw TypeCastException("type promotion error: Unsupported type")
    }
    fun promoteToLargestFloat(value1: Any, value2: Any): Pair<Any, Any> {
        val v1 = promoteToFloat(value1)
        val v2 = promoteToFloat(value2)
        return when (v1) {
            is Float -> {
                when (v2) {
                    is Float -> Pair(v1, v2)
                    is Double -> Pair(v1.toDouble(), v2)
                    else -> throw TypeCastException("type promotion error: Unsupported type")
                }
            }
            is Double -> {
                when (v2) {
                    is Float -> Pair(v1, v2.toDouble())
                    is Double -> Pair(v1, v2)
                    else -> throw TypeCastException("type promotion error: Unsupported type")
                }
            }
            else -> throw TypeCastException("type promotion error: Unsupported type")
        }
    }
    // TODO
    fun defaultFromType(value1: Any) = when {
        isNumber(value1) -> 0
        else -> when(value1) {
            is Float -> 0F
            is Double -> 0.0
            else -> throw TypeCastException("type promotion error: Unsupported type")
        }
    }
    fun <Type> defaultFromType(value1: Type, setter: (value: Type) -> Unit) = when(value1) {
        is Byte -> (setter as (value: Byte) -> Unit)(0)
        else -> when(value1) {
            is Float -> 0F
            is Double -> 0.0
            else -> throw TypeCastException("type promotion error: Unsupported type")
        }
    }
}

class Operator() : TypeChecker() {
    val operationAddition: Short = 1
    val operationSubtraction: Short = 2
    val operationMultiplication: Short = 3
    val operationDivision: Short = 4
    var allowAutomaticTypePromotion = false

    private val unsupportedOperationString = "operation: Unsupported operation: only Addition, Subtraction, Multiplication, and Division operations are available"
    private val unsupportedTypeString = "operation: Unsupported type"

    fun detectOverflow(operation: Short, value1: Any, value2: Any): Boolean = when (value1) {
        // assume both values to be the same type
        is Byte -> {
            val a = value1 as Byte
            val b = value2 as Byte
            val max = Byte.MAX_VALUE
            when (operation) {
                operationAddition -> a > 0 && max - a < b
                operationSubtraction -> b < 0 && max + b < a
                operationMultiplication -> {
                    if (a < b) detectOverflow(operation, b, a)
                    else {
                        var sum: Byte = 0
                        var i: Byte = 0
                        var overflow = false
                        val absOfB = if (b<0) (-b).toByte() else b
                        while(i < absOfB) {
                            if (detectOverflow(operationAddition, sum, a)) {
                                overflow = true
                                break
                            }
                            sum = (sum + a).toByte()
                            i++
                        }
                        overflow
                    }
                }
                else -> false
            }
        }
        is Short -> {
            val a = value1 as Short
            val b = value2 as Short
            val max = Short.MAX_VALUE
            when (operation) {
                operationAddition -> a > 0 && max - a < b
                operationSubtraction -> b < 0 && max + b < a
                operationMultiplication -> {
                    if (a < b) detectOverflow(operation, b, a)
                    else {
                        var sum: Short = 0
                        var i: Short = 0
                        var overflow = false
                        val absOfB = if (b<0) (-b).toShort() else b
                        while(i < absOfB) {
                            if (detectOverflow(operationAddition, sum, a)) {
                                overflow = true
                                break
                            }
                            sum = (sum + a).toShort()
                            i++
                        }
                        overflow
                    }
                }
                else -> false
            }
        }
        is Int -> {
            val a = value1 as Int
            val b = value2 as Int
            val max = Int.MAX_VALUE
            when (operation) {
                operationAddition -> a > 0 && max - a < b
                operationSubtraction -> b < 0 && max + b < a
                operationMultiplication -> {
                    if (a < b) detectOverflow(operation, b, a)
                    else {
                        var sum: Int = 0
                        var i: Int = 0
                        var overflow = false
                        val absOfB = if (b<0) (-b).toInt() else b
                        while(i < absOfB) {
                            if (detectOverflow(operationAddition, sum, a)) {
                                overflow = true
                                break
                            }
                            sum = (sum + a).toInt()
                            i++
                        }
                        overflow
                    }
                }
                else -> false
            }
        }
        is Long -> {
            val a = value1 as Long
            val b = value2 as Long
            val max = Long.MAX_VALUE
            when (operation) {
                operationAddition -> a > 0 && max - a < b
                operationSubtraction -> b < 0 && max + b < a
                operationMultiplication -> {
                    if (a < b) detectOverflow(operation, b, a)
                    else {
                        var sum: Long = 0
                        var i: Long = 0
                        var overflow = false
                        val absOfB = if (b<0) (-b).toLong() else b
                        while(i < absOfB) {
                            if (detectOverflow(operationAddition, sum, a)) {
                                overflow = true
                                break
                            }
                            sum = (sum + a).toLong()
                            i++
                        }
                        overflow
                    }
                }
                else -> false
            }
        }
        is Float -> {
            val a = value1 as Float
            val b = value2 as Float
            val max = Float.MAX_VALUE
            when (operation) {
                operationAddition -> a > 0 && max - a < b
                operationSubtraction -> b < 0 && max + b < a
                operationMultiplication -> {
                    if (a < b) detectOverflow(operation, b, a)
                    else {
                        var sum: Float = 0F
                        var i: Float = 0F
                        var overflow = false
                        val absOfB = if (b<0) (-b).toFloat() else b
                        while(i < absOfB) {
                            if (detectOverflow(operationAddition, sum, a)) {
                                overflow = true
                                break
                            }
                            sum = (sum + a).toFloat()
                            i++
                        }
                        overflow
                    }
                }
                else -> false
            }
        }
        is Double -> {
            val a = value1 as Double
            val b = value2 as Double
            val max = Double.MAX_VALUE
            when (operation) {
                operationAddition -> a > 0 && max - a < b
                operationSubtraction -> b < 0 && max + b < a
                operationMultiplication -> {
                    if (a < b) detectOverflow(operation, b, a)
                    else {
                        var sum: Double = 0.0
                        var i: Double = 0.0
                        var overflow = false
                        val absOfB = if (b<0) (-b).toDouble() else b
                        while(i < absOfB) {
                            if (detectOverflow(operationAddition, sum, a)) {
                                overflow = true
                                break
                            }
                            sum = (sum + a).toDouble()
                            i++
                        }
                        overflow
                    }
                }
                else -> false
            }
        }
        else -> throw TypeCastException(unsupportedTypeString)
    }

    fun detectUnderflow(operation: Short, value1: Any, value2: Any): Boolean = when (value1) {
        // assume both values to be the same type
        is Byte -> {
            val a = value1 as Byte
            val b = value2 as Byte
            val min = Byte.MIN_VALUE
            when (operation) {
                operationAddition -> a < 0 && min - a > b
                operationSubtraction -> b > 0 && min + b > a
                operationMultiplication -> {
                    if (a < b) detectUnderflow(operation, b, a)
                    else {
                        var sum: Byte = 0
                        var i: Byte = 0
                        var underflow = false
                        val absOfB = if (b<0) (-b).toByte() else b
                        while(i < absOfB) {
                            if (detectUnderflow(operationAddition, sum, a)) {
                                underflow = true
                                break
                            }
                            sum = (sum + a).toByte()
                            i++
                        }
                        underflow
                    }
                }
                else -> false
            }
        }
        is Short -> {
            val a = value1 as Short
            val b = value2 as Short
            val min = Short.MIN_VALUE
            when (operation) {
                operationAddition -> a < 0 && min - a > b
                operationSubtraction -> b > 0 && min + b > a
                operationMultiplication -> {
                    if (a < b) detectUnderflow(operation, b, a)
                    else {
                        var sum: Short = 0
                        var i: Short = 0
                        var underflow = false
                        val absOfB = if (b<0) (-b).toShort() else b
                        while(i < absOfB) {
                            if (detectUnderflow(operationAddition, sum, a)) {
                                underflow = true
                                break
                            }
                            sum = (sum + a).toShort()
                            i++
                        }
                        underflow
                    }
                }
                else -> false
            }
        }
        is Int -> {
            val a = value1 as Int
            val b = value2 as Int
            val min = Int.MIN_VALUE
            when (operation) {
                operationAddition -> a < 0 && min - a > b
                operationSubtraction -> b > 0 && min + b > a
                operationMultiplication -> {
                    if (a < b) detectUnderflow(operation, b, a)
                    else {
                        var sum: Int = 0
                        var i: Int = 0
                        var underflow = false
                        val absOfB = if (b<0) (-b).toInt() else b
                        while(i < absOfB) {
                            if (detectUnderflow(operationAddition, sum, a)) {
                                underflow = true
                                break
                            }
                            sum = (sum + a).toInt()
                            i++
                        }
                        underflow
                    }
                }
                else -> false
            }
        }
        is Long -> {
            val a = value1 as Long
            val b = value2 as Long
            val min = Long.MIN_VALUE
            when (operation) {
                operationAddition -> a < 0 && min - a > b
                operationSubtraction -> b > 0 && min + b > a
                operationMultiplication -> {
                    if (a < b) detectUnderflow(operation, b, a)
                    else {
                        var sum: Long = 0
                        var i: Long = 0
                        var underflow = false
                        val absOfB = if (b<0) (-b).toLong() else b
                        while(i < absOfB) {
                            if (detectUnderflow(operationAddition, sum, a)) {
                                underflow = true
                                break
                            }
                            sum = (sum + a).toLong()
                            i++
                        }
                        underflow
                    }
                }
                else -> false
            }
        }
        is Float -> {
            val a = value1 as Float
            val b = value2 as Float
            val min = Float.MIN_VALUE
            when (operation) {
                operationAddition -> a < 0 && min - a > b
                operationSubtraction -> b > 0 && min + b > a
                operationMultiplication -> {
                    if (a < b) detectUnderflow(operation, b, a)
                    else {
                        var sum: Float = 0F
                        var i: Float = 0F
                        var underflow = false
                        val absOfB = if (b<0) (-b).toFloat() else b
                        while(i < absOfB) {
                            if (detectUnderflow(operationAddition, sum, a)) {
                                underflow = true
                                break
                            }
                            sum = (sum + a).toFloat()
                            i++
                        }
                        underflow
                    }
                }
                else -> false
            }
        }
        is Double -> {
            val a = value1 as Double
            val b = value2 as Double
            val min = Double.MIN_VALUE
            when (operation) {
                operationAddition -> a < 0 && min - a > b
                operationSubtraction -> b > 0 && min + b > a
                operationMultiplication -> {
                    if (a < b) detectUnderflow(operation, b, a)
                    else {
                        var sum: Double = 0.0
                        var i: Double = 0.0
                        var underflow = false
                        val absOfB = if (b<0) (-b).toDouble() else b
                        while(i < absOfB) {
                            if (detectUnderflow(operationAddition, sum, a)) {
                                underflow = true
                                break
                            }
                            sum = (sum + a).toDouble()
                            i++
                        }
                        underflow
                    }
                }
                else -> false
            }
        }
        else -> throw TypeCastException(unsupportedTypeString)
    }

    fun <T> process(value1: Any, value2: Any, result: (v1: T, v2: T) -> Any): T = result(value1 as T, value2 as T) as T

    fun promoteMessage(Type: String, NewType: String?) =
        println("Warning: the current expression (of type $Type) would result in a value larger than " +
                "the maximum possible value of that a $Type type can hold" +
                if (NewType != null) ", to prevent this from occurring, " +
                        "the current expression (of type $Type) will now be promoted to an expression of type $NewType"
                else ", however no type currently exists in which is capable of holding the larger value")

    fun promoteMessage(Type: String) = promoteMessage(Type, null)

    fun convert(operation: Short, value1: Any, value2: Any, result: (v1: Int, v2: Int) -> Any) = when (value1) {
        is Byte -> {
            val a = value1 as Byte
            val b = value2 as Byte
            var promotionNeeded = false
            if (detectOverflow(operation, a, b)) promotionNeeded = true
            if (detectUnderflow(operation, a, b)) {
                if (promotionNeeded) throw IllegalStateException(
                    "both overflow and underflow should not be allowed to occur in the same expression"
                )
                promotionNeeded = true
            }
            if (promotionNeeded) {
                promoteMessage("Byte", "Short")
                val a = a.toShort()
                val b = a.toShort()
                var promotionNeeded = false
                if (detectOverflow(operation, a, b)) promotionNeeded = true
                if (detectUnderflow(operation, a, b)) {
                    if (promotionNeeded)
                        throw IllegalStateException(
                            "both overflow and underflow should not be allowed to occur in the same expression"
                        )
                    promotionNeeded = true
                }
                if (promotionNeeded) {
                    promoteMessage("Short", "Int")
                    val a = a.toInt()
                    val b = a.toInt()
                    var promotionNeeded = false
                    if (detectOverflow(operation, a, b)) promotionNeeded = true
                    if (detectUnderflow(operation, a, b)) {
                        if (promotionNeeded)
                            throw IllegalStateException(
                                "both overflow and underflow should not be allowed to occur in the same expression"
                            )
                        promotionNeeded = true
                    }
                    if (promotionNeeded) {
                        promoteMessage("Int", "Long")
                        val a = a.toLong()
                        val b = a.toLong()
                        var promotionNeeded = false
                        if (detectOverflow(operation, a, b)) {
                            promoteMessage("Long")
                            println("warning: an Overflow will occur")
                        }
                        if (detectUnderflow(operation, a, b)) {
                            if (promotionNeeded) throw IllegalStateException(
                                "both overflow and underflow should not be allowed to occur in the same expression"
                            )
                            promoteMessage("Long")
                            println("warning: an Underflow will occur")
                        }
                        process<Long>(a, b, result as (v1: Long, v2: Long) -> Any).toLong()
                    } else process<Int>(a, b, result as (v1: Int, v2: Int) -> Any).toInt()
                } else process<Short>(a, b, result as (v1: Short, v2: Short) -> Any).toShort()
            } else process<Byte>(a, b, result as (v1: Byte, v2: Byte) -> Any).toByte()
        }
        is Short -> {
            val a = value1 as Short
            val b = value2 as Short
            var promotionNeeded = false
            if (detectOverflow(operation, a, b)) promotionNeeded = true
            if (detectUnderflow(operation, a, b)) {
                if (promotionNeeded)
                    throw IllegalStateException(
                        "both overflow and underflow should not be allowed to occur in the same expression"
                    )
                promotionNeeded = true
            }
            if (promotionNeeded) {
                promoteMessage("Short", "Int")
                val a = a.toInt()
                val b = a.toInt()
                var promotionNeeded = false
                if (detectOverflow(operation, a, b)) promotionNeeded = true
                if (detectUnderflow(operation, a, b)) {
                    if (promotionNeeded)
                        throw IllegalStateException(
                            "both overflow and underflow should not be allowed to occur in the same expression"
                        )
                    promotionNeeded = true
                }
                if (promotionNeeded) {
                    promoteMessage("Int", "Long")
                    val a = a.toLong()
                    val b = a.toLong()
                    var promotionNeeded = false
                    if (detectOverflow(operation, a, b)) {
                        promoteMessage("Long")
                        println("warning: an Overflow will occur")
                    }
                    if (detectUnderflow(operation, a, b)) {
                        if (promotionNeeded) throw IllegalStateException(
                            "both overflow and underflow should not be allowed to occur in the same expression"
                        )
                        promoteMessage("Long")
                        println("warning: an Underflow will occur")
                    }
                    process<Long>(a, b, result as (v1: Long, v2: Long) -> Any).toLong()
                } else process<Int>(a, b, result as (v1: Int, v2: Int) -> Any).toInt()
            } else process<Short>(a, b, result as (v1: Short, v2: Short) -> Any).toShort()
        }
        is Int -> {
            val a = value1 as Int
            val b = value2 as Int
            var promotionNeeded = false
            if (detectOverflow(operation, a, b)) promotionNeeded = true
            if (detectUnderflow(operation, a, b)) {
                if (promotionNeeded)
                    throw IllegalStateException(
                        "both overflow and underflow should not be allowed to occur in the same expression"
                    )
                promotionNeeded = true
            }
            if (promotionNeeded) {
                promoteMessage("Int", "Long")
                val a = a.toLong()
                val b = a.toLong()
                var promotionNeeded = false
                if (detectOverflow(operation, a, b)) {
                    promoteMessage("Long")
                    println("warning: an Overflow will occur")
                }
                if (detectUnderflow(operation, a, b)) {
                    if (promotionNeeded) throw IllegalStateException(
                        "both overflow and underflow should not be allowed to occur in the same expression"
                    )
                    promoteMessage("Long")
                    println("warning: an Underflow will occur")
                }
                process<Long>(a, b, result as (v1: Long, v2: Long) -> Any).toLong()
            } else process<Int>(a, b, result as (v1: Int, v2: Int) -> Any).toInt()
        }
        is Long -> {
            val a = value1 as Long
            val b = value2 as Long
            var promotionNeeded = false
            if (detectOverflow(operation, a, b)) {
                promoteMessage("Long")
                println("warning: an Overflow will occur")
            }
            if (detectUnderflow(operation, a, b)) {
                if (promotionNeeded) throw IllegalStateException(
                    "both overflow and underflow should not be allowed to occur in the same expression"
                )
                promoteMessage("Long")
                println("warning: an Underflow will occur")
            }
            process<Long>(a, b, result as (v1: Long, v2: Long) -> Any).toLong()
        }
        is Float -> {
            val a = value1 as Float
            val b = value2 as Float
            var promotionNeeded = false
            if (detectOverflow(operation, a, b)) promotionNeeded = true
            if (detectUnderflow(operation, a, b)) {
                if (promotionNeeded)
                    throw IllegalStateException(
                        "both overflow and underflow should not be allowed to occur in the same expression"
                    )
                promotionNeeded = true
            }
            if (promotionNeeded) {
                promoteMessage("Float", "Double")
                val a = a.toDouble()
                val b = a.toDouble()
                var promotionNeeded = false
                if (detectOverflow(operation, a, b)) {
                    promotionNeeded = true
                    promoteMessage("Double")
                    println("warning: an Overflow will occur")
                }
                if (detectUnderflow(operation, a, b)) {
                    if (promotionNeeded) throw IllegalStateException(
                        "both overflow and underflow should not be allowed to occur in the same expression"
                    )
                    promoteMessage("Double")
                    println("warning: an Underflow will occur")
                }
                process<Double>(a, b, result as (v1: Double, v2: Double) -> Any).toDouble()
            } else process<Float>(a, b, result as (v1: Float, v2: Float) -> Any).toFloat()
        }
        is Double -> {
            val a = value1 as Double
            val b = value2 as Double
            var promotionNeeded = false
            if (detectOverflow(operation, a, b)) {
                promotionNeeded = true
                promoteMessage("Double")
                println("warning: an Overflow will occur")
            }
            if (detectUnderflow(operation, a, b)) {
                if (promotionNeeded) throw IllegalStateException(
                    "both overflow and underflow should not be allowed to occur in the same expression"
                )
                promoteMessage("Double")
                println("warning: an Underflow will occur")
            }
            process<Double>(a, b, result as (v1: Double, v2: Double) -> Any).toDouble()
        }
        else -> throw TypeCastException(unsupportedTypeString)
    }

    fun x(operation: Short, value1: Any, value2: Any): Any {
        return when(operation) {
            operationAddition -> convert(operation, value1, value2) {v1, v2 -> v1 + v2 }
            operationSubtraction -> convert(operation, value1, value2) {v1, v2 -> v1 - v2 }
            operationMultiplication -> convert(operation, value1, value2) {v1, v2 -> v1 * v2 }
            operationDivision -> convert(operation, value1, value2) {v1, v2 -> v1 / v2 }
            else -> throw UnsupportedOperationException(unsupportedOperationString)
        }
    }
    fun x(operation: Short, pair: Pair<Any,Any>): Any = x(operation, pair.first, pair.second)

    private fun isValidOperation(operator: Short) = operator == operationAddition || operator == operationSubtraction ||
            operator == operationMultiplication || operator == operationDivision
    @Throws(TypeCastException::class, UnsupportedOperationException::class)
    fun operation(operation: Short, value1: Any, value2: Any) = when {
        isValidOperation(operation) -> when {
            bothValid(value1, value2) -> when {
                typesDiffer(value1, value2) -> when {
                    isNumber(value1) -> when {
                        isNumber(value2) -> {
                            val x = promoteToLargestNumber(value1, value2)
                            when {
                                bothShort(x.first, x.second) -> x(operation, x)
                                bothInt(x.first, x.second) -> x(operation, x)
                                bothLong(x.first, x.second) -> x(operation, x)
                                else -> throw TypeCastException(unsupportedTypeString)
                            }
                        }
                        isFloatingPoint(value2) -> {
                            val x = promoteToLargestFloat(promoteToFloat(value1), value2)
                            if (bothFloat(x.first, x.second)) x(operation, x)
                            else x(operation, x)
                        }
                        else -> throw TypeCastException(unsupportedTypeString)
                    }
                    isFloatingPoint(value1) -> when {
                        isNumber(value2) -> {
                            val x = promoteToLargestFloat(value1, promoteToFloat(value2))
                            if (bothFloat(x.first, x.second)) x(operation, x)
                            else x(operation, x)
                        }
                        isFloatingPoint(value2) -> {
                            val x = promoteToLargestFloat(value1, value2)
                            if (bothFloat(x.first, x.second)) x(operation, x)
                            else x(operation, x)
                        }
                        else -> throw TypeCastException(unsupportedTypeString)
                    }
                    else -> throw TypeCastException(unsupportedTypeString)
                }
                else -> x(operation, value1, value2)
            }
            else -> throw TypeCastException(unsupportedTypeString)
        }
        else -> throw UnsupportedOperationException(unsupportedOperationString)
    }
    fun addition(value1: Any, value2: Any) = operation(operationAddition, value1, value2)
    fun subtraction(value1: Any, value2: Any) = operation(operationSubtraction, value1, value2)
    fun multiplication(value1: Any, value2: Any) = operation(operationMultiplication, value1, value2)
    fun division(value1: Any, value2: Any) = operation(operationDivision, value1, value2)
}

open class Engine {
    private val operator = Operator()
    fun addition(v1: Any, v2: Any) = operator.addition(v1, v2)
    fun subtraction(v1: Any, v2: Any) = operator.subtraction(v1, v2)
    fun multiplication(v1: Any, v2: Any) = operator.multiplication(v1, v2)
    fun division(v1: Any, v2: Any) = operator.division(v1, v2)
    open var isFixedSize = true
    open var isResizable = false
}

open class EngineFixedSize: Engine() {
}

open class Math {
    private val engine = Engine()
    fun addition(v1: Any, v2: Any) = engine.addition(v1, v2)
    fun subtraction(v1: Any, v2: Any) = engine.subtraction(v1, v2)
    fun multiplication(v1: Any, v2: Any) = engine.multiplication(v1, v2)
    fun division(v1: Any, v2: Any) = engine.division(v1, v2)
}

open class VectorCore<Type>: Math {
    // constructors
    constructor() : super() {
        this.vector = mutableListOf()
    }
    constructor(dimension: Int) : super() {
        this.vector = mutableListOf()
        this.resize(dimension)
    }
    // variables
    protected val vector: MutableList<Type?>
    var dimensions: Int
        get() = vector.size
        set(value: Int) = resize(value)
    // vector resizing operations
    fun resize(size: Int) = when {
        size == 0 -> vector.clear()
        size > vector.size -> while(size > vector.size) vector.add(null)
        else -> while(size < vector.size) vector.removeAt(vector.lastIndex)
    }
    fun addDimension(default: Type) = vector.add(default)
    fun removeDimension(dimension: Int) = vector.removeAt(dimension)
    // vector indexing
    operator fun get(index: Int) = vector.get(index)
    protected fun getOrZero(index: Int): Any = if (get(index) == null) 0 as Int else get(index)!! as Any
    operator fun set(index: Int, value: Type?) {
        vector[index] = value
    }
    fun clone() = Vector<Type>().also { it.vector.addAll(vector) }
    override fun toString(): String {
        return "dimensions: $dimensions\n" +
                "size: ${vector.size}\n" +
                "vector: $vector\n"
    }
    fun iterator(): Iterator<Type?> = vector.iterator()
    fun forEach(action: (Item: Type?) -> Unit) = vector.forEach {
        action(it)
    }
    fun forEachIndex(action: (index: Int) -> Unit) = vector.forEachIndexed { index, type ->
        action(index)
    }
}

open class VectorBase<Type>: VectorCore<Type> {
    // constructors
    constructor() : super()
    constructor(dimension: Int) : super(dimension)

    // base operations
    protected fun <T> operation(vector: Vector<T>, action: (result: Vector<Any>, lhs: Any, rhs: Any) -> Unit): Vector<Any> {
        println(this)
        println(vector)
        val v1 = Vector<Any>()
        val vA = clone()
        val vB = vector.clone()
        if (dimensions != vector.dimensions) {
            vA.resize(if (dimensions > vector.dimensions) dimensions else vector.dimensions)
            vB.resize(if (dimensions > vector.dimensions) dimensions else vector.dimensions)
        }
        vA.forEachIndex {
            action(v1, vA.getOrZero(it), vB.getOrZero(it))
        }
        return v1
    }
    protected fun sum(): Any {
        var sum: Any = 0.toByte()
        forEachIndex {
            sum = addition(sum, getOrZero(it))
        }
        return sum
    }
}

class Vector<Type> : VectorBase<Type> {
    constructor() : super()
    constructor(dimension: Int) : super(dimension)

    /**
     * performs vector addition
     */
    operator fun <T> plus(vector: Vector<T>): Vector<Any> = operation(vector) { result, lhs, rhs ->
        result.addDimension(addition(lhs, rhs))
    }
    /**
     * takes an **input vector**, then multiplies **this vector** and the **input vector**
     * and then returns the **sum** of the **resulting vector**
     *
     * example, `(1, 2) dot (1 2) -> (1*1, 2*2) -> (1, 4) -> 1 + 4 -> 5`
     */
    fun <T> scalarProduct(vector: Vector<T>) = operation(vector) { result, lhs, rhs ->
        result.addDimension(multiplication(lhs, rhs))
    }.sum()

    /**
     * @see scalarProduct
     */
    fun <T> dotProduct(vector: Vector<T>) = scalarProduct(vector)
}

//open class VectorSpaceCore<Type>: Math {
//    // constructors
//    constructor() : super() {
//        this.vector = mutableListOf()
//    }
//    constructor(dimension: Int) : super() {
//        this.vector = mutableListOf()
//        this.resize(dimension)
//    }
//    // variables
//    protected val vector: MutableList<Vector<Type>?>
//    var dimensions: Int
//        get() = vector.size
//        set(value: Int) = resize(value)
//    // vector resizing operations
//    fun resize(size: Int) = when {
//        size == 0 -> vector.clear()
//        size > vector.size -> while(size > vector.size) vector.add(null)
//        else -> while(size < vector.size) vector.removeAt(vector.lastIndex)
//    }
//    fun addDimension(default: Type) = vector.add(default)
//    fun removeDimension(dimension: Int) = vector.removeAt(dimension)
//    // vector indexing
//    operator fun get(index: Int) = vector.get(index)
//    protected fun getOrZero(index: Int): Any = if (get(index) == null) 0 as Int else get(index)!! as Any
//    operator fun set(index: Int, value: Type?) {
//        vector[index] = value
//    }
//    fun clone() = Vector<Type>().also { it.vector.addAll(vector) }
//    override fun toString(): String {
//        return "dimensions: $dimensions\n" +
//                "size: ${vector.size}\n" +
//                "vector: $vector\n"
//    }
//    fun iterator(): Iterator<Type?> = vector.iterator()
//    fun forEach(action: (Item: Type?) -> Unit) = vector.forEach {
//        action(it)
//    }
//    fun forEachIndex(action: (index: Int) -> Unit) = vector.forEachIndexed { index, type ->
//        action(index)
//    }
//}

/**
 * **Vector space**
 *
 * A collection of vectors, where vectors are objects that may be added together and
 * multiplied by scalars
 *
 * Euclidean vectors are an example of a vector space, typically used to represent
 * displacements, as well as physical quantities such as force or momentum
 *
 *  **Dimensions of a vector space**
 *
 * The number of coordinates required to specify any point within the space
 */
class VectorSpace<Type> {
}

fun vec() {
    val v1 = Vector<Byte>(2)
    val v2 = Vector<Long>(2)
    v1[0] = 1
    v1[1] = 2
    v2[0] = 1
    v2[1] = 2
    println(v1)
    println(v2)
    println("v1 + v2 = ${v1 + v2}")
    println("v1 dot v2 = ${v1.dotProduct(v2)}")
}