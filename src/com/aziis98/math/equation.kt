package com.aziis98.math

import com.aziis98.math.MathExpression.Variable.DefaultVariable
import java.util.*

// Copyright 2016 Antonio De Lucreziis

sealed class MathExpression {

    abstract fun differentiate(respectTo: String = DefaultVariable.name): MathExpression

    open fun simplify() = this

    class Constant(val value: Double) : MathExpression() {
        override fun differentiate(respectTo: String): MathExpression {
            return Constant(0.0)
        }

        override fun toString() = value.toString()
    }

    sealed class Variable(val name: String) : MathExpression() {
        object DefaultVariable : Variable("x")

        override fun toString() = name

        override fun differentiate(respectTo: String): MathExpression {
            return if (name == respectTo) Constant(1.0) else Constant(0.0)
        }

    }

    sealed class BinaryOperation(val left: MathExpression, val right: MathExpression) : MathExpression() {

        class Addition(left: MathExpression, right: MathExpression) : BinaryOperation(left, right) {
            override fun differentiate(respectTo: String): MathExpression {
                return left.differentiate(respectTo) + right.differentiate(respectTo)
            }

            override fun simplify(): MathExpression {
                val sLeft = left.simplify()
                val sRight = right.simplify()

                return when {
                    sLeft is Constant && sRight is Constant -> Constant(sLeft.value + sRight.value)

                    sLeft is Constant && sLeft.value == 0.0 -> sRight
                    sRight is Constant && sRight.value == 0.0 -> sLeft

                    sLeft is Variable && sRight is Variable && sLeft.name == sRight.name -> Multiplication(Constant(2.0), sLeft)

                    else -> Addition(sLeft, sRight)
                }
            }

            override fun toString() = "($left + $right)"
        }

        class Subtraction(left: MathExpression, right: MathExpression) : BinaryOperation(left, right) {
            override fun differentiate(respectTo: String): MathExpression {
                return left.differentiate(respectTo) - right.differentiate(respectTo)
            }

            override fun simplify(): MathExpression {
                val sLeft = left.simplify()
                val sRight = right.simplify()
                return when {
                    sRight is Constant && sRight.value == 0.0 -> sLeft

                    else -> Subtraction(sLeft, sRight)
                }
            }

            override fun toString() = "($left - $right)"
        }

        class Multiplication(left: MathExpression, right: MathExpression) : BinaryOperation(left, right) {
            override fun differentiate(respectTo: String): MathExpression {
                return left.differentiate(respectTo) * right + left * right.differentiate(respectTo)
            }

            override fun simplify(): MathExpression {
                val sLeft = left.simplify()
                val sRight = right.simplify()

                return when {
                    sLeft is Constant && sRight is Constant -> Constant(sLeft.value * sRight.value)

                    sLeft is Variable && sRight is Variable && sLeft.name == sRight.name -> Power(sLeft, Constant(2.0))

                    sLeft is Multiplication
                        && sRight is Variable
                        && sLeft.left is Constant
                        && sLeft.right is Variable
                        && sLeft.right.name == sRight.name -> Multiplication(Addition(sLeft.left, Constant(1.0)).simplify(), sRight)

                    sLeft is Constant && sLeft.value == 1.0 -> sRight
                    sRight is Constant && sRight.value == 1.0 -> sLeft

                    else -> Multiplication(sLeft, sRight)
                }
            }

            override fun toString() = "($left * $right)"
        }

        class Power(left: MathExpression, right: MathExpression) : BinaryOperation(left, right) {
            override fun differentiate(respectTo: String): MathExpression {
                return when {
                    left is Variable
                        && left.name == respectTo
                        && right is Constant
                        && right.value != 0.0 -> Power(left, Constant(right.value - 1.0))

                    else -> error("Not supported!")
                }
            }

            override fun toString() = "($left ^ $right)"
        }

        class Division(left: MathExpression, right: MathExpression) : BinaryOperation(left, right) {
            override fun differentiate(respectTo: String): MathExpression {
                throw UnsupportedOperationException("not implemented")
            }

            override fun simplify(): MathExpression {
                return when {
                    right is Constant && right.value == 1.0 -> left.simplify()

                    else -> Division(left.simplify(), right.simplify())
                }
            }

            override fun toString() = "($left / $right)"
        }

    }

}

operator fun MathExpression.plus(other: MathExpression) = MathExpression.BinaryOperation.Addition(this, other)
operator fun MathExpression.minus(other: MathExpression) = MathExpression.BinaryOperation.Subtraction(this, other)
operator fun MathExpression.times(other: MathExpression) = MathExpression.BinaryOperation.Multiplication(this, other)
operator fun MathExpression.div(other: MathExpression) = MathExpression.BinaryOperation.Division(this, other)

operator fun MathExpression.plus(other: Double) = MathExpression.BinaryOperation.Addition(this, MathExpression.Constant(other))
operator fun MathExpression.minus(other: Double) = MathExpression.BinaryOperation.Subtraction(this, MathExpression.Constant(other))
operator fun MathExpression.times(other: Double) = MathExpression.BinaryOperation.Multiplication(this, MathExpression.Constant(other))
operator fun MathExpression.div(other: Double) = MathExpression.BinaryOperation.Division(this, MathExpression.Constant(other))

fun expression(expression: (MathExpression) -> MathExpression): MathExpression {
    return expression(DefaultVariable)
}

class MathExpressionMatcher(init: MathExpressionMatcher.() -> ((MathExpression) -> Boolean)) {

    val constant: (MathExpression) -> Boolean = { it is MathExpression.Constant }
    val variable: (MathExpression) -> Boolean = { it is MathExpression.Variable }

    operator fun ((MathExpression) -> Boolean).plus(other: (MathExpression) -> Boolean) : (MathExpression) -> Boolean = {
        it is MathExpression.BinaryOperation.Addition &&
            ((this(it.left) && other(it.right)) || (other(it.left) && this(it.right)))
    }

    operator fun ((MathExpression) -> Boolean).times(other: (MathExpression) -> Boolean) : (MathExpression) -> Boolean = {
        it is MathExpression.BinaryOperation.Multiplication &&
            ((this(it.left) && other(it.right)) || (other(it.left) && this(it.right)))
    }

}

fun testMatch(expression: MathExpression, init: MathExpressionMatcher.() -> (MathExpression) -> Boolean) {
    val matcher = MathExpressionMatcher(init)

    
}