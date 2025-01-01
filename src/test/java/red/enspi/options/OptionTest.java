/*
 * author     Adrian <adrian@enspi.red>
 * copyright  2024
 * license    GPL-3.0 (only)
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License, version 3.
 *  The right to apply the terms of later versions of the GPL is RESERVED.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program.
 *  If not, see <http://www.gnu.org/licenses/gpl-3.0.txt>.
 */
package red.enspi.options;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Base tests for Options implementations.
 *
 * <p>Extend this test to verify your own implementations behave correctly.
 * You need to implement a source method that provides a {@code Stream<Arguments>} of the options to test.
 * This can be done quite easily in most cases:
 * <pre>
 * class YourOptionTest extends OptionTest {
 *   static Stream<Arguments> option_source() {
 *   return Arrays.stream(YourOption.values())
 *     .map((Option option) -> Arguments.of(option));
 *   }
 * }
 * </pre>
 */
abstract class OptionTest {

  @ParameterizedTest
  @MethodSource("option_source")
  void and(Option option) {
    assertTrue(option.in(option.and(option.value())), "Option must be in .and(.value())");
    assertTrue(option.in(option.and(-1)), "Option must be in .and(-1)");
    assertFalse(option.in(option.and(0)), "Option must not be in .and(0)");
  }

  @ParameterizedTest
  @MethodSource("option_source")
  void in(Option option) {
    assertFalse(option.in(0), "Option must not be .in(0)");
    assertTrue(option.in(option.value()), "Option must be in .value()");
    assertTrue(option.in(option.value() | 256), "Option must be in .value() | 256");
    assertFalse(option.in(-1 & option.not()), "Option must not be .in(-1 & .not())");
  }

  @ParameterizedTest
  @MethodSource("option_source")
  void not(Option option) {
    assertTrue((option.value() & option.not()) == 0, "Option.not() must be the inverse of its .value()");
  }

  @ParameterizedTest
  @MethodSource("option_source")
  void or(Option option) {
    assertTrue(option.in(option.or(0)), "Option must be in .or(0)");
    assertTrue(option.in(option.or(-1)), "Option must be in .or(-1)");
    assertTrue(option.in(option.or(256)), "Option must be in .or(256)");
    assertTrue(option.in(option.or(option.value())), "Option must be in .or(.value())");
  }

  @ParameterizedTest
  @MethodSource("option_source")
  void xor(Option option) {
    assertTrue(option.in(option.xor(0)), "Option must be in .xor(0)");
    assertTrue(option.in(option.xor(option.not())), "Option must be in .xor(.not())");
    assertFalse(option.in(option.xor(-1)), "Option must be in .xor(-1)");
    assertFalse(option.in(option.xor(option.value() | 256)), "Option must not be in .xor(.value() | 256)");
    assertFalse(option.in(option.xor(option.value())), "Option must not be in .xor(.value())");
  }

  @ParameterizedTest
  @MethodSource("option_source")
  void value(Option option) {
    var actual = option.value();
    assertTrue(
      actual > 0 && (actual & (actual -1)) == 0,
      () -> String.format("Option value must be a power of two; `%d` provided", actual));
  }
}
