_Lisa's Notes of Learning Kotlin_

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

## Extensions

- `Extensions` are **static** Java functions under the hood,
- so no **override** for extension functions in Kotlin.
- member function wins over extension



## Nullability

### Nullability operators

`elvis operator` (`?:`)
```kotlin
val s: String?
val length: Int = if (s != null) s.length else 0
// ==> same as
val length: Int = s?.length ?: 0
```

Not-null assertion `!!`
```kotlin
val s: String?
s!! // throws NPE if s is null
```


**Prefer safe access `?.`, elvis operator `?:`, `if-checks` over not-null operator `!!`**


### Optional types

**Optional type is a wrapper that stores the reference to the initial object. For each optional value, an extra object is created.**

```kotlin
class Optional<T>(val value: T) {
    fun isPresent() = value != null
    fun get() = value ?: throw NoSuchElementException("No value present")
}
```



### Nullable types under the hood

**Only one object at runtime. No wrapper created. Implemented by annotations.**

```kotlin
fun foo(): String = "foo"
fun bar(): String? = "bar"

// ===> under the hood:
@NotNull
public static final String foo() {
    return "foo";
}
@Nullable
public static final String bar() {
    return "bar";
}
```


### Type cast

- type cast: `as`
- safe cast: `as?`








## Functional Programming


### Lambda syntax

```kotlin
// e.g. full syntax
list.any({ i: Int -> i > 0 })
// the lambda can be moved out of the parentheses when it is the last argument
list.any() { i: Int -> i > 0 }
// the parentheses can be omitted if it is empty
list.any { i: Int -> i > 0 }
// type can be omitted if it's clear from the context
list.any { i -> i > 0 }
// **it** denotes the argument if it's the only one
list.any { it > 0 }
```

#### multi-line lambda
```kotlin
list.any {
    println("processing $it")
    it > 0 // the last expression is the result
}
```

#### Destructuring declarations
```kotlin
// e.g.
map.mapValues { entry -> "${entry.key} -> ${entry.value}" }
// use destructuring declarations syntax instead
map.mapValues { (key, value) -> "$key -> $value" }
// omit the parameter name if it's unused
map.mapValues { (_, value) -> "$value" }
```


### Common operations on collections

- filter
- map
- any / all / none
- find / first / firstOrNull (they do the same thing)
- count
- partition
- groupBy
- associateBy (duplicates will be removed & only the LAST element is chosen!)
- associate
- zip (remaining elements from the longer list will be ignored)
- zipWithNext `listOf(1,2,3,4...).zipWithNext() --> listOf((1,2), (2,3), (3,4)...)`
- flatten
- flatMap (combines two operations `map` & `flatten`)
- reduce
- groupBy
- ...


### Simplifying code

- don't use `it` if it has different types in neighboring lines (only use `it` if the lambda is trivial and straightforward)
- prefer explicit parameter names if it might be confusing otherwise
- learn the library and try to reuse the library functions as much as possible



### Function types

#### Calling lambda directly

```kotlin
{ println("hey!") }() // possible but strange
run { println("hey!") } // use **run** instead
```

#### Function types and nullability

- `() -> Int?`: return type is nullable
- `(() -> Int)?`: the variable is nullable

```kotlin
val f1: () -> Int? = null // (x) NOT compile
val f2: () -> Int? = { null } // a lambda without arguments that always returns null
val f3: (() -> Int?) = null // either lambda returning Int or null reference
val f4: (() -> Int?) = { null } // (x) NOT compile
```

#### Working with a nullable function type

```kotlin
val f3: (() -> Int?) = null

if (f3 != null) {
    f3()
}
// OR
f3?.invoke()
```


### Member references

```kotlin
class Person(val name: String, val age: Int)
people.maxBy { it.age } // convert lambda to reference
people.maxBy(Person::age) // Class::member
```

- You can store lambda in a variable but you can't store a function in a variable:
```kotlin
val isEven: (Int) -> Boolean = { i: Int -> i % 2 == 0 } // OK

fun isEven(i: Int): Boolean = i % 2 == 0
val predicate = isEven // COMPILER ERROR!
val predicate = ::isEven // OK - use function reference
val predicate = { i: Int -> isEven(i) } // OK
```

- If the function called inside a lambda only takes 0 or 1 argument, then explicit function call in a lambda is more concise than using member references. But when the lambda has multiple parameters, and the function called takes the same parameters in the same order, then **member references allow you to hide all the parameters because the compiler can infer the types**:
```kotlin
val action = { person: Person, message: String ->
    sendEmail(person, message)
}

val action = ::sendEmail // More concise in this case
```


- You can pass function reference as an argument:
```kotlin
fun isEven(i: Int): Boolean = i % 2 == 0

val list = listOf(1,2,3,4)
list.any(::isEven)
list.filter(::isEvent)
```

- `Bound` vs. `Non-bound` reference
```kotlin
class Person(val name: String, val age: Int) {
    fun isOlder(ageLimit: Int) = age > ageLimit
}

// Non-bound reference
val agePredicate = Person::isOlder
// because the full function is:
val agePredicate: (Person, Int) -> Boolean = { person, ageLimit ->
    person.isOlder(ageLimit)
}
val alice = Person("Alice", 29)
agePredicate(alice, 21) // true

// Bound reference
val alice = Person("Alice", 29)
val agePredicate = alice::isOlder // bound to this specific instance
// the full function is:
val agePredicate: (Int) -> Boolean = { ageLimit ->
    alice.isOlder(ageLimit)
}
agePredicate(21) // true
```

- Bound to `this` reference
```kotlin
class Person(val name: String, val age: Int) {
    fun isOlder(ageLimit: Int) = age > ageLimit

    fun getAgePredicate() = this::isOlder // bound to this instance
    fun getAgePredicate() = ::isOlder // this can be omitted
}
```

- a function reference without left handside is either a **global** reference or a **bound** reference
```kotlin
fun isEven(i: Int): Boolean = i % 2 == 0

val list = listOf(1,2,3,4)
list.any(::isEven) // reference to a global function
list.filter(::isEvent)
```

### Return from lambda

`return` in Kotlin always return from a function marked with `fun`:
```kotlin
fun duplicateNonZero(list: List<Int>): List<Int> {
    return list.flatMap {
        if (it == 0) return listOf() // return from duplicateNonZero fun!
        listOf(it, it)
    }
}
println(duplicateNonZero(listOf(3,0,5))) // []
```

- `return` from lambda using **annotation**
```kotlin
// won't break the outer function
fun duplicateNonZero(list: List<Int>): List<Int> {
    list.flatMap{
        if (it == 0) return@flatMap listOf<Int>()
        listOf(it, it)
    }
}
// same as
fun duplicateNonZero(list: List<Int>): List<Int> {
    list.flatMap l@{
        if (it == 0) return@l listOf<Int>()
        listOf(it, it)
    }
}
```

- `return` using **local function**
```kotlin
fun duplicateNonZero(list: List<Int>): List<Int> {
    fun duplicateNonZeroElement(e: Int): List<Int> {
        if (e == 0) return listOf()
        return listOf(e, e)
    }
    return list.flatMap(::duplicateNonZeroElement)
}
println(duplicateNonZero(listOf(3,0,5))) // [3,3,5,5]
```

- `return` using **anonymous function**
```kotlin
fun duplicateNonZero(list: List<Int>): List<Int> {
    return list.flatMap(fun (e): List<Int> { // can specify type List<Int> which is not possible using lambda
        if (e == 0) return listOf() // will only return from the anonymous function
        return listOf(e, e)
    })
}
println(duplicateNonZero(listOf(3,0,5))) // [3,3,5,5]
```

- NOT using `return`
```kotlin
fun duplicateNonZero(list: List<Int>): List<Int> {
    return list.flatMap {
        if (it == 0)
            listOf()
        else
            listOf(it, it)
    }
}
println(duplicateNonZero(listOf(3,0,5))) // [3,3,5,5]
```


## Kotlin is a not a purely functional language

- it combines different paradigms
- if you reply on immutability, higher-order functions, lambdas, function types, you're doing Kotlin in the functional style



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
| internal  | visible in the module | visible in the module |
| protected | visible in subclasses | ---                   |
| private   | visible in the class  | visible in the file   |

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


### Class modifiers

#### data class

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


#### Class hierarchy

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
- **all subclasses must be located in the same file**
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

#### Nested and Inner classes

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

#### Class delegation

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

`object` in Kotlin is a **Singleton**

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

#### Multiple constraints for a type parameter using `where`
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


## Inline functions

### Library functions looking like built-in constructs

All these functions are declared as `inline` functions - no performance overhead when you use these, no anonymous class or extra objects are created for lambda under the hood:

- `run`
- `let`
- `takeIf`
- `takeUnless`
- `repeat`
- `withLock`
- `use`

_Don't optimise prematurely!_

### Comparison of `run`, `with`, `let`, `also` and `apply`

// TODO

- this vs. it
- how to return


`@InlineOnly` specifies the function should not be called directly without inlining:
```kotlin
@kotlin.internal.InlineOnly
public inline fun <R> run(block: () -> R): R = block()
```


## Collections vs. Sequences

### Collections

Operations on collections:
- Lambdas are inlined (**no performance overhead**): `filter`, `map`, `any`, `find`, `groupBy`
- but **intermediate collections** are created for chained calls (**significant performance overhead** if not simple situation), i.e. `intermediate operations` return new collections as a result: `filter`, `map`, `groupBy`

### Sequences

`asSequence()` - from list to sequence

- Lambdas are **NOT** inlined

If the sequence operation returns another sequence, which is produced lazily, it's called `intermediate`, and otherwise the operation is `terminal`, e.g. `toList`, `max`.

### Collections vs Sequences

| Operations on Collections                             | Operations on Sequences |
| ----------------------------------------------------- | ----------------------- |
| **eager** evaluation                                  | **lazy** evaluation     |
| horizontal evaluation                                 | vertical evaluation     |
| intermediate collections are created on chained calls | lambda are not inlined  |

### Creating sequences

```kotlin
fun mySequence() = sequence {
    println("yield one element")
    yield(1)
    println("yield a range")
    yieldAll(3..5)
    println("yield a list")
    yieldAll(listOf(7,9))
}

println(mySequence()
    .map { it * it }
    .filter { it > 10 }
    .take(1))
    # fun <T> Sequence<T>.take(n: Int): Sequence<T>
    # Returns a sequence containing first [n] elements
    # take() is an intermediate operation
    # So no "yield..." will be printed

println(mySequence()
    .map { it * it }
    .filter { it > 10 }
    .first())
    # first() is a terminal operation and this will print:
    # yield one element
    # yield a range
    # 16
```

## Lambda with Receiver

_If the curly braces **{ }** are colored in **bold**, they surround lambdas!_

### `with` function

```kotlin
// repeat the variable name several times
val sb = StringBuilder()
sb.appendln("Alphabet: ")
for (c in 'a'..'z') {
    sb.append(c)
}
sb.toString()

// using `with`
val sb = StringBuilder()
with (sb) { // the lambda is the second argument of with function
    appendln("Alphabet: ") // this.appendln("Alphabet: ") `this` is an implicit receiver in the lambda (omitted in the syntax)
    for (c in 'a'..'z') {
        append(c)
    }
    toString()
}
```

### Lambda vs. Lambda with receiver

|                    |                      |
| ------------------ | -------------------- |
| regular function   | regular lambda       |
| extension function | lambda with receiver |

Inside the body of extension functions or lambda with receiver functions, `this` can be used as an implicit receiver and can be omitted.

```kotlin
// regular lambda
val isEven: (Int) -> Boolean = { it % 2 == 0 }
// lambda with receiver (put receiver type before dot and parameter types inside parenthesis)
val isOdd: Int.() -> Boolean = { this % 2 == 1 }

// calling regular lambda as regular function
isEven(0)
// calling lambda with receiver as extension function
1.isOdd()
```

### `buildString` function

```kotlin
val s: String = buildString { // lambda with receiver
    appendln("Alphabet: ")
    for (c in 'a'..'z') {
        append(c)
    }
}

// Note this inlined, so no extra objects for storing lambda will be created
inline fun buildString(
    builderAction: StringBuilder.() -> Unit
): String {
    val stringBuilder = StringBuilder()
    stringBuilder.builderAction()
    return stringBuilder.toString()
}
```

### `with` function declaration

```kotlin
inline fun <T, R> with(
    receiver: T,
    block: T.() -> R
): R = receiver.block()
```

### More useful library functions

```kotlin
inline fun <T, R> with(receiver: T, block: T.() -> R): R = receiver.block()

inline fun <T, R> T.run(block: T.() -> R): R = block()

inline fun <T, R> T.let(block: (T) -> R): R = block(this)

inline fun <T> T.apply(block: T.() -> Unit): T { block(); return this }

inline fun <T> T.also(block: (T) -> Unit): T { block(this); return this }
```

```kotlin
with (window) {
    width = 300
    height = 200
    isVisible = true
}

// run: like with, but extension
val windowOrNull = windowById["main"]
windowOrNull?.run { // `run` can be used with safe access
    width = 300
    height = 200
    isVisible = true
}

// apply: returns receiver as a result
val mainWindow =
    windowById["main"]?.apply {
        width = 300
        height = 200
        isVisible = true
    } ?: return // assign the variable only if the result is not null; otherwise, stop the execution and return from the outer function

// also: regular argument instead of this
windowById["main"]?.apply {
    width = 300
    height = 200
    isVisible = true
}?.also {
    showWindow(it)
}
```


|                             |          { .. this .. }           |           { .. it .. }            |
| --------------------------- | :-------------------------------: | :-------------------------------: |
| **return result of lambda** |          `with` / `run`           |               `let`               |
| **return receiver**         |              `apply`              |              `also`               |
|                             | receiver.apply { this.actions() } | receiver.also { moreActions(it) } |



## Types

### Basic types

In Kotlin, there's **NO** primitive types.

But under the hood, there're still primitive types in the bytecode level:

| Kotlin                        | Java                                                         |
| ----------------------------- | ------------------------------------------------------------ |
| `Int`, `Double`, `Boolean`    | `int`, `double`, `boolean`                                   |
| `Int?`, `Double?`, `Boolean?` | `java.lang.Integer`, `java.lang.Double`, `java.lang.Boolean` |

**Generic arguments:**

When Int is used as a generic argument, it cannot be compiled to a primitive because you cannot use primitives as generic arguments. It becomes a reference type.

| Kotlin                                                       | Java            |
| ------------------------------------------------------------ | --------------- |
| `List<Int>`                                                  | `List<Integer>` |
| `Array<Int>`                                                 | `Integer[]`     |
| `IntArray` (to get Arrays of primitive types under the hood) | `int[]`         |

**String:**

Kotlin doesn't have String implementation. It's replaced by `java.lang.String` under the hood.

| Kotlin          | Java               |
| --------------- | ------------------ |
| `kotlin.String` | `java.lang.String` |

But `kotlin.String` has some modifications which hide some confusing methods:

```java
// in Java
"one.two.".replaceAll(".", "*") // get "********" because "." is treated as regex
```

```kotlin
// in Kotlin
"one.two.".replace(".", "*") // get "one*two*" as expected
"one.two.".replace(".".toRegex(), "*") // get "********"
```

**Any:**

`Any` in Kotlin is a super type of all non-nullable types (reference types and corresponding primitive types).

| Kotlin | Java               |
| ------ | ------------------ |
| `Any`  | `java.lang.Object` |

_Autoboxing under the hood_: Same as Java, when passing an integer as an argument to a function that expects any, it will be autoboxed to a reference type.

```kotlin
log(2017)

fun log(any: Any) {
    println("Value: $any")
}

// instead
fun log(i: Int) {
    println("Value: $i")
}
```

**Function types:**

Unless Kotlin lambdas are inlined, the Kotlin function types are replaced with regular interfaces with the corresponding generic arguments.

| Kotlin            | Java                     |
| ----------------- | ------------------------ |
| () -> Boolean     | Function0<Boolean>       |
| (Order) -> Int    | Function1<Order, Int>    |
| (Int, Int) -> Int | Function2<Int, Int, Int> |

```Kotlin
package kotlin.jvm.functions

interface Function0<out R> : Function<R> {
    operator fun invoke(): R
}

interface Function1<in P1, out R> : Function<R> {
    operator fun invoke(p1: P1): R
}

interface Function2<in P1, in P2, out R> : Function<R> {
    operator fun invoke(p1: P1, p2: P2): R
}
```

**Array comparison in Kotlin:**

```kotlin
val ints1 = intArrayOf(1, 2)
val ints2 = intArrayOf(1, 2)
println(ints1 == ints2) // false, == is calling equals() which is reference comparison
println(ints1.contentEquals(ints2)) // true
```

**In Kotlin, prefer Lists to Arrays.**, array list has similar performance to array.

### Kotlin type hierarchy

- `Any` is the top type in Kotlin
- `Nothing` is the bottom type in Kotlin

```text
        Any
         |
String ...  User ...
         |
       Nothing
```

| Kotlin  | Java |                                        |
| ------- | ---- | -------------------------------------- |
| Unit    | Void | No meaningful value is returned        |
| Nothing | Void | Because there's no Nothing type in JVM |

- `Unit`: a type that allows only _one_ value and thus can hold no information - the function completes successfully
- `Nothing`: a type that has _no_ value - the function never completes

```Kotlin
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}

fun infiniteLoop(): Nothing {
    while(true) {}
}
```

`Nothing` type expression examples:
- `throw IllegalStateException()`
- `return`
- `TODO("Needs to be done")`


### Nullable types

Prevent Null Pointer Errors:

1. Annotate Java types:
   - `@Nullable Type` in Java -> `Type?` in Kotlin
   - `@NotNull Type` in Java -> `Type` in Kotlin

2. Specify types explicitly

You can only annotate nullable types and set Non-null by default (supported by JSR-305): `@ParametersAreNonnullByDefault`, `@MyNonnullByDefault`.

### Collection types

`List` (Read-only) & `MutableList` are two interfaces declared in `kotlin.collections` package; `MutableList` extends `List`.

`Read-only` $ \neq $ `immutable`:
- Rea-only interface just lacks mutating methods;
- The actual list can changed by another reference.

```Kotlin
// E.g.
val mutableList = mutableListOf(1, 2, 3)
val list: List<Int> = mutableList
mutableList.add(4)
println(list) // [1, 2, 3, 4]
```

Kotlin uses java.util.ArrayList under the hood:

```text
kotlin.List
    |
kotlin.MutableList
    |
java.util.ArrayList
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
