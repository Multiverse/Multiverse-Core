package org.mvplugins.multiverse.core.commandtools.queue;

import co.aikar.commands.ACFUtil;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.locale.message.Message;

/**
 * Represents a single command used in {@link CommandQueueManager} for confirming before running potentially
 * dangerous action.
 */
public class CommandQueuePayload {

    /**
     * Creates a new {@link CommandQueuePayload}
     *
     * @param issuer    The issuer of the command
     * @return The new {@link CommandQueuePayload}
     */
    public static CommandQueuePayload issuer(@NotNull MVCommandIssuer issuer) {
        return new CommandQueuePayload(issuer);
    }

    private static final String DEFAULT_PROMPT_MESSAGE = "The command you are trying to run is deemed dangerous."; // todo: localize

    private final int otp;
    private final MVCommandIssuer issuer;
    private Runnable action = () -> {};
    private Message prompt = Message.of(DEFAULT_PROMPT_MESSAGE);
    private BukkitTask expireTask;

    protected CommandQueuePayload(@NotNull MVCommandIssuer issuer) {
        this.otp = ACFUtil.rand(100, 999);
        this.issuer = issuer;
    }

    /**
     * Gets the issuer of the command.
     * @return The issuer
     */
    @NotNull
    public MVCommandIssuer issuer() {
        return issuer;
    }

    /**
     * Sets the logic to execute upon confirming.
     * @param action    The logic to execute
     * @return The same {@link CommandQueuePayload} for method chaining
     */
    public CommandQueuePayload action(@NotNull Runnable action) {
        this.action = action;
        return this;
    }

    /**
     * Gets the logic to execute upon confirming.
     *
     * @return  The logic to execute
     */
    @NotNull
    public Runnable action() {
        return action;
    }

    /**
     * Gets the OTP number for validation.
     *
     * @return The OTP number
     */
    public int otp() {
        return otp;
    }

    /**
     * Sets the question to ask sender to confirm.
     *
     * @param prompt    The prompt message.
     * @return The same {@link CommandQueuePayload} for method chaining.
     */
    public CommandQueuePayload prompt(Message prompt) {
        this.prompt = prompt;
        return this;
    }

    /**
     * Gets the question to ask sender to confirm.
     *
     * @return The prompt message.
     */
    public Message prompt() {
        return prompt;
    }

    void expireTask(BukkitTask expireTask) {
        this.expireTask = expireTask;
    }

    @Nullable
    BukkitTask expireTask() {
        return expireTask;
    }
}
