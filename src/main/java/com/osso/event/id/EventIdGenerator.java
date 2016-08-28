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

/**
 * <p>
 * A generator of {@link Event} IDs.
 * </p>
 * <p>
 * Implementations of this interface generate event IDs according to a specific
 * version of the Osso specification. While the exact definition of how event
 * IDs are generated is defined by the specification, all implementations make
 * the following guarantees.
 * <ul>
 *   <li>An ID is a string.</li>
 *   <li>
 *     ID generation is a pure function. This means that all implementations
 *     are thread-safe and suitable for de-duplication (within a single
 *     implementation).
 *   </li>
 * </ul>
 * Most implementations are hashing strategies that include a set of the event
 * fields to generate a unique event ID, save for the rare hash collisions.
 * </p>
 *
 * @see EventIds
 */
public interface EventIdGenerator {

  String generateId(Event event);

}
