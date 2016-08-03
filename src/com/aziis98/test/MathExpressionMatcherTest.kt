package com.aziis98.test

import com.aziis98.math.*
import org.junit.Assert.*
import org.junit.Test

// Copyright 2016 Antonio De Lucreziis

class MathExpressionMatcherTest {

    @Test
    fun test() {
        val matcher = MathExpressionMatcher {
            constant * variable
        }

        val expression = expression { x -> x * 2.0 * x + 1.0 }


    }

}