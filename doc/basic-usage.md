# Basic Usage

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
`nil` as might be expected. The full list of supported color names can be
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
