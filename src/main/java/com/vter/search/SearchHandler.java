package com.vter.search;

import com.vter.infrastructure.bus.MessageHandler;

public interface SearchHandler<TSearch extends Search<TResponse>, TResponse> extends MessageHandler<TSearch, TResponse> {
}
