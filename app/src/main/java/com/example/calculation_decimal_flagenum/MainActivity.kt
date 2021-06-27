package com.example.calculation_decimal_flagenum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlin.math.pow
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {

    enum class ErrorName {
        Flow, Arithmetic, NaN
    }

    enum class Flag {
        Pressed, NotPressed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** 値を入れるリスト */
        val valueList = mutableListOf<Double>()

        /** 一時的に値を入れておく変数 */
        var value = 0.0

        /** 演算子を入れるリスト
         *  + or - or × or ÷ */
        val operatorList = mutableListOf<Char>()

        /** 演算子ボタンが押されたか把握するための変数 */
        var flagOperation = Flag.NotPressed

        /** 実行ボタンが押されたか把握するための変数 */
        var flagEqual = Flag.NotPressed

        /** 小数かどうか把握するための変数 */
        var flagDecimal = Flag.NotPressed


        /** 数字ボタン */
        val button0 = findViewById<Button>(R.id.btn0)
        val button1 = findViewById<Button>(R.id.btn1)
        val button2 = findViewById<Button>(R.id.btn2)
        val button3 = findViewById<Button>(R.id.btn3)
        val button4 = findViewById<Button>(R.id.btn4)
        val button5 = findViewById<Button>(R.id.btn5)
        val button6 = findViewById<Button>(R.id.btn6)
        val button7 = findViewById<Button>(R.id.btn7)
        val button8 = findViewById<Button>(R.id.btn8)
        val button9 = findViewById<Button>(R.id.btn9)

        /** 演算子ボタン */
        val buttonPlus = findViewById<Button>(R.id.btn_plus)
        val buttonMinus = findViewById<Button>(R.id.btn_minus)
        val buttonTime = findViewById<Button>(R.id.btn_time)
        val buttonDivide = findViewById<Button>(R.id.btn_divide)

        /** 実行ボタン */
        val buttonEqual = findViewById<Button>(R.id.btn_equal)

        /** 1つ戻るボタン */
        val buttonBack = findViewById<Button>(R.id.btn_back)

        /** クリアボタン */
        val buttonClear = findViewById<Button>(R.id.btn_clear)
        val buttonAllClear = findViewById<Button>(R.id.btn_allClear)

        /** 符号を正負逆にするボタン */
        val buttonChangeOperator = findViewById<Button>(R.id.btn_changeOperator)

        /** 小数点ボタン */
        val buttonDecimalPoint = findViewById<Button>(R.id.btn_decimalPoint)

        /** 表示テキスト */
        val textArea = findViewById<TextView>(R.id.Output)
        val operatorArea = findViewById<TextView>(R.id.OutputOperator)


        /** クリアボタンが押された時の処理 */
        buttonClear.setOnClickListener {
            // 今打ち込んでいる数値のみ削除する
            // すでに格納されている数値や演算子は消えない
            value = 0.0
            flagDecimal = Flag.NotPressed
            textArea.text = value.toLong().toString()
        }

        /** オールクリアボタンが押された時の処理をまとめた関数 */
        fun allClearButtonAction() {
            // エラー処理時にも「オールクリア」の機能を使うため、
            // allClearButtonActionメソッドを作成。
            // 格納されている数値や演算子においても削除する。
            // 初期化
            value = 0.0
            valueList.clear()
            operatorList.clear()
            flagDecimal = Flag.NotPressed
            flagOperation = Flag.NotPressed
            flagEqual = Flag.NotPressed
            textArea.text = value.toLong().toString()
            operatorArea.text = null
        }

        /** オールクリアボタンが押された時の処理 */
        buttonAllClear.setOnClickListener {
            allClearButtonAction()
        }

        /** ダイアログによるエラー処理　*/
        fun dialogErrorAction (eName:ErrorName) {
            val dialog = AlertDialog.Builder(this)
            when(eName.name){
                "Flow" -> dialog.setTitle(resources.getString(R.string.dialog_FlowErrorTitle))
                "Arithmetic" -> dialog.setTitle(resources.getString(R.string.dialog_ArithmeticErrorTitle))
                "NaN" -> dialog.setTitle(resources.getString(R.string.dialog_NaNErrorTitle))
            }
            dialog
                .setMessage(resources.getString(R.string.dialog_message))
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .setPositiveButton(resources.getString(R.string.dialog_ok)) { _, _ ->
                    allClearButtonAction()
                }
                .show()
        }

        /** 数字ボタンが押された時の処理をまとめた関数 */
        fun numButtonAction(num: Int) {
            //整数部分12桁以上入力使用としたときにエラーを発生させる。
            //小数のときは、12桁以上は入力できないようにする。
            if (flagEqual == Flag.NotPressed && flagOperation == Flag.NotPressed && textArea.text.toString().length >= 12) {
                if (flagDecimal == Flag.NotPressed){
                    dialogErrorAction(ErrorName.Flow)
                }
            } else {

                // 実行ボタン"="が押された後に数字ボタンが押された場合
                // textAreaに残っていた計算結果はクリアされ、初期状態から計算を行う。
                // そのため、小数点も無しの状態に戻す。
                if (flagEqual == Flag.Pressed) {
                    flagDecimal = Flag.NotPressed
                }
                flagOperation = Flag.NotPressed
                flagEqual = Flag.NotPressed

                if (flagDecimal == Flag.Pressed) {
                    // 小数の場合
                    // 下記の方法を使うことで、0.00000とか打てるし、エラーもない！
                    // あと、testArea.textのところで+使うことがあまり良くないみたい。
                    textArea.text = (String.format("%s%s", textArea.text.toString(), num.toString()))
                    value = textArea.text.toString().toDouble()
                } else {
                    // 整数の場合
                    if (value == 0.0) {
                        // まだ何も値が入力されていない場合
                        value = num.toDouble()
                        textArea.text = value.toLong().toString()
                    } else {
                        // すでに値が入力されていた場合
                        value = (value.toLong().toString() + num.toString()).toDouble()
                        textArea.text = value.toLong().toString()
                    }
                }
            }
        }

        /** 数字ボタンを押された時の処理 */
        button0.setOnClickListener {
            numButtonAction(0)
        }

        button1.setOnClickListener {
            numButtonAction(1)
        }

        button2.setOnClickListener {
            numButtonAction(2)
        }

        button3.setOnClickListener {
            numButtonAction(3)
        }

        button4.setOnClickListener {
            numButtonAction(4)
        }

        button5.setOnClickListener {
            numButtonAction(5)
        }

        button6.setOnClickListener {
            numButtonAction(6)
        }

        button7.setOnClickListener {
            numButtonAction(7)
        }

        button8.setOnClickListener {
            numButtonAction(8)
        }

        button9.setOnClickListener {
            numButtonAction(9)
        }

        /** 計算処理をまとめた関数 */
        // 【0除算エラー対応】
        // Int_ver.では try-catchでArithmeticExceptionをキャッチする。
        // Double_ver.では Double.isInfinite()メソッドを使って、Infinityを判別する。
        fun equalButtonAction() {
            valueList.add(value)
            value = valueList[0]
            for (i in 0 until valueList.size - 1) {
                when {
                    operatorList[i] == '+' -> value += valueList[i + 1]
                    operatorList[i] == '-' -> value -= valueList[i + 1]
                    operatorList[i] == '×' -> value *= valueList[i + 1]
                    operatorList[i] == '÷' -> value /= valueList[i + 1]
                }
            }
            when {
                // 0除算エラー対応 Double_ver.
                value.isInfinite() -> dialogErrorAction(ErrorName.Arithmetic)
                // 整数部分 12桁オーバー対応
                value.toLong().toString().length > 12 -> dialogErrorAction(ErrorName.Flow)
                // 0同士除算エラー対応
                value.isNaN() -> dialogErrorAction(ErrorName.NaN)

                else -> {
                    if (value - value.toLong().toDouble() == 0.0) {
                        // 導き出した値が整数の場合
                        textArea.text = value.toLong().toString()
                        // バックスペースを使えるようにするため
                        flagDecimal = Flag.NotPressed
                    } else {
                        // 導き出した値が小数の場合
                        // 最初に、小数部分 12桁オーバー対応 0から数えて11番目の数を四捨五入して、10番目の数までにする。
                        if (value.toString().length > 12) {
                            value = (value * 10.0.pow(
                                (12 - value.toString().indexOf(".") - 1)
                            )).roundToLong() / 10.0.pow(
                                (12 - value.toString().indexOf(".") - 1)
                            )
                        }
                        textArea.text = value.toString()
                        // バックスペースを使えるようにするため
                        flagDecimal = Flag.Pressed
                    }
                }
            }
        }

        /** 実行ボタン"="が押された時の処理 */
        buttonEqual.setOnClickListener {
            if (valueList.size > 0 && operatorList.size > 0 && flagOperation == Flag.NotPressed) {
                equalButtonAction()
                value = 0.0
                valueList.clear()
                operatorList.clear()
                flagEqual = Flag.Pressed
                flagOperation = Flag.NotPressed
                operatorArea.text = null
            }
        }


        /** 演算子ボタンが押された時の処理をまとめた関数 */
        fun calcButtonAction(op: Char) {
            operatorArea.text = op.toString()
            if (flagOperation == Flag.NotPressed) {
                operatorList.add(op)
                if (flagEqual == Flag.Pressed) {
                    // 実行ボタンが押された直後に、演算子ボタンが押された場合
                    valueList.add(textArea.text.toString().toDouble())
                } else {
                    // 数字ボタンが押された後、演算子ボタンが押された場合
                    equalButtonAction()
                }
                value = 0.0
                flagDecimal = Flag.NotPressed
                flagOperation = Flag.Pressed
            } else {
                if (valueList.isNotEmpty()){
                    // 演算子ボタンが2回連続で押された場合
                    operatorList.removeAt(operatorList.size - 1)
                    operatorList.add(op)
                }
            }
        }

        /** 演算子ボタンが押された時の処理 */
        buttonPlus.setOnClickListener {
            calcButtonAction('+')
        }

        buttonMinus.setOnClickListener {
            calcButtonAction('-')
        }

        buttonTime.setOnClickListener {
            calcButtonAction('×')
        }

        buttonDivide.setOnClickListener {
            calcButtonAction('÷')
        }



        /** バックスペース"→"が押された時の処理 */
        buttonBack.setOnClickListener {
            // testAreaに0が入っている時は動作しなくて良いから。
            if (textArea.text.toString() != "0") {
                if (flagDecimal == Flag.Pressed) {
                    // 小数の場合（数値の最後に小数点がある場合も含む）
                    if (textArea.text.toString().substring(
                            textArea.text.toString().length - 1,
                            textArea.text.toString().length) == ".") {
                        // 数値の最後が小数点だった場合
                        flagDecimal = Flag.NotPressed
                        textArea.text = textArea.text.toString().dropLast(1)
                    } else {
                        // 小数の場合  ex.) 3.14
                        textArea.text = textArea.text.toString().dropLast(1)
                        value = textArea.text.toString().toDouble()
                    }
                } else {
                    // 整数の場合
                    value = (textArea.text.toString().toLong() / 10).toDouble()
                    textArea.text = value.toLong().toString()
                }
            }
        }

        /** プラスマイナスボタンが押された時の処理 */
        buttonChangeOperator.setOnClickListener {
            // 演算子が押されていないときだけ動作するように。
            if(flagOperation == Flag.NotPressed){
                value = textArea.text.toString().toDouble() * (-1)
                if (flagDecimal == Flag.Pressed) {
                    // 小数の場合
                    textArea.text = value.toString()
                } else {
                    // 整数の場合
                    textArea.text = value.toLong().toString()
                }
            }
        }

        /** 小数点ボタンが押された時の処理 */
        buttonDecimalPoint.setOnClickListener {
            if (flagEqual == Flag.Pressed) {
                // 実行ボタンが押された直後に、小数点ボタンが押されたら、「0.」を表示する。
                flagDecimal = Flag.Pressed
                value = 0.0
                textArea.text = value.toLong().toString().plus(".")
                flagEqual = Flag.NotPressed
            } else {
                if (flagDecimal == Flag.NotPressed) {
                    // 値がまだ小数でないときにだけ、小数点を付ける。
                    flagDecimal = Flag.Pressed
                    textArea.text = textArea.text.toString().plus(".")
                }
            }
        }
    }
}
