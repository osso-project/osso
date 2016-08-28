/*
 * Copyright (c) 2016 Rocana.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.osso.event.id;

import com.osso.event.Event;
import com.osso.event.id.EventIdGenerator;
import com.osso.event.id.EventIdGeneratorV1;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TestEventIdGeneratorV1 {

  @Test
  public void testGenerateId() {
    EventIdGenerator generator = new EventIdGeneratorV1();

    Map<String, String> attributes = new HashMap<>();
    attributes.put("a", "1");
    attributes.put("b", "2");
    attributes.put("c", "3");

    Event event = Event.newBuilder()
      .setTs(0)
      .setEventTypeId(1)
      .setSource("source")
      .setLocation("location")
      .setHost("host")
      .setService("service")
      .setBody(ByteBuffer.wrap("String body".getBytes()))
      .setAttributes(attributes)
      .build();

    Assert.assertEquals(
      "DJMAWB6VOIBYGKQZ4E2XXBVMIZW2CXTM3NPWFJW32DYJGMA47IPQ====",
      generator.generateId(event)
    );
  }

}