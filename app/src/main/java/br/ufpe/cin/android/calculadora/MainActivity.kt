package br.ufpe.cin.android.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var infoTxtView: TextView
    private lateinit var calcEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        infoTxtView = findViewById(R.id.text_info)
        calcEditText = findViewById(R.id.text_calc)

        // region Buttons assignments
        val sevenBtn = findViewById<Button>(R.id.btn_7)
        val eightBtn = findViewById<Button>(R.id.btn_8)
        val nineBtn = findViewById<Button>(R.id.btn_9)
        val divideSignBtn = findViewById<Button>(R.id.btn_Divide)

        val fourBtn = findViewById<Button>(R.id.btn_4)
        val fiveBtn = findViewById<Button>(R.id.btn_5)
        val sixBtn = findViewById<Button>(R.id.btn_6)
        val multiplicationSignBtn = findViewById<Button>(R.id.btn_Multiply)

        val oneBtn = findViewById<Button>(R.id.btn_1)
        val twoBtn = findViewById<Button>(R.id.btn_2)
        val threeBtn = findViewById<Button>(R.id.btn_3)
        val minusSignBtn = findViewById<Button>(R.id.btn_Subtract)

        val dotSignBtn = findViewById<Button>(R.id.btn_Dot)
        val zeroBtn = findViewById<Button>(R.id.btn_0)
        val equalSignBtn = findViewById<Button>(R.id.btn_Equal)
        val plusSignBtn = findViewById<Button>(R.id.btn_Add)

        val openingParenthesis = findViewById<Button>(R.id.btn_LParen)
        val closingParenthesis = findViewById<Button>(R.id.btn_RParen)
        val powerOfSign = findViewById<Button>(R.id.btn_Power)
        val clearConsoleBtn = findViewById<Button>(R.id.btn_Clear)
        // endregion

        sevenBtn.setOnClickListener(this)
        eightBtn.setOnClickListener(this)
        nineBtn.setOnClickListener(this)
        divideSignBtn.setOnClickListener(this)

        fourBtn.setOnClickListener(this)
        fiveBtn.setOnClickListener(this)
        sixBtn.setOnClickListener(this)
        multiplicationSignBtn.setOnClickListener(this)

        oneBtn.setOnClickListener(this)
        twoBtn.setOnClickListener(this)
        threeBtn.setOnClickListener(this)
        minusSignBtn.setOnClickListener(this)

        dotSignBtn.setOnClickListener(this)
        zeroBtn.setOnClickListener(this)
        equalSignBtn.setOnClickListener(this)
        plusSignBtn.setOnClickListener(this)

        openingParenthesis.setOnClickListener(this)
        closingParenthesis.setOnClickListener(this)
        powerOfSign.setOnClickListener(this)
        clearConsoleBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_7 -> calcEditText.text.append("7")
            R.id.btn_8 -> calcEditText.text.append("8")
            R.id.btn_9 -> calcEditText.text.append("9")
            R.id.btn_Divide -> calcEditText.setText("${calcEditText.text}/")

            R.id.btn_4 -> calcEditText.text.append("4")
            R.id.btn_5 -> calcEditText.text.append("5")
            R.id.btn_6 -> calcEditText.text.append("6")
            R.id.btn_Multiply -> calcEditText.setText("${calcEditText.text}*")

            R.id.btn_1 -> calcEditText.text.append("1")
            R.id.btn_2 -> calcEditText.text.append("2")
            R.id.btn_3 -> calcEditText.text.append("3")
            R.id.btn_Subtract -> calcEditText.setText("${calcEditText.text}-")

            R.id.btn_Dot -> calcEditText.text.append(".")
            R.id.btn_0 -> calcEditText.text.append("0")
            R.id.btn_Add -> calcEditText.setText("${calcEditText.text}+")
            R.id.btn_Equal -> {
                try {
                    infoTxtView.text = this.eval(calcEditText.text.toString()).toString()
                } catch (e: RuntimeException) {
                    infoTxtView.text = getString(R.string.invalid_expression_txt)
                    Toast.makeText(v.context, getString(R.string.type_valid_expression_txt), Toast.LENGTH_LONG).show()
                }
            }

            R.id.btn_LParen -> calcEditText.setText("${calcEditText.text}(")
            R.id.btn_RParen -> calcEditText.setText("${calcEditText.text})")
            R.id.btn_Power -> calcEditText.setText("${calcEditText.text}^")
            R.id.btn_Clear -> calcEditText.text.clear()
        }
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }

}