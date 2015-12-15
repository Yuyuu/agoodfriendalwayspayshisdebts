package com.vter.search;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.jongo.Find;
import org.jongo.FindOne;
import org.jongo.Jongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JongoQueryBuilder {

  private JongoQueryBuilder(String collection) {
    this.collection = collection;
  }

  public static JongoQueryBuilder create(String collection) {
    return new JongoQueryBuilder(collection);
  }

  public JongoQueryBuilder add(String field, String query, Object... values) {
    parts.add(new QueryPart(field, query, values));
    return this;
  }

  public Find find(Jongo jongo) {
    return jongo.getCollection(collection).find(query(), parameters());
  }

  public FindOne findOne(Jongo jongo) {
    return jongo.getCollection(collection).findOne(query(), parameters());
  }

  private String query() {
    if (parts.isEmpty()) {
      return "{}";
    }
    final StringBuilder builder = new StringBuilder();
    builder.append("{$and:[");
    Joiner.on(",").appendTo(builder, parts.stream().map(QueryPart::buildQuery).collect(Collectors.toList()));
    builder.append("]}");
    return builder.toString();
  }

  private Object[] parameters() {
    final ArrayList<Object> objects = Lists.newArrayList();
    parts.forEach(part -> part.applyParameters(objects));
    return objects.toArray();
  }

  private final String collection;
  private List<QueryPart> parts = Lists.newArrayList();

  private static class QueryPart {

    public QueryPart(String field, String query, Object[] values) {
      this.field = field;
      this.query = query;
      this.values = values;
    }

    public void applyParameters(List<Object> objects) {
      Collections.addAll(objects, values);
    }

    public String buildQuery() {
      return "{" + field + ":" + query + "}";
    }

    private final String field;
    private final String query;
    private final Object[] values;
  }
}
