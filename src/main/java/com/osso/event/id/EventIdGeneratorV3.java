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

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.osso.event.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *   The event ID generation implementation for version 3 of the Osso
 *   specification.
 * </p>
 * <p>
 *   This implementation is a base 32 encoding of a SHA-256 hash of the
 *   following fields, in the following order:
 * <ul>
 *   <li><tt>version</tt></li>
 *   <li><tt>ts</tt></li>
 *   <li><tt>event_type_id</tt></li>
 *   <li><tt>location</tt></li>
 *   <li><tt>host</tt></li>
 *   <li><tt>service</tt></li>
 *   <li><tt>source</tt>, if defined.</li>
 *   <li><tt>body</tt>, if defined.</li>
 *   <li>
 *     Each attribute key, then value, in sorted order by the attribute key.
 *   </li>
 * </ul>
 * </p>
 * <p>
 *   The exact implementations of the base 32 encoding and SHA-256 hash function
 *   are Google Guava's {@link BaseEncoding#base32()} and
 *   {@link Hashing#sha256()}, respectively. The base 32 encoding is
 *   <a href="http://tools.ietf.org/html/rfc4648#section-6">RFC-4648 section 6</a>
 *   compliant, uses uppercase characters, and the '<tt>=</tt>' character for
 *   padding. Guava's SHA-256 implementation is a wrapper around Java's
 *   {@link java.security.MessageDigest}. Integers and longs are encoded in
 *   little endian format, while strings use Java's
 *   {@link String#getBytes(java.nio.charset.Charset)} with a UTF-8 character
 *   set, which uses the appropriate {@link java.nio.charset.CharsetEncoder}.
 * </p>
 */
public final class EventIdGeneratorV3 implements EventIdGenerator {

  private static final BaseEncoding baseEncoding = BaseEncoding.base32();
  private static final HashFunction hashFunction = Hashing.sha256();

  @Override
  public String generateId(Event event) {
    Hasher hasher = hashFunction.newHasher();

    hasher.putLong(event.getVersion())
      .putLong(event.getTs())
      .putLong(event.getEventTypeId())
      .putString(event.getLocation(), Charsets.UTF_8)
      .putString(event.getHost(), Charsets.UTF_8)
      .putString(event.getService(), Charsets.UTF_8);

    if (event.getSource() != null) {
      hasher.putString(event.getSource(), Charsets.UTF_8);
    }

    if (event.getBody() != null) {
      hasher.putBytes(event.getBody().array());
    }

    Map<String, String> attributes = event.getAttributes();

    List<String> attrKeys = new ArrayList<>(attributes.keySet());
    Collections.sort(attrKeys);

    for (String attrKey : attrKeys) {
      hasher.putString(attrKey, Charsets.UTF_8)
        .putString(attributes.get(attrKey), Charsets.UTF_8);
    }

    return baseEncoding.encode(hasher.hash().asBytes());
  }

}
