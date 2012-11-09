package com.mvplugin.features;

/**
 * This is the prefixChat-feature.
 * <p>
 * It has a boolean state. If that state is {@code true}, all chat messages will be prefixed with the
 * name of the world the sender is in, if the state is {@code true}, nothing will happen.
 */
public interface PrefixChatFeature {
    boolean isPrefixChatEnabled();
    void setPrefixChatState(boolean enabled);
}
