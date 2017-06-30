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

package org.osso.event;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestEvent {

  @Test
  public void testEvent() {
    Map<String, String> attributes = new HashMap<>();

    attributes.put("a", "1");
    attributes.put("b", "2");

    Event event = Event.newBuilder()
      .setId("id")
      .setEventTypeId(1)
      .setTs(0)
      .setLocation("location")
      .setHost("host")
      .setService("service")
      .setAttributes(attributes)
      .build();

    Assert.assertEquals("id", event.getId());
    Assert.assertEquals(1, event.getEventTypeId().intValue());
    Assert.assertEquals(0, event.getTs().longValue());
    Assert.assertEquals("location", event.getLocation());
    Assert.assertEquals("host", event.getHost());
    Assert.assertEquals("service", event.getService());
    Assert.assertEquals(attributes, event.getAttributes());
    Assert.assertNull(event.getBody());
  }

}
