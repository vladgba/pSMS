package x.vladgba.sms

import android.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

/** "Marks": words the user weighted as important or bad, with the rating each word carries. */
class MarksPage(a: SettingsActivity) : Page(a, R.layout.page_marks) {
    private val adapter = Adapter()
    private lateinit var none: View

    init {
        val list = findViewById<ListView>(R.id.list)
        val header = layoutInflater.inflate(R.layout.marks_header, list, false)
        list.addHeaderView(header, null, false)
        list.adapter = adapter
        none = header.findViewById(R.id.noMarks)
        header.findViewById<View>(R.id.review).setOnClickListener { reviewWords() }
        list.setOnItemClickListener { _, _, pos, _ ->
            val w = adapter.items.getOrNull(pos - list.headerViewsCount) ?: return@setOnItemClickListener
            AlertDialog.Builder(this).setTitle(w.word)
                .setItems(arrayOf(getString(R.string.weight_important), getString(R.string.weight_bad), getString(R.string.delete))) { _, which ->
                    when (which) {
                        0 -> Ui.load({ Db.get(this).saveWeight(w.word, Marks.IMPORTANT) }) { reload() }
                        1 -> Ui.load({ Db.get(this).saveWeight(w.word, Marks.BAD) }) { reload() }
                        2 -> AlertDialog.Builder(this).setMessage(getString(R.string.delete_mark_q, w.word))
                            .setPositiveButton(R.string.delete) { _, _ ->
                                Ui.load({ Db.get(this).removeWeight(w.word) }) { reload() }
                            }
                            .setNegativeButton(R.string.cancel, null).show()
                    }
                }.show()
        }
    }

    override val actionIcon = R.drawable.ic_add
    override val actionLabel = R.string.add_mark
    override fun onAction() = addDialog()

    override fun onShow() = reload()

    private fun reload() {
        Ui.load({ Db.get(this).weights() }) {
            adapter.items = it
            adapter.notifyDataSetChanged()
            none.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Reads every message, then walks the user through the words: good, bad or neutral. */
    private fun reviewWords() {
        Toast.makeText(this, R.string.reviewing, Toast.LENGTH_SHORT).show()
        Ui.load({ Marks.collect(this) }) { stats ->
            if (stats.isEmpty()) {
                Toast.makeText(this, R.string.review_none, Toast.LENGTH_SHORT).show()
                return@load
            }
            reviewNext(stats, 0)
        }
    }

    /** Shows word [i]; Neutral leaves it unweighted and moves on, back button ends the review. */
    private fun reviewNext(stats: List<Marks.Stat>, i: Int) {
        if (i >= stats.size) {
            Toast.makeText(this, R.string.review_done, Toast.LENGTH_SHORT).show()
            reload()
            return
        }
        val s = stats[i]
        AlertDialog.Builder(this)
            .setTitle(s.word)
            .setMessage(getString(R.string.review_count, s.count))
            .setPositiveButton(R.string.review_good) { _, _ ->
                Ui.load({ Db.get(this).saveWeight(s.word, Marks.IMPORTANT) }) { reviewNext(stats, i + 1) }
            }
            .setNegativeButton(R.string.review_bad) { _, _ ->
                Ui.load({ Db.get(this).saveWeight(s.word, Marks.BAD) }) { reviewNext(stats, i + 1) }
            }
            .setNeutralButton(R.string.review_neutral) { _, _ -> reviewNext(stats, i + 1) }
            .setOnCancelListener { reload() }
            .show()
    }

    /** Add a word by typing it, then choose important or bad. */
    private fun addDialog() {
        val input = EditText(this)
        input.setHint(R.string.mark_word_hint)
        input.setSingleLine()
        val pad = (16 * resources.displayMetrics.density).toInt()
        val box = android.widget.LinearLayout(this)
        box.orientation = android.widget.LinearLayout.VERTICAL
        box.addView(input, android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(pad, 0, pad, 0) })
        AlertDialog.Builder(this)
            .setTitle(R.string.add_mark)
            .setView(box)
            .setPositiveButton(R.string.weight_important) { _, _ ->
                val w = input.text.toString().trim()
                if (w.isEmpty()) return@setPositiveButton
                Ui.load({ Db.get(this).saveWeight(w, Marks.IMPORTANT) }) { reload() }
            }
            .setNegativeButton(R.string.weight_bad) { _, _ ->
                val w = input.text.toString().trim()
                if (w.isEmpty()) return@setNegativeButton
                Ui.load({ Db.get(this).saveWeight(w, Marks.BAD) }) { reload() }
            }
            .setNeutralButton(R.string.cancel, null)
            .show()
    }

    private inner class Adapter : BaseAdapter() {
        var items: List<Weight> = ArrayList()
        override fun getCount() = items.size
        override fun getItem(p: Int) = items[p]
        override fun getItemId(p: Int) = items[p].id

        override fun getView(p: Int, convertView: View?, parent: ViewGroup): View {
            val v = convertView ?: layoutInflater.inflate(R.layout.item_rule, parent, false)
            val w = items[p]
            val good = w.weight > 0
            v.findViewById<TextView>(R.id.tag).apply {
                text = getString(if (good) R.string.mark_up else R.string.mark_down, w.weight)
                setTextColor(getColor(if (good) R.color.accent else R.color.danger))
            }
            v.findViewById<TextView>(R.id.pattern).text = w.word
            v.findViewById<TextView>(R.id.field).visibility = View.GONE
            v.findViewById<View>(R.id.on).visibility = View.GONE
            return v
        }
    }
}
