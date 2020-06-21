package rationals

import java.lang.IllegalArgumentException
import java.math.BigInteger

data class Rational(val n: BigInteger, val d: BigInteger) : Comparable<Rational> {
    private val numerator: BigInteger
    private val denominator: BigInteger

    // init {
    //     when (d) {
    //         0.toBigInteger() -> throw IllegalArgumentException("Denominator cannot be 0")
    //         else -> {
    //             val gcd = n.gcd(d)
    //             if (d < 0.toBigInteger()) {
    //                 numerator = -n / gcd
    //                 denominator = -d / gcd
    //             } else {
    //                 numerator = n / gcd
    //                 denominator = d / gcd
    //             }
    //         }
    //     }
    // }

    init {
        // require throws IllegalArgumentException if the condition isn't satisfied!
        require(d != BigInteger.ZERO) { "Denominatore must be non-zero" }

        val gcd = n.gcd(d)
        val sign = d.signum().toBigInteger() // either 1 or -1 to ensure denominator is always positive!
        numerator = n / gcd * sign
        denominator = d / gcd * sign
    }

    // constructor(n: BigInteger) : this(n, 1.toBigInteger())
    constructor(n: BigInteger) : this(n, BigInteger.ONE)

    operator fun unaryMinus(): Rational =
        Rational(-numerator, denominator)

    operator fun plus(other: Rational): Rational =
        Rational(
            numerator * other.denominator + other.numerator * denominator,
            denominator * other.denominator
        )

    operator fun minus(other: Rational): Rational =
        Rational(
            numerator * other.denominator - other.numerator * denominator,
            denominator * other.denominator
        )

    operator fun times(other: Rational): Rational =
        Rational(numerator * other.numerator, denominator * other.denominator)

    operator fun div(other: Rational): Rational =
        Rational(numerator * other.denominator, denominator * other.numerator)

    // override fun equals(other: Any?): Boolean =
    //     when (other) {
    //         is Rational -> numerator * other.denominator == other.numerator * denominator
    //         else -> false
    //     }

    // Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun compareTo(other: Rational): Int =
        (numerator * other.denominator).compareTo(other.numerator * denominator)

    override fun toString(): String {
        if (denominator == 1.toBigInteger()) {
            return "$numerator"
        } else {
            return "$numerator/$denominator"
        }
    }

    // Auto-generated
    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
}

infix fun Int.divBy(other: Int): Rational = Rational(toBigInteger(), other.toBigInteger())

infix fun Long.divBy(other: Long): Rational = Rational(toBigInteger(), other.toBigInteger())

infix fun BigInteger.divBy(other: BigInteger): Rational = Rational(this, other)

// fun String.toRational(): Rational {
//     val parts = split('/')
//     return when (parts.size) {
//         1 -> Rational(parts.elementAt(0).toBigInteger())
//         2 -> Rational(parts.elementAt(0).toBigInteger(), parts.elementAt(1).toBigInteger())
//         else -> throw IllegalArgumentException("Not a Rational number")
//     }
// }
fun String.toRational(): Rational {
    // Move duplicated function into another extension function
    fun String.toBigIntegerOrFail() =
        toBigIntegerOrNull()
            ?: throw IllegalArgumentException("Expecting rational in the form of 'n/d' or 'n', was '$this'")

    if (!contains("/")) {
        val number = toBigIntegerOrFail()
        return Rational(number)
    }
    val parts = split("/")
    return Rational(parts[0].toBigIntegerOrFail(), parts[1].toBigIntegerOrFail())
}

fun main() {
    val a = "20325830850349869048604856908".toRational()
    println(a)
    val b = "-9192901948302584358938698".toRational()
    println(b)
    println(a > b)

    // val half = 1 divBy 2
    //
    // val third = 1 divBy 3
    // 1..9
    // val sum: Rational = half + third
    // println(5 divBy 6 == sum)
    //
    // val difference: Rational = half - third
    // println(1 divBy 6 == difference)
    //
    // val product: Rational = half * third
    // println(1 divBy 6 == product)
    //
    // val quotient: Rational = half / third
    // println(3 divBy 2 == quotient)
    //
    // val negation: Rational = -half
    // println(-1 divBy 2 == negation)
    //
    // println((2 divBy 1).toString() == "2")
    // println((-2 divBy 4).toString() == "-1/2")
    // println("117/1098".toRational().toString() == "13/122")
    //
    // val twoThirds = 2 divBy 3
    // println(half < twoThirds)
    //
    // println(half in third..twoThirds)
    //
    // println(2000000000L divBy 4000000000L == 1 divBy 2)
    //
    // println(
    //     "912016490186296920119201192141970416029".toBigInteger() divBy
    //         "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2
    // )
}
