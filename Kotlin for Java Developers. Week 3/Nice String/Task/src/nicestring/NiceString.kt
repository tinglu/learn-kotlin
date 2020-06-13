package nicestring

fun String.isNice(): Boolean {
    val vowels = "aeiou"

    var satisfiedConditions = 0
    var containSubString = false
    var numVowels = 0
    var containDoubleLetter = false

    zip(drop(1) + " ").forEach {
        if (it.first == 'b' && (it.second == 'u' || it.second == 'a' || it.second == 'e')) containSubString = true

        if (vowels.contains(it.first)) numVowels += 1

        if (it.first == it.second) containDoubleLetter = true
    }

    if (!containSubString) satisfiedConditions += 1
    if (numVowels >= 3) satisfiedConditions += 1
    if (containDoubleLetter) satisfiedConditions += 1
    return satisfiedConditions >= 2
}
