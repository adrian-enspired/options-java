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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Tests for OptionSet. */
class OptionSetTest {

  enum E implements Option { A, B, C; }

  @Test
  void of() {
    var actual = OptionSet.of(E.A, E.C);
    assertEquals(5L, actual.mask, "expected .of(A,C) to be 5; saw " + actual.mask);

    var options = List.of(E.A, E.B, E.C);
    assertEquals(
      options,
      actual.options(),
      () -> String.format(
        "expected .options() to be [A,B,C]; saw [%s]",
        options.stream().map(E::name).collect(Collectors.joining(","))));
  }

  @Test
  void optionset() {
    var actual = new OptionSet<E>() {};

    assertEquals(0L, actual.mask, "empty mask should be 0");

    var options = List.of(E.A, E.B, E.C);
    assertEquals(
      options,
      actual.options(),
      () -> String.format(
        "expected .options() to be [A,B,C]; saw [%s]",
        options.stream().map(E::name).collect(Collectors.joining(","))));
  }

  @Test
  void and() {
    assertEquals(
      0L,
      new OptionSet<E>() {}.and(E.A).mask,
      "expected .and(A) to be 0; saw " + new OptionSet<E>() {}.and(E.A).mask);

    assertEquals(
      1L,
      new OptionSet<E>(-1) {}.and(E.A).mask,
      "expected -1.and(A) to be 1; saw " + new OptionSet<E>(-1) {}.and(E.A).mask);

    assertEquals(
      1L,
      OptionSet.of(E.A).and(E.A).mask,
      "expected 1.and(A) to be 1; saw " + OptionSet.of(E.A).and(E.A).mask);
  }

  @Test
  void or() {
    assertEquals(
      1L,
      new OptionSet<E>() {}.or(E.A).mask,
      "expected .or(A) to be 1; saw " + new OptionSet<E>() {}.or(E.A).mask);

    assertEquals(
      -1L,
      new OptionSet<E>(-1) {}.or(E.A).mask,
      "expected -1.or(A) to be -1; saw " + new OptionSet<E>() {}.or(E.A).mask);

    assertEquals(
      5L,
      new OptionSet<E>() {}.or(E.A).or(E.C).mask,
      "expected .or(A).or(C) to be 5; saw " + new OptionSet<E>() {}.or(E.A).or(E.C).mask);

    assertEquals(
      5L,
      new OptionSet<E>() {}.or(E.A, E.C).mask,
      "expected .or(A,C) to be 5; saw " + new OptionSet<E>() {}.or(E.A).or(E.C).mask);
  }
}
