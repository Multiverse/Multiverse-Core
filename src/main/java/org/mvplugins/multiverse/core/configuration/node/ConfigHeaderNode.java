package org.mvplugins.multiverse.core.configuration.node;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * A node that represents a header without any value.
 */
public class ConfigHeaderNode implements CommentedNode {

    /**
     * Creates a new builder for a {@link ConfigHeaderNode}.
     *
     * @param path  The path of the node.
     * @return The new builder.
     */
    public static @NotNull Builder<? extends Builder<?>> builder(String path) {
        return new Builder<>(path);
    }

    private final @NotNull String path;
    private final @NotNull String[] comments;

    protected ConfigHeaderNode(@NotNull String path, @NotNull String[] comments) {
        this.path = path;
        this.comments = comments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String[] getComments() {
        return comments;
    }

    public static class Builder<B extends Builder<B>> {

        protected final @NotNull String path;
        protected final @NotNull List<String> comments;

        public Builder(@NotNull String path) {
            this.path = path;
            this.comments = new ArrayList<>();
        }

        /**
         * Adds a comment line to the node.
         *
         * @param comment The comment to add.
         * @return This builder.
         */
        public @NotNull B comment(@NotNull String comment) {
            if (!Strings.isNullOrEmpty(comment) && !comment.startsWith("#")) {
                comment = "# " + comment;
            }
            comments.add(comment);
            return self();
        }

        /**
         * Builds the node.
         *
         * @return The built node.
         */
        public @NotNull ConfigHeaderNode build() {
            return new ConfigHeaderNode(path, comments.toArray(new String[0]));
        }


        protected @NotNull B self() {
            //noinspection unchecked
            return (B) this;
        }
    }
}
