# Contributing to SMPRPG

Thank you for taking the time to contribute! 

Before starting, understand that this repository is primarily a **Kotlin** codebase. Any remaining Java code is
considered **legacy**, and is scheduled for migration. It is **highly recommended** to use Kotlin wherever possible, but
it is still okay to utilize Java if necessary for the time being.

## Styleguide

### Commit Messages

Commit messages are pretty straightforward. You should follow the basic principle of:
"When applied, this commit will.... [COMMIT MESSAGE]". Your commit messages should always
be prefixed with the "topic" of the commit. This can be thought of as a one word descriptive word that
relates to what change you made in the codebase. For example, take the commit message:

`items: buff stone sword damage`

Just from this commit message, we know that "when applied, it will" buff stone sword damage. We also know that
the commit relates to items in some way. Use your best judgement!

### General Coding Standards

Code should aim to be self documenting. What this means, is that you shouldn't rely on comments to portray what
your code is doing. You can use various techniques to accomplish this, such as "magic value" prevention and well named variables.
[Here is a good video](https://www.youtube.com/watch?v=Bf7vDBBOBUA) that perfectly describes what im talking about.

Code should also try not to be nested too much. If your code starts looking like a staircase, it probably needs to be refactored.
Once again, [here is a great video](https://youtu.be/CFRhGnuXG-4?si=GJ-z4marerFxcsBn) going in depth on just how much
of an impact things like guard clauses and structure design can affect code cleanliness.

There should be well written Javadocs on *most* methods and class definitions. Especially in a Minecraft plugin context
where we have to spend time fighting with vanilla mechanics, it should be immediately evident why some code exists in the project.

### Kotlin Standards (Work in Progress)

As a general rule of thumb, just follow the
[coding conventions outlined by the Kotlin developers](https://kotlinlang.org/docs/coding-conventions.html) themselves.
There are however a couple of things I disagree with that should not be followed:

- Never keep "nested" operations and/or guard clauses on one line. 
```kotlin
// Bad
if (!valid) return

// Bad
if (valid) if (expired) println("expired!")

// Good
if (!valid) 
    return

// Good
if (valid)
    if (expired)
        println("expired!")
```

- When implementing event handlers, they should satisfy the following requirements:
1. The `Suppress("unused")` annotation is also present.
2. The function is private*.
3. The function starts with the `on` prefix. e.g. `onPlayerJoin()`
```kotlin
// Good
@EventHandler
@Suppress("unused")
private fun onPlayerJoin(event: PlayerJoinEvent) {
    event.player.sendMessage(Component.text("hey! :)"))
} 
```
\*Note: There is a caveat to this that can lead to unintended behavior. If a child class extends the class that contains
the listener, and it is marked as private in the parent class, the child class will **not** register the listener when 
registered. If the child class **must** also register the listener from a parent class, it is alright to leave the event 
handler to be public. Just keep in mind, that in most circumstances this is a symptom of poorly designed code structure,
and that you may need to rethink where your event handler is going!


### Java Standards (Work in Progress)

Java code is considered legacy, but there may come times when you need to contribute Java code.
Java code is also a lot more lenient than Kotlin, but good standards should still be followed.
A good general rule of thumb, is your inspector should have no warnings on default settings.
You should also follow the same ideologies that the Kotlin section brings up, as they apply to Java as well. 

- When implementing event handlers, they should satisfy the following requirements:
1. The function is private*.
2. The function starts with the `__on` prefix. e.g. `__onPlayerJoin()`
```java
// Good
@EventHandler
private void __onPlayerJoin(PlayerJoinEvent event) {
    event.getPlayer().sendMessage(Component.text("hey! :)"));
} 
```
\*Note: There is a caveat to this that can lead to unintended behavior. If a child class extends the class that contains
the listener, and it is marked as private in the parent class, the child class will **not** register the listener when
registered. If the child class **must** also register the listener from a parent class, it is alright to leave the event
handler to be public. Just keep in mind, that in most circumstances this is a symptom of poorly designed code structure,
and that you may need to rethink where your event handler is going!