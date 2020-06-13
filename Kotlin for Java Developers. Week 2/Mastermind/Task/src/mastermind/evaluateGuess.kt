package mastermind

data class Evaluation(val rightPosition: Int, val wrongPosition: Int)

/**
 * My Solution .... LOL :
 * */
// fun evaluateGuess(secret: String, guess: String): Evaluation {
//     val letters = mutableMapOf<Char, Int>()
//     secret.forEach { letters.put(it, letters.getOrDefault(it, 0) + 1) }
//     var rightPos = 0
//     var wrongPos = 0
//     for (i in secret.indices) { // calculate right positions
//         val secLetter = secret[i]
//         val guessLetter = guess[i]
//         val currCount = letters.getOrDefault(guessLetter, 0)
//         if (secLetter == guessLetter) {
//             rightPos += 1
//             letters.put(guessLetter, currCount - 1)
//         }
//     }
//     for (i in secret.indices) { // calculate wrong positions
//         val secLetter = secret[i]
//         val guessLetter = guess[i]
//         val currCount = letters.getOrDefault(guessLetter, 0)
//         if (secLetter != guessLetter && currCount > 0) {
//             wrongPos += 1
//             letters.put(guessLetter, currCount - 1)
//         }
//     }
//     return Evaluation(rightPos, wrongPos)
// }
//
// fun main() {
//     var evaluation: Evaluation = evaluateGuess("BCDA", "AFEA")
//     println(
//         "Right positions: ${evaluation.rightPosition}; " +
//             "wrong positions: ${evaluation.wrongPosition}."
//     )
// }

/**
 *
 * Functional Style:
 */
fun evaluateGuess(secret: String, guess: String): Evaluation {

    val rightPositions = secret.zip(guess).count { it.first == it.second }

    val commonLetters = "ABCDEF".sumBy { ch ->
        Math.min(secret.count { it == ch }, guess.count { it == ch })
    }

    return Evaluation(rightPositions, commonLetters - rightPositions)
}

fun main() {
    val result = Evaluation(rightPosition = 1, wrongPosition = 1)
    println(evaluateGuess("BCDF", "ACEB"))
    println(evaluateGuess("AAAF", "ABCA"))
    println(evaluateGuess("ABCA", "AAAF"))
}
