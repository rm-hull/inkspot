# Inkspot [![Build Status](https://travis-ci.org/rm-hull/inkspot.svg?branch=master)](http://travis-ci.org/rm-hull/inkspot) [![Dependencies Status](https://jarkeeper.com/rm-hull/inkspot/status.svg)](https://jarkeeper.com/rm-hull/inkspot) [![Downloads](https://jarkeeper.com/rm-hull/inkspot/downloads.svg)](https://jarkeeper.com/rm-hull/inkspot)
A small Clojure/ClojureScript library for creating colour swatches and converting between colorspaces.

### Pre-requisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.4.2 or above installed.

### Building

This repo now incorporates [uiGradients](https://github.com/Ghosh/uiGradients)
as a submodule, so run the following after cloning:

    $ cd  inkspot
    $ git submodule init
    $ git submodule update

To build and install the library locally, run:

    $ lein test
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

See the [API Documentation](http://rm-hull.github.io/inkspot).

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
found in the [lindsay](https://github.com/rm-hull/inkspot/blob/master/src/inkspot/color_chart/lindsay.clj)
and [X11](https://github.com/rm-hull/inkspot/blob/master/src/inkspot/color_chart/x11.clj) files.
*Notes:* (1) The lindsay list is checked first, and if the color name is not present,
it is then checked for in the X11 list. (2) the coerce lookup is case insensitive.

```clojure
(color/coerce :bisque)
=> #<Color java.awt.Color[r=255,g=228,b=196]>

(color/coerce :BISQUE)
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

#### Distinct Color swatches

| Function | Color Palette |
|:---------|:------|
| color-chart/web-safe-colors | ![Web-safe](https://raw.github.com/rm-hull/inkspot/master/example/palette/distinct/web-safe-colors.png) |
| color-chart.lindsay/swatch | ![Lindsay](https://raw.github.com/rm-hull/inkspot/master/example/palette/distinct/lindsay.png) |
| color-chart.x11/swatch | ![X11](https://raw.github.com/rm-hull/inkspot/master/example/palette/distinct/x11.png) |

#### Interpolated Color swatches

| Function | Color Palette |
|:---------|:------|
| color-chart/spectrum | ![Spectrum](https://raw.github.com/rm-hull/inkspot/master/example/palette/interpolated/spectrum.png) |
| color-chart/rainbow | ![Rainbow](https://raw.github.com/rm-hull/inkspot/master/example/palette/interpolated/rainbow.png) |
| color-chart/hue | ![Hue](https://raw.github.com/rm-hull/inkspot/master/example/palette/interpolated/hue.png) |
| color-chart/gradient :orange :blue 216 | ![gradient1](https://raw.github.com/rm-hull/inkspot/master/example/palette/interpolated/gradient1.png) |
| color-chart/gradient :red :snow 216 | ![gradient2](https://raw.github.com/rm-hull/inkspot/master/example/palette/interpolated/gradient2.png) |
| color-chart/heatmap 216 | ![heatmap](https://raw.github.com/rm-hull/inkspot/master/example/palette/interpolated/heatmap.png) |
| color-chart/cube-helix 216 | ![cube-helix](https://raw.github.com/rm-hull/inkspot/master/example/palette/interpolated/cube-helix.png) |

These palettes were generated with the following
[example](https://github.com/rm-hull/inkspot/blob/master/example/example.clj):

```clojure
(ns inkspot.examples
  (require [clojure.java.io :as io]
           [inkspot.color :as color]
           [inkspot.color-chart :as cc]
           [inkspot.palette :as palette]
           [inkspot.color-chart.lindsay :as lindsay]
           [inkspot.color-chart.x11 :as x11])
  (import [javax.imageio ImageIO]))

;; Distinct Color Swatches
(doseq [[k v] {:web-safe-colors (map color/coerce cc/web-safe-colors)
               :lindsay         (map color/coerce (vals lindsay/swatch))
               :x11             (map color/coerce (vals x11/swatch))}
        :let [f (io/file (str "example/palette/" (name k) ".png"))]]
  (ImageIO/write (palette/draw v :g2d-target palette/bitmap) "png" f))

;; Interpolated Color Swatches
(doseq [[k v] {:spectrum   (cc/spectrum 216)
               :rainbow    (cc/rainbow 216)
               :gradient1  (cc/gradient :orange :blue 216)
               :gradient2  (cc/gradient :red :snow 216)
               :heatmap    (cc/heatmap 216)
               :cube-helix (cc/cube-helix 216)}
        :let [f (io/file (str "example/palette/" (name k) ".png"))]]
  (ImageIO/write (palette/draw v :g2d-target palette/bitmap
                                 :cell-width 2 :cell-height 50
                                 :cells-per-row 216 :border 0) "png" f))
```

### uiGradients

The [gradients.json](https://github.com/Ghosh/uiGradients/blob/master/gradients.json)
from _uiGradients_ is loaded in (via a macro in clojurescript),
and interpolated color swatches can be generated by specifying the color name. For example:

```clojure
; Names can be specified as the named strings or kebab-case
(cc/ui-gradient :sea-blizz 240)
=> (#<Color java.awt.Color[r=28,g=216,b=210]> #<Color java.awt.Color[r=28,g=216,b=209]> ...

(cc/ui-gradient "Sea Blizz" 240)
=> (#<Color java.awt.Color[r=28,g=216,b=210]> #<Color java.awt.Color[r=28,g=216,b=209]> ...
```
| Name | Color Palette |
|:---------|:------|
| :a-lost-memory | ![a-lost-memory](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/a-lost-memory.png) |
| :almost | ![almost](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/almost.png) |
| :amethyst | ![amethyst](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/amethyst.png) |
| :aqua-marine | ![aqua-marine](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/aqua-marine.png) |
| :aqualicious | ![aqualicious](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/aqualicious.png) |
| :army | ![army](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/army.png) |
| :ash | ![ash](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/ash.png) |
| :aubergine | ![aubergine](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/aubergine.png) |
| :autumn | ![autumn](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/autumn.png) |
| :behongo | ![behongo](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/behongo.png) |
| :bloody-mary | ![bloody-mary](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/bloody-mary.png) |
| :blurry-beach | ![blurry-beach](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/blurry-beach.png) |
| :bora-bora | ![bora-bora](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/bora-bora.png) |
| :bourbon | ![bourbon](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/bourbon.png) |
| :calm-darya | ![calm-darya](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/calm-darya.png) |
| :candy | ![candy](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/candy.png) |
| :cheer-up-emo-kid | ![cheer-up-emo-kid](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/cheer-up-emo-kid.png) |
| :cherry | ![cherry](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/cherry.png) |
| :cherryblossoms | ![cherryblossoms](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/cherryblossoms.png) |
| :clouds | ![clouds](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/clouds.png) |
| :dance-to-forget | ![dance-to-forget](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/dance-to-forget.png) |
| :day-tripper | ![day-tripper](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/day-tripper.png) |
| :dirty-fog | ![dirty-fog](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/dirty-fog.png) |
| :dracula | ![dracula](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/dracula.png) |
| :earthly | ![earthly](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/earthly.png) |
| :electric-violet | ![electric-violet](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/electric-violet.png) |
| :emerald-water | ![emerald-water](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/emerald-water.png) |
| :facebook-messenger | ![facebook-messenger](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/facebook-messenger.png) |
| :forever-lost | ![forever-lost](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/forever-lost.png) |
| :frozen | ![frozen](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/frozen.png) |
| :horizon | ![horizon](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/horizon.png) |
| :influenza | ![influenza](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/influenza.png) |
| :jonquil | ![jonquil](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/jonquil.png) |
| :juicy-orange | ![juicy-orange](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/juicy-orange.png) |
| :kashmir | ![kashmir](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/kashmir.png) |
| :kyoto | ![kyoto](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/kyoto.png) |
| :lemon-twist | ![lemon-twist](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/lemon-twist.png) |
| :man-of-steel | ![man-of-steel](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/man-of-steel.png) |
| :mango-pulp | ![mango-pulp](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/mango-pulp.png) |
| :mantle | ![mantle](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/mantle.png) |
| :miaka | ![miaka](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/miaka.png) |
| :midnight-city | ![midnight-city](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/midnight-city.png) |
| :mirage | ![mirage](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/mirage.png) |
| :misty-meadow | ![misty-meadow](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/misty-meadow.png) |
| :mojito | ![mojito](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/mojito.png) |
| :moonrise | ![moonrise](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/moonrise.png) |
| :moor | ![moor](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/moor.png) |
| :moss | ![moss](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/moss.png) |
| :mystic | ![mystic](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/mystic.png) |
| :namn | ![namn](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/namn.png) |
| :neon-life | ![neon-life](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/neon-life.png) |
| :opa | ![opa](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/opa.png) |
| :parklife | ![parklife](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/parklife.png) |
| :peach | ![peach](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/peach.png) |
| :petrichor | ![petrichor](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/petrichor.png) |
| :pinky | ![pinky](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/pinky.png) |
| :pinot-noir | ![pinot-noir](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/pinot-noir.png) |
| :purple-paradise | ![purple-paradise](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/purple-paradise.png) |
| :red-mist | ![red-mist](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/red-mist.png) |
| :reef | ![reef](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/reef.png) |
| :rose-water | ![rose-water](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/rose-water.png) |
| :sea-blizz | ![sea-blizz](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/sea-blizz.png) |
| :sea-weed | ![sea-weed](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/sea-weed.png) |
| :shadow-night | ![shadow-night](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/shadow-night.png) |
| :shore | ![shore](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/shore.png) |
| :shrimpy | ![shrimpy](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/shrimpy.png) |
| :shroom-haze | ![shroom-haze](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/shroom-haze.png) |
| :sirius-tamed | ![sirius-tamed](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/sirius-tamed.png) |
| :soundcloud | ![soundcloud](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/soundcloud.png) |
| :starfall | ![starfall](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/starfall.png) |
| :steel-gray | ![steel-gray](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/steel-gray.png) |
| :stellar | ![stellar](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/stellar.png) |
| :sunrise | ![sunrise](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/sunrise.png) |
| :teal-love | ![teal-love](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/teal-love.png) |
| :the-strain | ![the-strain](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/the-strain.png) |
| :titanium | ![titanium](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/titanium.png) |
| :vasily | ![vasily](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/vasily.png) |
| :venice-blue | ![venice-blue](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/venice-blue.png) |
| :virgin | ![virgin](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/virgin.png) |
| :winter | ![winter](https://raw.github.com/rm-hull/inkspot/master/example/palette/ui-gradients/winter.png) |

The JSON file is packaged into the inkspot jar, but the uiGradients project is referenced as a git submodule and the
intention is to keep it mostly up-to-date as new inkspot releases are published.

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

(colors 43) ; outside range
=> nil
```

### Converting IColor instances to other colorspaces and back

The ```inkspot.converter``` namespace supports the following conversions to/from
```IColor``` instances. In all cases the non-RGB colorspace results are always
returned as vector of 3 elements.

* [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) (Hue, Saturation, Value)
* [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) (Hue, Saturation, Luminosity)
* [Y'UV](https://en.wikipedia.org/wiki/YUV) (Luma, Chrominance as used in PAL video standards)

```clojure
(use 'inkspot.converter)

(rgb->hsv :yellow)
=> [60.0 1.0 1.0]

(hsv->rgb [60.0 1.0 1.0])
=> #<Color java.awt.Color[r=255,g=255,b=0]>
```

## TODO

* ~~Web-safe colors~~
* ~~Spectral colors~~
* ~~IColor protocol~~
* ~~Color mapper function - given a numerical range and a color swatch, maps numerical input to the range of colors~~
* ~~Color averaging/mixing~~
* ~~Create PNG & SVG swatch palette representations (& add custome height, width, border options)~~
* Logarithmic color mapper function
* Import LUT [maps](https://github.com/rm-hull/webrot/tree/master/resources/private/maps)
* ~~Gradient interpolation: Use HSV values rather than RGB interpolation?~~ not necessary
* ~~RGB, HSV, HSL, YUV,~~ YIQ colorspace conversions
* ~~X11 Color names~~
* Monochrome/triadic/tetradic schemes
* ~~CubeHelix schemes~~
* ~~Embed uiGradients~~

## Known Bugs

* ~~CLJS files not generating properly~~

## References

* http://catless.ncl.ac.uk/Lindsay/swatch0.html
* http://www.lynda.com/resources/hexpalette/hue.html
* https://github.com/xav/Grapefruit
* http://www.mcfedries.com/books/cightml/x11color.htm
* http://www.mrao.cam.ac.uk/~dag/CUBEHELIX/
* https://github.com/Ghosh/uiGradients

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
