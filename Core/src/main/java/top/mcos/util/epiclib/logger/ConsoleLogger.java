/*
 * EpicPluginLib - Library with basic utilities for bukkit plugins.
 * Copyright (C) 2022  Christiano Rangel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.mcos.util.epiclib.logger;

import org.jetbrains.annotations.NotNull;

/**
 * A logger to log colored messages or leveled messages to console.
 *
 * @param <R> The receiver that will receive messages logged by the method {@link #log(Object receiver, String message)}.
 */
public interface ConsoleLogger<R>
{
    /**
     * Creates a simple, platform-independent logger. This logger should be used as a placeholder, you should use your
     * platform's {@link ConsoleLogger} implementing class as soon as it's available.
     *
     * @param prefix The prefix of every message.
     */
    static @NotNull ConsoleLogger<?> simpleLogger(@NotNull String prefix)
    {
        return new ConsoleLogger<Object>()
        {
            @Override
            public @NotNull String getPrefix()
            {
                return prefix;
            }

            @Override
            public void log(@NotNull String message)
            {
                log(message, Level.INFO);
            }

            @Override
            public void log(@NotNull String message, @NotNull Level level)
            {
                message = prefix + message;
                if (level == Level.ERROR) {
                    System.err.println(message);
                } else {
                    System.out.println(message);
                }
            }

            @Override
            public void log(@NotNull Object receiver, @NotNull String message)
            {
                log(message, Level.INFO);
            }
        };
    }

    /**
     * @return The prefix applied to the start of every message.
     */
    @NotNull String getPrefix();

    /**
     * Logs formatted messages with the prefix to console.
     *
     * @param message The message with color codes to send to console.
     */
    void log(@NotNull String message);

    /**
     * Removes color codes the from message and logs to console with a specific {@link Level} and the prefix.
     *
     * @param message The message to log to console.
     * @param level   The level the message should be logged to console.
     */
    void log(@NotNull String message, @NotNull Level level);

    /**
     * Sends formatted messages with the prefix to the message receiver.
     *
     * @param receiver Who the message will be sent.
     * @param message  The message to be sent.
     */
    void log(@NotNull R receiver, @NotNull String message);

    enum Level
    {
        ERROR,
        WARN,
        INFO
    }
}
