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
 * <p>Option sets are immutable: and/or/xor all return new instances.
 */
public abstract class OptionSet<T extends Enum<?> & Option> {

  @SuppressWarnings("unchecked")
  public static <T extends Enum<?> & Option> OptionSet<T> of( T ...options) {
    // come on java, how can you be THIS stupid
    return new OptionSet<T>((Class<T>) options[0].getClass(), 0L) {}.or(options);
  }

  /** The option set's integer value. */
  public final long mask;

  private List<T> options = null;

  private Class<T> type;

  /** Canonical constructor. */
  public OptionSet(long mask) {
    this.mask = mask;
  }
  public OptionSet(int mask) {
    this((long) mask);
  }

  /** Construct from option values. */
  public OptionSet(@SuppressWarnings("unchecked") T ...options) {
    this(Arrays.stream(options).map(T::value).reduce(0L, (a, b) -> a | b));
  }

  /** Override this if you need to provide a default mask. */
  public OptionSet() {
    this(0L);
  }

  private OptionSet(Class<T> type, long mask) {
    this(mask);
    this.type = type;
  }

  /** ANDs this set with the given option(s). */
  public OptionSet<T> and(@SuppressWarnings("unchecked") T ...options) {
    var newMask = this.mask;
    for (var option : options) {
      newMask = option.and(newMask);
    }
    return new OptionSet<T>(this.type, newMask) {};
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
    return new OptionSet<T>(this.type, ~ this.mask) {};
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
    return new OptionSet<T>(this.type, newMask) {};
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
    return new OptionSet<T>(this.type, newMask) {};
  }

  @SuppressWarnings("unchecked")
  private Class<T> optionsEnum() {
    if (this.type == null) {
      if (
        this.getClass().getGenericSuperclass() instanceof ParameterizedType pType
          && pType.getActualTypeArguments() instanceof Type[] tArgs
          && tArgs.length > 0
          && tArgs[0] instanceof Class<?> tClass) {
        this.type = (Class<T>) tClass;
      } else {
        throw OptionSet.Error.InvalidOptionImplementation.throwable();
      }
    }
    return this.type;
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
