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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import red.enspi.exceptable.Exceptable.Signal;

/**
 * Represents a collection of options.
 *
 * Option sets are immutable: and/or/xor all return new instances.
 */
public abstract class OptionSet<T extends Enum<?> & Option> {

  public static <T extends Enum<?> & Option> OptionSet<T> from(@SuppressWarnings("unchecked") T ...options) {
    return new OptionSet<T>() {}.or(options);
  }

  /** The option set's integer value. */
  public final long mask;

  private List<T> options = null;

  public OptionSet() {
    this(0L);
  }

  public OptionSet(int mask) {
    this((long) mask);
  }

  public OptionSet(long mask) {
    this.mask = mask;
  }

  /** ANDs this set with the given option(s). */
  public OptionSet<T> and(@SuppressWarnings("unchecked") T ...options) {
    var newMask = this.mask;
    for (var option : options) {
      newMask = option.and(newMask);
    }
    return this.from(newMask);
  }

  /** Does this option set include (any of) the given option(s)? */
  public boolean has(@SuppressWarnings("unchecked") T ...options) {
    for (var option : options) {
      if (option.in(this.mask)) {
        return true;
      }
    }
    return false;
  }

  /** Does this option set include all of the given option(s)? */
  public boolean hasAll(@SuppressWarnings("unchecked") T ...options) {
    for (var option : options) {
      if (! option.in(this.mask)) {
        return false;
      }
    }
    return true;
  }

  /** NOTs this set. */
  public OptionSet<T> not() {
    return this.from(~ this.mask);
  }

  /** Lists all options that can be included in this set. */
  public List<T> options() {
    if (this.options == null) {
      this.options = Collections.unmodifiableList(
        Arrays.asList(this.optionsEnum().getEnumConstants()));
    }
    return this.options;
  }

  /** ORs this set with the given option(s). */
  public OptionSet<T> or(@SuppressWarnings("unchecked") T ...options) {
    var newMask = mask;
    for (var option : options) {
      newMask = option.or(newMask);
    }
    return this.from(newMask);
  }

  /** Lists the options included in this set. */
  public List<T> toOptions() {
    var list = new ArrayList<T>();
    for (var option : this.options()) {
      if (option.in(this.mask)) {
        list.add(option);
      }
    }
    return list;
  }

  /** XORs this set with the given option(s). */
  public OptionSet<T> xor(@SuppressWarnings("unchecked") T ...options) {
    var newMask = this.mask;
    for (var option : options) {
      newMask = option.xor(newMask);
    }
    return this.from(newMask);
  }

  @SuppressWarnings("unchecked")
  private OptionSet<T> from(long mask) {
    try {
      return this.getClass().getDeclaredConstructor(int.class).newInstance(mask);
    } catch (
      InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException e) {
      // problem instantiating the actual subtype;
      //  fall back on returning an anonymous instance of the same generic type
      return new OptionSet<T>(mask) {};
    }
  }

  @SuppressWarnings("unchecked")
  private Class<T> optionsEnum() {
    if (
      this.getClass().getGenericSuperclass() instanceof ParameterizedType pType
        && pType.getActualTypeArguments() instanceof Type[] tArgs
        && tArgs.length > 0
        && tArgs[0] instanceof Class<?> tClass) {
      return (Class<T>) tClass;
    }
    throw OptionSet.Error.InvalidOptionImplementation.throwable();
  }

  /** You're doing it wrong. */
  public enum Error implements Signal<RuntimeException> {

    /** Not currently used (thrown), but a ClassCastException probably means this happened. */
    IllegalVarArgsUsage {
      @Override
      public String description() {
        return "Each of `options` passed must be an instance of the parameterized Option.";
      }
    },

    /** Realistically this should never actually be possible; it's just used to satisfy the compiler. */
    InvalidOptionImplementation {
      @Override
      public String description() {
        return "OptionSet must be parameterized with an Enum that implements Option.";
      }
    };
  }
}
