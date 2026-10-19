package ru.parodiyazhizni.vkpromo

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import android.text.InputType

class MainActivity : Activity() {
    private val bg = Color.rgb(13, 13, 15)
    private val panel = Color.rgb(28, 28, 32)
    private val red = Color.rgb(210, 35, 45)
    private val white = Color.rgb(245, 245, 245)
    private val muted = Color.rgb(175, 175, 182)
    private lateinit var keyword: EditText
    private lateinit var community: EditText
    private lateinit var communityKeyword: EditText
    private lateinit var comment: EditText
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(24))
            setBackgroundColor(bg)
        }
        scroll.addView(content)

        content.addView(TextView(this).apply {
            text = "ПАРОДИЯ ЖИЗНИ"
            textSize = 25f
            setTextColor(red)
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }, matchWrap())
        content.addView(TextView(this).apply {
            text = "VK PROMO  •  ПОИСК АУДИТОРИИ"
            textSize = 12f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(5), 0, dp(18))
        }, matchWrap())

        section(content, "1. ПОИСК ПО КЛЮЧЕВЫМ СЛОВАМ")
        keyword = input("Например: Барклай-де-Толли, паган-метал")
        content.addView(keyword, matchWrap())
        button(content, "ИСКАТЬ ВО ВК") {
            val q = keyword.text.toString().trim()
            if (q.isEmpty()) toast("Введи ключевые слова")
            else {
                open("https://vk.com/search?c%5Bq%5D=${Uri.encode(q)}&c%5Bsection%5D=wall")
                status.text = "Открыт поиск ВК по запросу: $q"
            }
        }

        section(content, "2. ПОИСК ВНУТРИ СООБЩЕСТВА")
        community = input("Ссылка или короткое имя сообщества")
        content.addView(community, matchWrap())
        communityKeyword = input("Ключевое слово для поиска")
        content.addView(communityKeyword, matchWrap())
        button(content, "ОТКРЫТЬ СООБЩЕСТВО") {
            val raw = community.text.toString().trim()
            if (raw.isEmpty()) toast("Введи ссылку или имя сообщества")
            else {
                val cleaned = raw.removePrefix("https://").removePrefix("http://")
                    .removePrefix("m.vk.com/").removePrefix("vk.com/").trim('/')
                open("https://vk.com/$cleaned")
                status.text = "Сообщество открыто. Доступность поиска зависит от интерфейса ВК."
            }
        }
        button(content, "ИСКАТЬ КЛЮЧЕВОЕ СЛОВО") {
            val q = communityKeyword.text.toString().trim()
            if (q.isEmpty()) toast("Введи ключевое слово")
            else {
                open("https://vk.com/search?c%5Bq%5D=${Uri.encode(q)}&c%5Bsection%5D=wall")
                status.text = "Открыт общий поиск ВК. При необходимости используй фильтры ВК."
            }
        }

        section(content, "3. ЧЕРНОВИК КОММЕНТАРИЯ")
        comment = input("Напиши комментарий по теме публикации…").apply {
            minLines = 4
            gravity = Gravity.TOP or Gravity.START
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }
        content.addView(comment, LinearLayout.LayoutParams(-1, dp(120)))
        button(content, "СКОПИРОВАТЬ КОММЕНТАРИЙ") {
            val text = comment.text.toString().trim()
            if (text.isEmpty()) toast("Сначала напиши комментарий")
            else {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Комментарий ВК", text))
                toast("Скопировано. Проверь правила группы перед публикацией.")
            }
        }

        section(content, "4. СТАТУС")
        status = TextView(this).apply {
            text = "Готово. Прототип открывает поиск ВК; результаты и публикацию проверяешь сам."
            textSize = 13f
            setTextColor(muted)
            setBackgroundColor(panel)
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        content.addView(status, matchWrap())
        content.addView(TextView(this).apply {
            text = "Версия 0.1 • Без рекламы • Не запрашивает пароль ВК"
            textSize = 11f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(18), 0, 0)
        }, matchWrap())

        setContentView(scroll)
    }

    private fun section(parent: LinearLayout, label: String) {
        parent.addView(TextView(this).apply {
            text = label
            textSize = 14f
            setTextColor(red)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, dp(18), 0, dp(8))
        }, matchWrap())
    }

    private fun input(hintText: String) = EditText(this).apply {
        hint = hintText
        textSize = 14f
        setTextColor(white)
        setHintTextColor(muted)
        setPadding(dp(12), dp(10), dp(12), dp(10))
        setBackgroundColor(panel)
    }

    private fun button(parent: LinearLayout, label: String, action: () -> Unit) {
        parent.addView(Button(this).apply {
            text = label
            textSize = 12f
            setTextColor(white)
            setBackgroundColor(red)
            isAllCaps = false
            setOnClickListener { action() }
        }, matchWrap().apply { topMargin = dp(8) })
    }

    private fun open(url: String) {
        try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
        catch (_: Exception) { toast("Не удалось открыть ссылку") }
    }
    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
