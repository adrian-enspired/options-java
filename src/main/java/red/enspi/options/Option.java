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

/**
 * Represents individual options.
 *
 * <p>Options are boolean. They are not _settings_: they have no value, they are simply present or not.
 * You should avoid conflicting/exclusive options.
 *
 * <p>N.B.;
 * <ul>
 * <li>Each subtype of this interface can support up to 64 distinct options.
 * <li>This interface is meant to be implemented as an enum.
 *     Options that are not Enums **will not work correctly** with OptionSets.
 * </ul>
 */
public interface Option {

  /** ANDs this option's value with the given mask. */
  default long and(long mask) {
    return this.value() & mask;
  }

  default long and(int mask) {
    return this.and((long) mask);
  }

  /** Checks whether this option's value is included the given mask. */
  default boolean in(long mask) {
    var value = this.value();
    return (value & mask) == value;
  }
  default boolean in(int mask) {
    return this.in((long) mask);
  }

  /** NOTs this option. */
  default long not() {
    return ~ this.value();
  }

  /** ORs this option's value with the given mask. */
  default long or(long mask) {
    return this.value() | mask;
  }
  default long or(int mask) {
    return this.or((long) mask);
  }

  /** XORs this option's value with the given mask. */
  default long xor(long mask) {
    return this.value() ^ mask;
  }
  default long xor(int mask) {
    return this.xor((long) mask);
  }

  /**
   * This option's longeger value.
   *
   * <p>If you override this method, you must insure every value returned is a power of 2.
   */
  default long value() {
    return (long) Math.pow(2, this.ordinal());
  }

  /** See Enum.ordinal(). */
  abstract int ordinal();
}
