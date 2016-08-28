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
import org.apache.avro.Schema;

public final class EventIds {

  private static final EventIdGenerator generatorV1 = new EventIdGeneratorV1();
  private static final EventIdGenerator generatorV3 = new EventIdGeneratorV3();

  // Prevent instantiation.
  private EventIds() {
  }

  public static Event populateId(Event event) {
    event.setId(generatorForEvent(event).generateId(event));

    return event;
  }

  public static EventIdGenerator generatorForEvent(Event event) {
    Schema schema = event.getSchema();

    /*
     * Version field was added in v3. Its presence is enough to tell us we
     * should use the v3 generator, at least for now.
     */
    if (schema.getField("version") != null) {
      return generatorV3();
    } else {
      return generatorV1();
    }
  }

  public static EventIdGenerator generatorV1() {
    return generatorV1;
  }

  public static EventIdGenerator generatorV3() {
    return generatorV3;
  }

}
