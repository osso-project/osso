# Osso

A modern standard for representing event-oriented data.

## What is Osso?

Osso is a modern standard for representing event-oriented data in high-throughput
operational systems. It uses existing open standards for schema definition and
serialization, but adds semantic meaning and definition to make integration between
systems easy, while still being size- and processing-efficient.

An Osso event is largely use case agnostic, and can represent a log message, stack
trace, metric sample, user action taken, ad display or click, generic HTTP event,
and so on. Every event has a set of common fields as well as optional key/value
attributes that are typically event type-specific.

The common event fields are:

  * Version (int) - The Osso event format version.
  * Event type (long) - A numeric event type.
  * Event ID (string) - An ID that uniquely identifies an event.
  * Timestamp (long) - A millisecond-accurate epoch timestamp indicating the moment the event occurred.
  * Location (string) - The location that generated the event.
  * Host (string) - The host or device that generated the event.
  * Service (string) - The service that generate the event.
  * Body (byte array) - The body or "payload" of the event.

The event attributes are always a set of string/string key/value pairs.

### Event Types, Bodies, and Attributes

TODO.

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
