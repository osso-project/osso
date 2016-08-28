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
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class TestEventIds {

  @Test
  public void testV1() {
    EventIdGenerator generator = EventIds.generatorV1();

    Assert.assertNotNull(generator);
  }

  @Test
  public void testV3() {
    EventIdGenerator generator = EventIds.generatorV3();

    Assert.assertNotNull(generator);
  }

  @Test
  public void testPopulateEvent() {
    Event event = Event.newBuilder()
      .setTs(0)
      .setEventTypeId(1)
      .setAttributes(new HashMap<String, String>())
      .build();

    EventIds.populateId(event);

    Assert.assertEquals("O4DEUDH36ZLHLF54VDEH5JUOJX67DPK5AVR4WJ7QS34QWO2NX7VA====", event.getId());
  }

}