package x.vladgba.sms

import android.content.Context

/**
 * Word weights and the rating derived from them.
 * The user marks words as important (positive weight) or bad (negative weight);
 * a message's rating is the sum of the weights of the words it contains.
 */
object Marks {
    const val IMPORTANT = 1
    const val BAD = -1

    /** A word found in the user's messages, with how often it appears. */
    class Stat(val word: String, val count: Int)

    /**
     * Counts markable words across all messages, most frequent first.
     * Words the user already weighted are left out, so reviewing always makes progress.
     */
    fun collect(c: Context, limit: Int = 500): List<Stat> {
        val counts = HashMap<String, Int>()
        Sms.forEachBody(c) { body ->
            for (w in body.split(Regex("[^\\p{L}\\p{N}']+"))) {
                val t = w.trim('\'').lowercase()
                if (t.length < 3 || t.none { it.isLetter() }) continue
                counts[t] = (counts[t] ?: 0) + 1
            }
        }
        val marked = HashSet<String>()
        for (w in Db.get(c).weights()) marked.add(w.word)
        return counts.entries
            .asSequence()
            .filter { it.key !in marked }
            .sortedByDescending { it.value }
            .take(limit)
            .map { Stat(it.key, it.value) }
            .toList()
    }

    /** Word list pre-compiled for scoring many messages in one pass. */
    class Compiled(weights: List<Weight>) {
        private val words = Array(weights.size) { weights[it].word.lowercase() }
        private val vals = IntArray(weights.size) { weights[it].weight }

        fun isEmpty() = words.isEmpty()

        /** Sum of the weights of every whole-word, case-insensitive occurrence in [body]. */
        fun score(body: String): Int {
            if (words.isEmpty() || body.isEmpty()) return 0
            val low = body.lowercase()
            var sum = 0
            for (i in words.indices) {
                val w = words[i]
                if (w.isEmpty()) continue
                var from = 0
                while (true) {
                    val at = low.indexOf(w, from)
                    if (at < 0) break
                    val before = at == 0 || !low[at - 1].isLetterOrDigit()
                    val end = at + w.length
                    val after = end >= low.length || !low[end].isLetterOrDigit()
                    if (before && after) sum += vals[i]
                    from = end
                }
            }
            return sum
        }
    }

    /** Splits a message into markable words: letters/digits/apostrophes, 2+ chars, case-folded unique. */
    fun wordsOf(body: String): List<String> =
        body.split(Regex("[^\\p{L}\\p{N}']+"))
            .map { it.trim('\'') }
            .filter { it.length >= 2 }
            .distinctBy { it.lowercase() }
}
