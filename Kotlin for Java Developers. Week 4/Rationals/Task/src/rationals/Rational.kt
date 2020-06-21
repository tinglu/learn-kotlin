package rationals

import java.math.BigInteger

data class Rational(val n: BigInteger, val d: BigInteger) : Comparable<Rational> {
    private val numerator: BigInteger
    private val denominator: BigInteger

    init {
        when (d) {
            0.toBigInteger() -> throw IllegalArgumentException("Denominator cannot be 0")
            else -> {
                val gcd = n.gcd(d)
                if (d < 0.toBigInteger()) {
                    numerator = -n / gcd
                    denominator = -d / gcd
                } else {
                    numerator = n / gcd
                    denominator = d / gcd
                }
            }
        }
    }

    constructor(n: BigInteger) : this(n, 1.toBigInteger())

    operator fun unaryMinus(): Rational =
        Rational(-this.numerator, this.denominator)

    operator fun plus(other: Rational): Rational =
        Rational(
            this.numerator * other.denominator + other.numerator * this.denominator,
            this.denominator * other.denominator
        )

    operator fun minus(other: Rational): Rational =
        Rational(
            this.numerator * other.denominator - other.numerator * this.denominator,
            this.denominator * other.denominator
        )

    operator fun times(other: Rational): Rational =
        Rational(this.numerator * other.numerator, this.denominator * other.denominator)

    operator fun div(other: Rational): Rational =
        Rational(this.numerator * other.denominator, this.denominator * other.numerator)

    override fun equals(other: Any?): Boolean =
        when (other) {
            is Rational -> this.numerator * other.denominator == other.numerator * this.denominator
            else -> false
        }

    override fun compareTo(other: Rational): Int =
        (this.numerator * other.denominator).compareTo(other.numerator * this.denominator)

    override fun toString(): String {
        if (denominator == 1.toBigInteger()) {
            return "$numerator"
        } else {
            return "$numerator/$denominator"
        }
    }

    override fun hashCode(): Int {
        var result = n.hashCode()
        result = 31 * result + d.hashCode()
        result = 31 * result + numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
}

infix fun Int.divBy(other: Int): Rational = Rational(this.toBigInteger(), other.toBigInteger())

infix fun Long.divBy(other: Long): Rational = Rational(this.toBigInteger(), other.toBigInteger())

infix fun BigInteger.divBy(other: BigInteger): Rational = Rational(this, other)

fun String.toRational(): Rational {
    val parts = this.split('/')
    return when (parts.size) {
        1 -> Rational(parts.elementAt(0).toBigInteger())
        2 -> Rational(parts.elementAt(0).toBigInteger(), parts.elementAt(1).toBigInteger())
        else -> throw IllegalArgumentException("Not a Rational number")
    }
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
