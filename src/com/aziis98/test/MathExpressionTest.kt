package com.aziis98.test

import com.aziis98.math.*
import com.aziis98.math.MathExpression.Variable.DefaultVariable
import org.junit.*
import org.junit.Assert.*

// Copyright 2016 Antonio De Lucreziis

class MathExpressionTest {

    @Test
    fun test1() {
        // val evaluate1 = evaluate(DefaultVariable * DefaultVariable + 1.0, 4.0)

        // assertEquals(evaluate1, 17.0, Double.MIN_VALUE)

        val expr1 = expression { x -> x * x * x + 1.0 }

        println(expr1)

        println(expr1.simplify())

        val diff1 = expr1.differentiate()

        println(diff1)

        println(diff1.simplify())
    }

}