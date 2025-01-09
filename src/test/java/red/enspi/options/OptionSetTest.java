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

    var toOptions = List.of(E.A, E.C);
    assertEquals(
      toOptions,
      actual.toOptions(),
      () -> String.format(
        "expected .toOptions() to be [A,C]; saw [%s]",
        toOptions.stream().map(E::name).collect(Collectors.joining(","))));
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
  void has() {
    var actual = OptionSet.of(E.A, E.C);
    assertTrue(actual.has(E.A), "expected actual.has(A) to be true");
    assertFalse(actual.has(E.B), "expected actual.has(B) to be false");
    assertTrue(actual.has(E.C), "expected actual.has(C) to be true");
    assertTrue(actual.has(E.A, E.C), "expected actual.has(A,C) to be true");
    assertFalse(actual.has(E.A, E.B, E.C), "expected actual.has(A,B,C) to be false");

    var and = actual.and(E.C);
    assertFalse(and.has(E.A), "expected and.has(A) to be false");
    assertFalse(and.has(E.B), "expected and.has(B) to be false");
    assertTrue(and.has(E.C), "expected and.has(C) to be true");
    assertFalse(and.has(E.A, E.C), "expected and.has(A,C) to be false");
    assertFalse(and.has(E.A, E.B, E.C), "expected and.has(A,B,C) to be false");

    var not = actual.not();
    assertFalse(not.has(E.A), "expected not.has(A) to be false");
    assertTrue(not.has(E.B), "expected not.has(B) to be true");
    assertFalse(not.has(E.C), "expected not.has(C) to be false");
    assertFalse(not.has(E.A, E.C), "expected and.has(A,C) to be false");
    assertFalse(not.has(E.A, E.B, E.C), "expected and.has(A,B,C) to be false");

    var or = actual.or(E.B);
    assertTrue(or.has(E.A), "expected or.has(A) to be true");
    assertTrue(or.has(E.B), "expected or.has(B) to be true");
    assertTrue(or.has(E.C), "expected or.has(C) to be true");
    assertTrue(or.has(E.A, E.C), "expected or.has(A,C) to be true");
    assertTrue(or.has(E.A, E.B), "expected or.has(A,B) to be true");
    assertTrue(or.has(E.A, E.B, E.C), "expected or.has(A,B,C) to be true");

    var xor = actual.xor(E.A);
    assertFalse(xor.has(E.A), "expected xor.has(A) to be false");
    assertFalse(xor.has(E.B), "expected xor.has(B) to be false");
    assertTrue(xor.has(E.C), "expected xor.has(C) to be true");
    assertFalse(xor.has(E.A, E.C), "expected xor.has(A,C) to be false");
    assertFalse(xor.has(E.A, E.B), "expected xor.has(A,B) to be false");
    assertFalse(xor.has(E.A, E.B, E.C), "expected xor.has(A,B,C) to be false");
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
      "expected .or(A,C) to be 5; saw " + new OptionSet<E>() {}.or(E.A, E.C).mask);
  }

  @Test
  void xor() {
    assertEquals(
      1L,
      new OptionSet<E>() {}.xor(E.A).mask,
      "expected .xor(A) to be 1; saw " + new OptionSet<E>() {}.xor(E.A).mask);

    assertEquals(
      -2L,
      new OptionSet<E>(-1) {}.xor(E.A).mask,
      "expected -1.xor(A) to be -2; saw " + new OptionSet<E>() {}.xor(E.A).mask);

    assertEquals(
      5L,
      new OptionSet<E>() {}.xor(E.A).xor(E.C).mask,
      "expected .xor(A).xor(C) to be 5; saw " + new OptionSet<E>() {}.xor(E.A).xor(E.C).mask);

    assertEquals(
      5L,
      new OptionSet<E>() {}.xor(E.A, E.C).mask,
      "expected .xor(A,C) to be 5; saw " + new OptionSet<E>() {}.xor(E.A, E.C).mask);
  }

  @Test
  void hasAny() {
    var actual = OptionSet.of(E.A, E.C);
    assertTrue(actual.hasAny(E.A), "expected actual.hasAny(A) to be true");
    assertFalse(actual.hasAny(E.B), "expected actual.hasAny(B) to be false");
    assertTrue(actual.hasAny(E.C), "expected actual.hasAny(C) to be true");
    assertTrue(actual.hasAny(E.A, E.C), "expected actual.hasAny(A,C) to be true");
    assertTrue(actual.hasAny(E.A, E.B, E.C), "expected actual.hasAny(A,B,C) to be true");

    var and = actual.and(E.C);
    assertFalse(and.hasAny(E.A), "expected and.hasAny(A) to be false");
    assertFalse(and.hasAny(E.B), "expected and.hasAny(B) to be false");
    assertTrue(and.hasAny(E.C), "expected and.hasAny(C) to be true");
    assertTrue(and.hasAny(E.A, E.C), "expected and.hasAny(A,C) to be true");
    assertTrue(and.hasAny(E.A, E.B, E.C), "expected and.hasAny(A,B,C) to be true");

    var not = actual.not();
    assertFalse(not.hasAny(E.A), "expected not.hasAny(A) to be false");
    assertTrue(not.hasAny(E.B), "expected not.hasAny(B) to be true");
    assertFalse(not.hasAny(E.C), "expected not.hasAny(C) to be false");
    assertFalse(not.hasAny(E.A, E.C), "expected and.hasAny(A,C) to be false");
    assertTrue(not.hasAny(E.A, E.B, E.C), "expected and.hasAny(A,B,C) to be true");

    var or = actual.or(E.B);
    assertTrue(or.hasAny(E.A), "expected or.hasAny(A) to be true");
    assertTrue(or.hasAny(E.B), "expected or.hasAny(B) to be true");
    assertTrue(or.hasAny(E.C), "expected or.hasAny(C) to be true");
    assertTrue(or.hasAny(E.A, E.C), "expected or.hasAny(A,C) to be true");
    assertTrue(or.hasAny(E.A, E.B), "expected or.hasAny(A,B) to be true");
    assertTrue(or.hasAny(E.A, E.B, E.C), "expected or.hasAny(A,B,C) to be true");

    var xor = actual.xor(E.A);
    assertFalse(xor.hasAny(E.A), "expected xor.hasAny(A) to be false");
    assertFalse(xor.hasAny(E.B), "expected xor.hasAny(B) to be false");
    assertTrue(xor.hasAny(E.C), "expected xor.hasAny(C) to be true");
    assertTrue(xor.hasAny(E.A, E.C), "expected xor.hasAny(A,C) to be true");
    assertFalse(xor.hasAny(E.A, E.B), "expected xor.hasAny(A,B) to be false");
    assertTrue(xor.hasAny(E.A, E.B, E.C), "expected xor.hasAny(A,B,C) to be true");
  }
}
