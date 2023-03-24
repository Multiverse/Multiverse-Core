package com.onarandombox.MultiverseCore.utils.settings.node;

import java.util.ArrayList;
import java.util.List;

import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link CommentedNode} that allows for comments to be added to the node.
 */
public class MVCommentedNode implements CommentedNode {

    /**
     * Creates a new builder for a {@link MVCommentedNode}.
     *
     * @param path  The path of the node.
     * @return The new builder.
     */
    public static Builder<Builder> builder(String path) {
        return new Builder<>(path);
    }

    protected final String path;
    protected final String[] comments;

    protected MVCommentedNode(String path, String[] comments) {
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

    /**
     * Builder for {@link MVCommentedNode}.
     *
     * @param <B> The type of the builder.
     */
    public static class Builder<B extends Builder> {
        protected final String path;
        protected final List<String> comments;

        /**
         * Creates a new builder for a {@link MVCommentedNode}.
         *
         * @param path  The path of the node.
         */
        protected Builder(String path) {
            this.path = path;
            this.comments = new ArrayList<>();
        }

        /**
         * Adds a comment line to the node.
         *
         * @param comment The comment to add.
         * @return This builder.
         */
        public B comment(@NotNull String comment) {
            if (!comment.isEmpty() && !comment.trim().startsWith("#")) {
                // Automatically add a comment prefix if the comment doesn't start with one.
                comment = "# " + comment;
            }
            this.comments.add(comment);
            return (B) this;
        }

        /**
         * Builds the node.
         *
         * @return The built node.
         */
        public MVCommentedNode build() {
            return new MVCommentedNode(path, comments.toArray(new String[0]));
        }
    }
}
