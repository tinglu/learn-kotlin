 # I. Kotlin

_Lisa's Notes_

# [Kotlin for Java Developers Coursera](https://www.coursera.org/learn/kotlin-for-java-developers/)

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

- `Extensions` are **static** Java functions under the hood,
- so no **override** for extension functions in Kotlin.
- member function wins over extension

## Properties

### Lazy or Late initialization

`lazy property`:

```kotlin
val lazyValue: String by lazy {
    println("computed!")
    "Hello"
}

fun main(args: Array<String>) {
    println(lazyValue)
    println(lazyValue) // only computed once!
}
// computed!
// Hello
// Hello

fun main(args: Array<String>) {
    // no lazyValue usage
}
// nothing printed!
```

`lateinit`:
- can't be nullable (and the **non-nullable** variable can be initialized later)
- can't be `val`!
- can't be primitive type (only reference types might be initialized with `null`)
```kotlin
lateinit var myData: MyData
```


## OOP in Kotlin

### Declarations

- **public (default)**, private, internal (visible in a module, NO package private)
- **final (default)**, open (non-final), abstract

| Modifier  | Class member          | Top-level declaration |
| --------- | --------------------- | --------------------- |
| public    | visible everywhere    | visible everywhere    |
| internal  | visible in teh module | visible in teh module |
| protected | visible in subclasses | ---                   |
| private   | visible in teh class  | visible in the file   |

| Kotlin modifier | JVM level                 |
| --------------- | ------------------------- |
| public          | public                    |
| protected       | protected                 |
| private         | private / package private |
| internal        | public & _name mangling_  |

- one file may contain several classes and top-level functions

### Constructors, Inheritance syntax

`val/var` on a parameter creates a property:
```kotlin
class Person(val name: String)
```


Same syntax for `extends` & `implements`:
```kotlin
interface Base
class BaseImpl : Base

open class Parent
class Child : Parent()
```

Calling a constructor of the parent class:
```kotlin
open class Parent(val name: String)
class Child(name: String) : Parent(name)

open class Parent(val name: String)
class Child : Parent {
    constructor(name: String, param: Int) : super(name)
}
```

Overriding a `property` is overriding a `getter`!
```kotlin
open class Parent {
    open val foo = 1
    init {
        println(foo) // when initialised by Child, this calls Child's getFoo() getter where variable foo is not initialised yet in Child class!
    }
}

class Child: Parent() {
    override val foo = 2
}

fun main() {
    Child()
}
```


## Class modifiers

### data class

`data` modifier generates useful methods: `euqals`, `hashCode`, `copy`, `toString`, etc.


`Equals`:
```kotlin
val set1 = setOf(1, 2, 3)
val set2 = setOf(1, 2, 3)
set1 == set2 // true
```

`Reference quality`:
```kotlin
set1 === set2 // false
```

```kotlin
// Every class inherits default equals implements (reference quality comparison)
class Foo(val first: Int, val second: Int)
val f1 = Foo(1, 2)
val f2 = Foo(1, 2)
println(f1 == f2) // false

data class Bar(val first: Int, val second: Int)
val b1 = Bar(1, 2)
val b2 = Bar(1, 2)
println(b1 == b2) // true
```

Compiler only generate code with properties defined in primary constructors, so to exclude a property from the generated implementations, declare it in the class body:
```kotlin
data class User(val email: String) {
    val nickname: String? = null
}
val user1 = User("abc@email.com")
user1.nickname = "name111"

val user2 = User("abc@email.com")
user2.nickname = "name222"

println(user1 == user2) // true!
```


### Class hierarchy

```kotlin
interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

fun eval(e: Expr): Int = when (e) {
    is Num -> e.value
    is Sum -> eval(e.left) + eval(e.right)
    else -> throw IllegalArgumentException("Unknown expression") // this is needed to make the when() exhaustive!
}
```

`sealed` class can solve the exhaustive problem - it restricts class hierarchy:
- all subclasses must be located in the same file
- `sealed` class has a **private default constructor** so it won't accidentally instantiate this class from Java or create subclasses
```kotlin
sealed class Expr
class Num(val value: Int) : Expr()
class Sum(val left: Expr, val right: Expr) : Expr()

fun eval(e: Expr): Int = when (e) {
    is Num -> e.value
    is Sum -> eval(e.left) + eval(e.right)
    // else branch is not needed
}
```

### Nested and Inner classes

Which class(nested or inner) stores a **reference** to an outer class:

|       In Java        |      In Kotlin       | Class declared within another class |
| :------------------: | :------------------: | :---------------------------------: |
|    static class A    | class A (by default) |            nested class             |
| class A (by default) |    inner class A     |             inner class             |


```kotlin
class A {
    class B
    inner class C { // inner is a Singleton
        ...this@A...
    }
}
```

### Class delegation

```kotlin
interface Repository {
    fun getById(id: Int): Customer
    fun getAll(): List<Customer>
}

interface Logger {
    fun logAll()
}

class Controller(
    repository: Repository,
    logger: Logger
) : Repository by repository, Logger by logger // by keyword means delegating to the following instances

fun use(Controller: Controller) {
    controller.logAll()
}
```

### Objects

`object` in Kotlin is a **Singleton **

#### object expression

`object expression` is **NOT** a singleton - a new instance of object expression is created for each call:
```kotlin
window.addMouseListener(
  object : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {
      // ...
    }
    override fun mouseEntered(e: MouseEvent) {
      // ...
    }
  }
)
```

#### Companion object

`companion object` is a special nested object inside a class:
```kotlin
class A {
    companion object {
        fun foo() = 1
    }
}

fun main(args: Array<String>) {
    A.foo()
}
```

`companion object` can implement an interface
```kotlin
interface Factory<T> {
    fun create(): T
}

class A {
    private constructor()

    companion object : Factory<A> {
        override fun create : A {
            return A()
        }
    }
}
```

`companion object` can be a receiver of extension function
```kotlin
// business logic module
class Person(val firstName: String, val lastName: String) {
    companion object { ... }
}

// client/server communication module
fun Person.Companion.fromJson(json: String): Person {
    ...
}

val p = Person.fromJSON(json)
```

#### NO static keyword

Declare "static" members:
- at the top-level function (cannot access private property of the class from the top-level function outside of this class)
- inside objects
- inside companion objects

### Constants

#### `const`

- `const` (for primitive types and String)

```kotlin
// Compile-time constants:
const val answer = 12 // The value is inlined
```

#### `@JvmField`

- `@JvmField` (eliminates accessors)
- `@JvmField` exposes a Kotlin property as a field in Java
```kotlin
@JvmField
val prop = MyClass() // the val has No getter!

// the same as in Java
public static final MyClass prop = new MyClass()
```

#### @JvmField in `object` vs. in `class`:

```kotlin
object A {
    @JvmField
    val prop = MyClass() // static field generated
}

class B {
    @JvmField
    val prop = MyClass() // regular field generated
}
```

#### Property annotations comparison

##### (1) the field is private by default
```kotlin
object SuperComputer {
    val answer = 42
}

// in Java
SuperComputer.INSTANCE.getAnswer() // access only by getter of an instance because the field is private by default!
```

##### (2) `@JvmStatic` access as a static member
```kotlin
object SuperComputer {
    @JvmStatic
    val answer = 42
}

// in Java
SuperComputer.getAnswer() // access by getter as a static member - field isn't exposed - doesn't make sense to make property static
```

##### (3) `@JvmField` expose the field as a `static` field when used from Java
```kotlin
object SuperComputer {
    @JvmField
    val answer = 42
}

// in Java
SuperComputer.answer // access by a field
```

##### (4) `const` inline the property name by its value & exposed as a `static` field when used from Java
```kotlin
object SuperComputer {
    const val answer = 42
}

// in Java
println(SuperComputer.answer) // ==> will be inlined as println(42)
```

### Generics

#### Non-nullable upper bound
```kotlin
fun <T : Any> foo(list: List<T>) {
    for (element in list) {
    }
}

foo(listOf(1, null)) // Error: element cannot be null because the inferred type Int? is not a subtype of Any
```

#### Multiple constraints for a type parameter using `when`
```kotlin
fun <T> ensureTrailingPeriod(seq: T) where T : CharSequence, T : Appendable {
    if (!seq.endsWith('.')) {
        seq.append('.')
    }
}
```

### Solve platform declaration clash when having the same JVM signature by using `@JvmName`
```kotlin
fun List<Int>.average(): Double { ... }

@JvmName("averageOfDouble")
fun List<Double>.average(): Double { ... }
```

----------------------------------------------------------------------

# II. [Kotlin Koans](https://kotlinlang.org/docs/tutorials/koans.html)

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













----------------------------------------------------------------------

# III. Others

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
