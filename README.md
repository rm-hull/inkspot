# Inkspot [![Build Status](https://secure.travis-ci.org/rm-hull/inkspot.png)](http://travis-ci.org/rm-hull/inkspot)

A small Clojure/ClojureScript library for creating colour swatches

### Pre-requisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.3.2 or above installed.

### Building

To build and install the library locally, run:

    $ lein test
    $ lein cljsbuild once
    $ lein install

### Including in your project

There _will be_ a version hosted at [Clojars](https://clojars.org/rm-hull/wireframes) soon.
For leiningen include a dependency:

```clojure
[rm-hull/inkspot "0.0.1-SNAPSHOT"]
```

For maven-based projects, add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>rm-hull</groupId>
  <artifactId>inkspot</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Basic Usage

## TODO

* ~~Web-safe colors~~
* ~~Spectral colors~~
* ~~IColor protocol~~
* ~~Color mapper function - given a numerical range and a color swatch, maps numerical input to the range of colors~~
* Color averaging/mixing

## Known Bugs

## References

* http://catless.ncl.ac.uk/Lindsay/swatch0.html

## License

The MIT License (MIT)

Copyright (c) 2014 Richard Hull

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/rm-hull/inkspot/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
