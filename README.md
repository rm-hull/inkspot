# Inkspot [![Build Status](https://secure.travis-ci.org/rm-hull/inkspot.png)](http://travis-ci.org/rm-hull/inkspot)

A small Clojure/ClojureScript library for creating colour swatches

### Pre-requisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.3.4 or above installed.

### Building

To build and install the library locally, run:

    $ lein test
    $ lein cljsbuild once
    $ lein install

### Including in your project

There is version hosted at [Clojars](https://clojars.org/rm-hull/inkspot).
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

A cross-platform ```IColor``` protocol has been overlaid on several common
color representations. For example, in Clojure:

```clojure
(color/coerce 0xFF0000)
=> #<Color java.awt.Color[r=255,g=0,b=0]>

(color/coerce "#00FF00")
=> #<Color java.awt.Color[r=0,g=255,b=0]>

(color/coerce "rgb(0,0,255)")
=> #<Color java.awt.Color[r=0,g=0,b=255]>

(color/coerce java.awt.Color/MAGENTA)
=> #<Color java.awt.Color[r=255,g=0,b=255]>

(color/coerce [255 255 0])
=> #<Color java.awt.Color[r=255,g=255,b=0]>
```

Specifying the color by keyword is also possible, with unknown colors returning
```nil``` as might be expected. The full list of supported color names can be
found [here](https://github.com/rm-hull/inkspot/blob/master/src/inkspot/color_chart/lindsay.clj).

```clojure
(color/coerce :bisque)
=> #<Color java.awt.Color[r=255,g=228,b=196]>

(color/coerce :none-existent-color)
=> nil
```

For ClojureScript, the same calls would instead yield a string representation,
thus:

```clojure
(color/coerce :bisque)
=> "rgba(255,228,196,1.0)"
```

### Built-in color swatches

There are a number of built-in swatches which can be used,

| Function | Color Palette |
|:---------|:------|
| color-chart/web-safe-colors | ![Web-safe](https://raw.github.com/rm-hull/inkspot/master/example/palette/web-safe-colors.png) |
| color-chart/spectrum | ![Spectrum](https://raw.github.com/rm-hull/inkspot/master/example/palette/spectrum.png) |
| color-chart/rainbow | ![Rainbow](https://raw.github.com/rm-hull/inkspot/master/example/palette/rainbow.png) |
| color-chart.lindsay/swatch | ![Lindsay](https://raw.github.com/rm-hull/inkspot/master/example/palette/lindsay.png) |
| color-chart.cc/gradient :orange :blue 216 | ![gradient1](https://raw.github.com/rm-hull/inkspot/master/example/palette/gradient1.png) |
| color-chart.cc/gradient :red :snow 216 | ![gradient2](https://raw.github.com/rm-hull/inkspot/master/example/palette/gradient2.png) |

These palettes were generated with the following
[example](https://github.com/rm-hull/inkspot/blob/master/example/example.clj):

```clojure
(ns inkspot.examples
  (require [clojure.java.io :as io]
           [inkspot.color :as color]
           [inkspot.color-chart :as cc]
           [inkspot.color-chart.lindsay :as lindsay])
  (import [javax.imageio ImageIO]))

(let [palettes {
        :web-safe-colors (map color/coerce cc/web-safe-colors)
        :spectrum        (cc/spectrum 216)
        :rainbow         (cc/rainbow 216)
        :lindsay         (map color/coerce (vals lindsay/swatch))
        :gradient1       (cc/gradient :orange :blue 216)}]
  (doseq [[k v] palettes
        :let [f (io/file (str "example/palette/" (name k) ".png"))]]
    (ImageIO/write (cc/create-palette v) "png" f)))
```

### Mixing Colors
TODO

### Cycling Colors
TODO

### Linear mapping across numeric ranges

A color swatch can be mapped to a specific range of numbers (integer or floating
point), where for each given input in the range, the nearest color is selected.
For example,

```clojure
(use 'inkspot.color-chart)

(def colors
  (color-mapper
    (spectrum 48)
    100 250))

(colors 100) ; lower bound is inclusive
=> #<Color java.awt.Color[r=224,g=0,b=0]>

(colors 249.99)
=> #<Color java.awt.Color[r=110,g=0,b=180]>

(colors 250) ; upper bound is exclusive
=> nil

(color 43) ; outside range
=> nil
```

## TODO

* ~~Web-safe colors~~
* ~~Spectral colors~~
* ~~IColor protocol~~
* ~~Color mapper function - given a numerical range and a color swatch, maps numerical input to the range of colors~~
* ~~Color averaging/mixing~~
* Create ~~PNG~~ & SVG swatch representations
* Logarithmic color mapper function
* Import LUT [maps](https://github.com/rm-hull/webrot/tree/master/resources/private/maps)
* Gradient interpolation: Use HSV values rather than RGB interpolation?

## Known Bugs

* CLJS files not generating properly

## References

* http://catless.ncl.ac.uk/Lindsay/swatch0.html
* http://www.lynda.com/resources/hexpalette/hue.html

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
