package com.justinsb.issuesync.store;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleYaml {
  static final Gson gson = new Gson();

  public static String build(Message m) {
    LinkedHashMap<String, String> props = Maps.newLinkedHashMap();
    for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : m.getAllFields().entrySet()) {
      Descriptors.FieldDescriptor field = entry.getKey();
      String fieldName = field.getName();

      Object value = entry.getValue();
      String valueString = gson.toJson(value);

      props.put(fieldName, valueString);
    }

    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entry : props.entrySet()) {
      String k = entry.getKey();
      String v = entry.getValue();

      sb.append(k);
      sb.append(": ");
      sb.append(v);
      sb.append("\n");
    }

    return sb.toString();
  }
}
