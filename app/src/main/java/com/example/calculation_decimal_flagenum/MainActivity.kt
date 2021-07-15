package com.example.calculation_decimal_flagenum

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

    /** 値を入れるリスト */
    private val valueList = mutableListOf<Double>()

    /** 一時的に値を入れておく変数 */
    private var value = 0.0

    /** 演算子を入れるリスト
     *  + or - or × or ÷ */
    private val operatorList = mutableListOf<Char>()

    /** 演算子ボタンが押されたか把握するための変数 */
    private var flagOperation = Flag.NotPressed

    /** 実行ボタンが押されたか把握するための変数 */
    private var flagEqual = Flag.NotPressed

    /** 小数かどうか把握するための変数 */
    private var flagDecimal = Flag.NotPressed

    fun buttonPress(v: View){
        when(v.id){
            R.id.btn0 -> numButtonAction(0)
            R.id.btn1 -> numButtonAction(1)
            R.id.btn2 -> numButtonAction(2)
            R.id.btn3 -> numButtonAction(3)
            R.id.btn4 -> numButtonAction(4)
            R.id.btn5 -> numButtonAction(5)
            R.id.btn6 -> numButtonAction(6)
            R.id.btn7 -> numButtonAction(7)
            R.id.btn8 -> numButtonAction(8)
            R.id.btn9 -> numButtonAction(9)
            R.id.btn_clear -> clearButtonAction()
            R.id.btn_allClear -> allClearButtonAction()
            R.id.btn_changeOperator -> changeOperatorButtonAction()
            R.id.btn_plus -> calcButtonAction('+')
            R.id.btn_minus -> calcButtonAction('-')
            R.id.btn_time -> calcButtonAction('×')
            R.id.btn_divide -> calcButtonAction('÷')
            R.id.btn_equal -> equalButtonAction()
            R.id.btn_back -> backButtonAction()
            R.id.btn_decimalPoint -> decimalButtonAction()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /** ダイアログによるエラー処理　*/
    private fun dialogErrorAction (eName:ErrorName) {
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

    /** クリアボタンが押された時の処理 */
    private fun clearButtonAction() {
        // 今打ち込んでいる数値のみ削除する
        // すでに格納されている数値や演算子は消えない
        value = 0.0
        flagDecimal = Flag.NotPressed
        findViewById<TextView>(R.id.Output).text = value.toLong().toString()
    }

    /** オールクリアボタンが押された時の処理をまとめた関数 */
    private fun allClearButtonAction() {
        // 格納されている数値や演算子においても削除する。
        value = 0.0
        valueList.clear()
        operatorList.clear()
        flagDecimal = Flag.NotPressed
        flagOperation = Flag.NotPressed
        flagEqual = Flag.NotPressed
        findViewById<TextView>(R.id.Output).text = value.toLong().toString()
        findViewById<TextView>(R.id.OutputOperator).text = null
    }

    /** 数字ボタンが押された時の処理をまとめた関数 */
    @SuppressLint("CutPasteId")
    private fun numButtonAction(num: Int) {
        //整数部分12桁以上入力使用としたときにエラーを発生させる。
        //小数のときは、12桁以上は入力できないようにする。
        if (flagEqual == Flag.NotPressed &&
            flagOperation == Flag.NotPressed &&
            findViewById<TextView>(R.id.Output).text.toString().length >= 12) {
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
                // testArea.textのところで+使うことがあまり良くないみたい。
                findViewById<TextView>(R.id.Output).text = (String.format("%s%s", findViewById<TextView>(R.id.Output).text.toString(), num.toString()))
                value = findViewById<TextView>(R.id.Output).text.toString().toDouble()
            } else {
                // 整数の場合
                value = if (value == 0.0) {
                    // まだ何も値が入力されていない場合
                    num.toDouble()
                } else {
                    // すでに値が入力されていた場合
                    (value.toLong().toString() + num.toString()).toDouble()
                }
                findViewById<TextView>(R.id.Output).text = value.toLong().toString()
            }
        }
    }

    /** 計算処理をまとめた関数 */
    // 【0除算エラー対応】
    // Int_ver.では try-catchでArithmeticExceptionをキャッチする。
    // Double_ver.では Double.isInfinite()メソッドを使って、Infinityを判別する。
    private fun calculation() {
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
                    findViewById<TextView>(R.id.Output).text = value.toLong().toString()
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
                    findViewById<TextView>(R.id.Output).text = value.toString()
                    // バックスペースを使えるようにするため
                    flagDecimal = Flag.Pressed
                }
            }
        }
    }

    /** 実行ボタン"="が押された時の処理 */
    private fun equalButtonAction() {
        if (valueList.size > 0 && operatorList.size > 0 && flagOperation == Flag.NotPressed) {
            calculation()
            value = 0.0
            valueList.clear()
            operatorList.clear()
            flagEqual = Flag.Pressed
            flagOperation = Flag.NotPressed
            findViewById<TextView>(R.id.OutputOperator).text = null
        }
    }

    /** 演算子ボタンが押された時の処理をまとめた関数 */
    private fun calcButtonAction(op: Char) {
        findViewById<TextView>(R.id.OutputOperator).text = op.toString()
        if (flagOperation == Flag.NotPressed) {
            operatorList.add(op)
            if (flagEqual == Flag.Pressed) {
                // 実行ボタンが押された直後に、演算子ボタンが押された場合
                valueList.add(findViewById<TextView>(R.id.Output).text.toString().toDouble())
            } else {
                // 数字ボタンが押された後、演算子ボタンが押された場合
                calculation()
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

    /** バックスペース"→"が押された時の処理 */
    private fun backButtonAction(){
        // testAreaに0が入っている時は動作しなくて良いから。
        if (findViewById<TextView>(R.id.Output).text.toString() != "0") {
            if (flagDecimal == Flag.Pressed) {
                // 小数の場合（数値の最後に小数点がある場合も含む）
                if (findViewById<TextView>(R.id.Output).text.toString().substring(
                        findViewById<TextView>(R.id.Output).text.toString().length - 1,
                        findViewById<TextView>(R.id.Output).text.toString().length) == ".") {
                    // 数値の最後が小数点だった場合
                    flagDecimal = Flag.NotPressed
                    findViewById<TextView>(R.id.Output).text = findViewById<TextView>(R.id.Output).text.toString().dropLast(1)
                } else {
                    // 小数の場合  ex.) 3.14
                    findViewById<TextView>(R.id.Output).text = findViewById<TextView>(R.id.Output).text.toString().dropLast(1)
                    value = findViewById<TextView>(R.id.Output).text.toString().toDouble()
                }
            } else {
                // 整数の場合
                value = (findViewById<TextView>(R.id.Output).text.toString().toLong() / 10).toDouble()
                findViewById<TextView>(R.id.Output).text = value.toLong().toString()
            }
        }
    }

    /** プラスマイナスボタンが押された時の処理 */
    @SuppressLint("CutPasteId")
    private fun changeOperatorButtonAction() {
        // 演算子が押されていないときだけ動作するように。
        if(flagOperation == Flag.NotPressed){
            value = findViewById<TextView>(R.id.Output).text.toString().toDouble() * (-1)
            findViewById<TextView>(R.id.Output).text = if (flagDecimal == Flag.Pressed) {
                // 小数の場合
                value.toString()
            } else {
                // 整数の場合
                value.toLong().toString()
            }
        }
    }

    /** 小数点ボタンが押された時の処理 */
    private fun decimalButtonAction(){
        if (flagEqual == Flag.Pressed) {
            // 実行ボタンが押された直後に、小数点ボタンが押されたら、「0.」を表示する。
            flagDecimal = Flag.Pressed
            value = 0.0
            findViewById<TextView>(R.id.Output).text = value.toLong().toString().plus(".")
            flagEqual = Flag.NotPressed
        } else {
            if (flagDecimal == Flag.NotPressed) {
                // 値がまだ小数でないときにだけ、小数点を付ける。
                flagDecimal = Flag.Pressed
                findViewById<TextView>(R.id.Output).text = findViewById<TextView>(R.id.Output).text.toString().plus(".")
            }
        }
    }
}
