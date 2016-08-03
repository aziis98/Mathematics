# Mathematics

An api that gives helpers for solving equations and differentiating expressions within Kotlin

## Differenciation

Here there is an example of how to differentiate a function

```kotlin
    val expr = expression { x -> x * x + 1.0 } // Default variable is "x"
    
    println(expression)

    val derivative = expr.differentiate() // Differenciated in respect to default variable "x"

    println(derivative)
```
    
    