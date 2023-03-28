package com.onarandombox.MultiverseCore.configuration.node;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;

public class ConfigHeaderNode implements CommentedNode {

    /**
     * Creates a new builder for a {@link ConfigHeaderNode}.
     *
     * @param path  The path of the node.
     * @return The new builder.
     */
    public static Builder<? extends Builder> builder(String path) {
        return new Builder<>(path);
    }

    private final String path;
    private final String[] comments;

    protected ConfigHeaderNode(String path, String[] comments) {
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
    public String[] getComments() {
        return comments;
    }

    public static class Builder<B extends Builder<B>> {

        protected final String path;
        protected final ArrayList<String> comments;

        public Builder(String path) {
            this.path = path;
            this.comments = new ArrayList<>();
        }

        /**
         * Adds a comment line to the node.
         *
         * @param comment The comment to add.
         * @return This builder.
         */
        public B comment(String comment) {
            if (!Strings.isNullOrEmpty(comment) && !comment.startsWith("#")) {
                comment = "# " + comment;
            }
            comments.add(comment);
            return (B) this;
        }

        public B comment(String... comments) {
            Collections.addAll(this.comments, comments);
            return (B) this;
        }

        public B comment(Iterable<String> comments) {
            comments.forEach(this.comments::add);
            return (B) this;
        }

        public ConfigHeaderNode build() {
            return new ConfigHeaderNode(path, comments.toArray(new String[0]));
        }
    }
}
