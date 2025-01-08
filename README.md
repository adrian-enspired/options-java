Just sayin', you've got _options_

Simple boilerplate for declaring system options.

## installation and dependencies

No external dependencies.
Plays well with serialization tools (for example, jackson).

Recommended installation method is to use maven. Check the latest package on github.

## usage

To create new options, simply list those options in an Enum that implements the `Option` interface:
```java
enum Color implements Option {
  Red, Blue, Yellow;
}
```

You can then assign and check various combinations of options:
```java
var green = new OptionSet<Color>(Color.Blue, Color.Yellow) {};
green.has(Option.Red);    // false
green.has(Option.Blue);   // true
green.has(Option.Yellow); // true

var blue = green.xor(Option.Yellow);
green.has(Option.Red);    // false
green.has(Option.Blue);   // true
green.has(Option.Yellow); // false
```

Keep in mind that Options are not _settings_: they have no state or semantic value; they are simply present or not.
You should avoid conflicting/exclusive options (for exmaple, you should have "loud" _or_ "quiet", but not both).

The actual "backing values" of Options are powers of two and by default are assigned based on order.
In the example above, `Red` would be `1`, `Blue` is `2`, and `Yellow` is `4`; the value of `green` was `6`.
Generally speaking, the specific value should not be important.
If it is, you can override `Option.value()` to set them as desired, though they must still be powers of two in order to work correctly.

## tests

Tests are still being added. you can run the test suite via maven:
```
mvn test
```

Additionally, when you implement your own option enum, you can extend the abstract `OptionTest` to test them. There's not much there to mess up... but you can still see the green.

## contributing or getting help

I'm on IRC at [libera#__adrian](https://web.libera.chat/#__adrian), or open an issue on github. Feedback is welcomed as well!
