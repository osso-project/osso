# Osso

A modern standard for representing event-oriented data.

## What is Osso?

Osso is a modern standard for representing event-oriented data in high-throughput
operational systems. It uses existing open standards for schema definition and
serialization, but adds semantic meaning and definition to make integration between
systems easy, while still being size- and processing-efficient.

An Osso event is largely use case agnostic, and can represent a log message, stack
trace, metric sample, user action taken, ad display or click, generic HTTP event,
or otherwise. Every event has a set of common fields as well as optional key/value
attributes that are typically event type-specific.

The common event fields are:

  * Version (long) - The Osso event format version.
  * Event type ID (int) - A numeric event type.
  * Event ID (string) - An ID that uniquely identifies an event.
  * Timestamp (long) - A millisecond-accurate epoch timestamp indicating the moment the event occurred.
  * Location (string) - The location that generated the event.
  * Host (string) - The host or device that generated the event.
  * Service (string) - The service that generate the event.
  * Body (byte array) - The body or "payload" of the event.

The event attributes are always a set of string/string key/value pairs.

The event model is formally defined by an [Apache Avro][apache-avro] schema that can
be found at `src/main/avro/com/osso/event/Event.avsc` ([view on Github][github-schema]),
and events are serialized as Avro objects when transferred over the wire between systems.
Systems are free to use this same format when storing events on disk. Some serialization
libraries such as [Apache Parquet][apache-parquet] - a highly optimized columnar
storage format - can also serialize to and from Avro objects, making it an appealing
in-memory representation.

[apache-avro]: http://avro.apache.org/
[github-schema]: https://github.com/osso-project/osso/blob/master/src/main/avro/com/osso/event/Event.avsc
[apache-parquet]: http://parquet.apache.org/

### Event Types, Bodies, and Attributes

An Osso event uses a semi-structured schema which allows systems interested in specific
kinds of events to get what they need, while retaining the ability to handle all events
generically. This means that adding a new event type does not require coordination of
systems that aren't interested in that type of event. This is counter to a model where
each type of event has its own specific schema of which all systems must be made aware.

While flexible, this model requires that there be a way to indicate to downstream systems
(i.e. consumers of these events) how to process the event type-specific data. The
`event_type_id` field acts as this identifier. Downstream systems use the even type to
decide how they should interpret the event payload in the `body` field, and what key/value
pairs they can expect in the `attributes` field. In this way, the event type is analogous
to a class in a programming language like Java.

For example, let's assume event type 100 represents a generic syslog event
([RFC-5424][rfc-5424]). Here's what an event would look like:

```
{
  version:        3,
  event_type_id:  100,
  id:             "**some hash of the event fields**",
  ts:             1472076150211,
  location:       "aws/us-west-2a",
  host:           "some.host.name",
  service:        "sshd",
  body:           [ **array of bytes representing the raw syslog event** ],
  attributes: {
    syslog_pid:       668,
    syslog_facility:  3,
    syslog_severity:  6,
    syslog_process:   "dhclient",
    syslog_message:   "DHCPACK from 10.10.0.1 (xid=0x45b63bdc)",
    ...
  }
}
```

In this example, the `body` retains the original syslog event as it was received by
the syslog server. Since syslog messages are ASCII encoded text, these bytes are
human readable, but there's no requirement that that be the case. In order to know
_how_ to parse the body, a consumer must use the `event_type_id`. Again, in the case
of syslog events, if we see an event type ID 100, we know the format of the `body`
field, a priori. The `attributes` work the same way; using the `event_type_id`,
we know exactly which attributes will appear on the event.

Consumers of events can "upgrade" or "promote" generic event types to more specific
event types by extracting information from the `body` (or any other fields) and
generating an event of a new (typically more specific) type in its place. Let's
assume for a second we have firewall devices that can only generate events via
syslog. We may have an agent process that listens on a syslog port, receives any
event, and produces an Osso event of type 100 - a generic syslog event similar to
the example above. While this is useful, much of the really interesting data is
going to be contained in the `syslog_message` attribute as plain text. For messages
that are firewall connection rejection events, it's far more interesting to extract
data such as the source IP, source port, destination IP, and destination port into
attributes so they can be used in downstream analytics. In this instance, we may
perform a transformation similar to the following Java snippet:

```java
void onEvent(Event event) {
  switch (event.getEventTypeId()) {

    case 100:

      // Syslog event processing rules
      switch (event.getAttributes().get("syslog_process")) {
        case "cisco-asa":
          // Extract the security-specific info from the syslog_message attribute.
          Map<String, String> attrs = parseSecurityEvent(event.getAttributes().get("syslog_message"));

          // Create a new event, but override the event type and attributes.
          Event securityEvent = new Event.Builder(event)
            .setEventTypeId(54321)
            .setAttributes(attrs)
            .build();

          // Emit a new "promoted" event in place of the original.
          emit(securityEvent);
          break;

        // Other syslog processing rules
        
        default:
          // If we don't know how to process an event, just pass it along.
          emit(event);
          break;
      }

      break;

    // Other event types
  }
}
```

Of course, in a production system, processing events with hard-coded rules like
this is probably not ideal, but it logically illustrates how code can promote
generic events. It's equally obvious to see how one might implement other kinds
of event processing such as filtering, masking, aggregation, and so on. These
techniques, and Osso events - are compatible with all stream processing systems
and libraries including [Apache Spark][apache-spark], [Apache Flink][apache-flink],
[Kafka Streams][kafka-streams], and [Akka][akka].

[rfc-5424]: https://tools.ietf.org/html/rfc5424
[apache-spark]: http://spark.apache.org/
[apache-flink]: http://flink.apache.org/
[kafka-streams]: http://kafka.apache.org/documentation.html#streams
[akka]: http://akka.io/

### Event IDs

TODO.

### Timestamps and Time

TODO.

### Standard Event Types

TODO.

### Example Uses

TODO.

### Related Work

#### Syslog

TODO.

#### Arcsight CEF

TODO.

### FAQ

_"Why create a new standard for this kind of data?"_

_"Who created this?"_

_"Why not use event type-specific schemas?"_

_"Why not use JSON as the serialization format?"_

_"Why not use **insert other serialization format** as the serialization format?"_

_"How do I pronounce 'Osso'?"_

`OH-so` - `OH` like "open", and `so` like "so," in English.

## Contributing

### Issue Tracking

Please file an issue via [Github issues][github-issues] if you think you've
found a bug, wish to request a feature, or have a problem using Osso.

[github-issues]: https://github.com/osso-project/osso/issues

### Submitting Code

Please use [Github pull requests][github-prs] to submit code.

[github-prs]: https://github.com/osso-project/osso/pulls

## License

Osso is distributed under the Apache License 2.0. See the LICENSE file included
with this source code for more information.
