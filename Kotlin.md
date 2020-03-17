# Kotlin

_Lisa's Notes_

# [Kotlin Koans](https://kotlinlang.org/docs/tutorials/koans.html)

- `object expression` plays the same role in Kotlin as an anonymous class in Java

- `sequences` allow you to perform operations **lazily** rather than eagerly. 

- `var` keyword (**mutable**), `val` keyword (**read-only**).

## Scope Functions

[scope-functions](https://kotlinlang.org/docs/reference/scope-functions.html#scope-functions)

`Context object`: **this** or **it**

Inside the lambda of a scope function, the context object is available by a short reference instead of its actual name. Each scope function uses one of two ways to access the context object: 
- as a `lambda receiver` (**this**) 
- or as a `lambda argument` (**it**). 


## Generic functions

[Generic functions](https://kotlinlang.org/docs/reference/generics.html#generic-functions)

- Type parameters are placed before the name of the function


# Kotlin for Java Developers [Coursera](https://www.coursera.org/learn/kotlin-for-java-developers/)

- you can define functions at the top level
- **main** can be with or without arguments
- `if` is an expression!
- Kotlin is a statically typed language, can do type inference

## Basics

### Variables

- `val`: read-only (cannot be reassigned)
- `var`: mutable

### Functions

```kotlin
// Top-level function:
fun topLevel() = 1

// Member function:
class A {
    fun member() = 2
}

// Local function:
fun other() {
    fun local() = 3
}
```

### Extensions

`Extensions` are **static** Java functions under the hood, so no **override** for extension functions in Kotlin.

# Others

## Plugins

### `optics`

```kotlin
@optics
data class Street(val number: Int, val name: String) {
    companion object
}

@optics
data class Address(val city: String, val street: Street) {
    companion object
}

@optics
data class Company(val name: String, val address: Address) {
    companion object
}

@optics
data class Employee(val name: String, val company: Company) {
    companion object
}

fun main() {
    val employee = Employee("John Doe", Company("Arrow", Address("Functional city", Street(23, "lambda street"))))

    // Modify an object
    // this doesn't work
    // e2.company.address.street.name = "new addr"

    // approach 1: nested copy
    val e2 = employee.copy(
        company = employee.company.copy(
            address = employee.company.address.copy(
                street = employee.company.address.street.copy(
                    name = "New street name"
                )
            )
        )
    )
    println(e2)

    // approach 2: lens composition
    val e3: Employee = Employee.company.address.street.name.set(employee, "New street name with lenses")
    println(e3)

    val e4: Employee = Employee.company.address.street.name.modify(employee) { originalStreetName -> originalStreetName.toUpperCase() }
    println(e3 === e4)


    // Comparison
    val e5 = employee.copy()
    println(employee == e5)
    println(employee === e5)
}
```

## REST API with Kotlin and Spring Boot

```kotlin
// https://scotch.io/@grahamcox82/how-to-build-a-simple-rest-api-with-kotlin-and-spring-boot

@RestController
@RequestMapping("/foo")
class BarController {
    companion object

    /**
     * Pretend to create a new user
     * @param user The details of the user to create
     */
    @PostMapping("/user", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(@RequestBody user: NewUser): User {
        return User(
            username = user.username,
            screenName = user.screenName,
            email = user.email,
            registered = Instant.now()
        )
    }
}

/**
 * Representation of a User
 * @property username The username of the user
 * @property screenName The screen name of the user
 * @property email The email address of the user
 * @property registered When the user registered with us
 */
data class User(
    val username: String,
    val screenName: String,
    val email: String,
    val registered: Instant
)

/**
 * Representation of a User to create
 * @property username The username of the user
 * @property screenName The screen name of the user
 * @property email The email address of the user
 */
data class NewUser @JsonCreator constructor(
    val username: String,
    val screenName: String,
    val email: String
)
```
